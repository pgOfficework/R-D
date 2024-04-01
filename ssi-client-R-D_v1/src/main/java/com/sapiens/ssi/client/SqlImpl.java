package com.sapiens.ssi.client;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sapiens.ssi.bean.SSIBean;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.source.SourceFactory;
import com.sapiens.ssi.source.SourceInterface;
import com.sapiens.ssi.sql.core.connection.ConnectionPoolInstance;

import ssi_nrt_kafkaconsumer_2sept2021.ssi_core_interface_0_1.SSI_Core_Interface;


public class SqlImpl implements ClientInterface{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(SqlImpl.class);

	/*
	start ssi clients of sources like kafka ,files .. and events of source system like contact,policy,cliams..
	 */
	@Override
	public void callClient(Map<String, Object> configSSI, String ssiType) {

		Map<String, LinkedHashMap<String, Object>> configSource = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		configSource.putAll((LinkedHashMap<String, LinkedHashMap<String, Object>>)configSSI.get(SSIConstant.source));
		log.trace("configSSI : "+configSSI);

		//proccessing parallely the ssi source
		configSource.entrySet().parallelStream().
		forEach(src -> {
			log.trace("processing source type : "+src.getKey());

			//proccessing parallely the ssi source events
			((LinkedHashMap<String, Object>) src.getValue()).entrySet().parallelStream().forEach(event -> {
				SourceInterface srcdb = SourceFactory.getSource(src.getKey());
				if(srcdb!=null) {
					srcdb.startSSI(((Map<String, Object>)configSSI.get(SSIConstant.target)),((Map<String, Object>)event.getValue()), ssiType, src.getKey());
				}
			});

		});
	}
	/*
	process payload of consumed data streamed by clients 
	 */
	@Override
	public void processPayload(Map<String, Object> payload) {
		//Auditing SSI_Real_time_MQ_Log
		Connection auditConn = ConnectionPoolInstance.getTrgtPoolObj(payload.get(SSIConstant.Target_Audit_JDBC_URL).toString(), payload.get(SSIConstant.Target_Audit_User_Id).toString(), payload.get(SSIConstant.Target_Audit_Password).toString())
				.getConnection();

		//generate mq audit load query
		String mqAuditQuery = "insert into "+payload.get(SSIConstant.Target_Audit_Schema_Nm)+".SIT_Real_time_MQ_Log (MQ_ID,Event_ID,Event_Type,MQ_Start_Timestamp,MQ_End_Timestamp,Status,Status_Desc,MQ_Core_TimeStamp) values "
				+ "('"+payload.get(SSIConstant.MQ_ID)+"','"+payload.get(SSIConstant.Event_Id)+"','"+payload.get(SSIConstant.Event_type)+"','"+payload.get(SSIConstant.mqStartTimeStamp)+"','"+payload.get(SSIConstant.mqEndTimeStamp)+"',3,'Completed','"+payload.get(SSIConstant.mqCoreTimeStamp)+"')";

		mqAuditQuery = mqAuditQuery.replace("'null'","null");

		log.info("MQ LOG sql query : "+mqAuditQuery);

		Statement mqAuditStmt = null;
		try {
			mqAuditStmt = auditConn.createStatement();

			mqAuditStmt.executeUpdate(mqAuditQuery);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		finally {
			try {
				if(mqAuditStmt!=null & !mqAuditStmt.isClosed()) {
					mqAuditStmt.close();
				}
				if(auditConn!=null & !auditConn.isClosed())
					auditConn.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		SSI_Core_Interface ssi =  new SSI_Core_Interface();

		log.debug("processing payload for sqlType : "+payload);
		
		//add payload data to ssi sql core job contexts
		List<String> arrList =new LinkedList<String>();
		for (Entry<String, Object> configKV : payload.entrySet()) {
			arrList.add("--context_param "+configKV.getKey()+"="+configKV.getValue());

		}
		// stream ssi sql core job contexts
		String[] contextLoad = arrList.stream().toArray(String[]::new);
		log.trace("arrList : "+arrList);
		log.info("contextLoad length : "+contextLoad.length);

		//run ssi sql core job
		ssi.runJob(contextLoad);

	}
}

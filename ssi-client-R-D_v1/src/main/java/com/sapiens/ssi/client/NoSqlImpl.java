package com.sapiens.ssi.client;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sapiens.ssi.SSIApplication;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.CoreFactory;
import com.sapiens.ssi.noSql.core.SSICoreInterface;
import com.sapiens.ssi.noSql.core.connection.MongoConnection;
import com.sapiens.ssi.noSql.core.transformer.config.JntTransformationLoad;
import com.sapiens.ssi.source.SourceFactory;
import com.sapiens.ssi.source.SourceInterface;

/*
no sql client implementation
 */
public class NoSqlImpl implements ClientInterface{

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(NoSqlImpl.class);

	/*
	start ssi clients of sources like kafka ,files .. and events of source system like contact,policy,cliams..
	 */
	@Override
	public void callClient(Map<String, Object> configSSI, String ssiType) {
		try {
			
			//Loading stored JNT transformations
			JntTransformationLoad.tLoad((Map<String, Object>) configSSI.get(SSIConstant.nosql));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//		System.out.println(configSSI.get("source"));
		Map<String, LinkedHashMap<String, Object>> configSource = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		configSource.putAll((LinkedHashMap<String, LinkedHashMap<String, Object>>)configSSI.get(SSIConstant.source));

		//proccessing parallely the ssi source
		configSource.entrySet().parallelStream().
		forEach(src -> {

			log.trace("processing source type : "+src.getKey());

			//proccessing parallely the ssi source events
			((LinkedHashMap<String, Object>) src.getValue()).entrySet().parallelStream().forEach(e -> {
				SourceInterface srcdb = SourceFactory.getSource(src.getKey());
				if(srcdb!=null) {
					srcdb.startSSI(((Map<String, Object>)configSSI.get(SSIConstant.target)),((Map<String, Object>)e.getValue()), ssiType, e.getKey());
				}
			});

		});
	}

	/*
	process payload of consumed data streamed by clients 
	 */
	@Override
	public void processPayload(Map<String, Object> payload) {

		log.debug(" payload data : "+payload);
		//Auditing SSI_Real_time_MQ_Log
		String uriTarget = payload.get(SSIConstant.Target_Database_Uri).toString();//"mongodb+srv://SapiensDBA:Sapiens123@datahub-freecluster.b0ho1.mongodb.net/test?retryWrites=true&w=majority";
		String dbTarget = payload.get(SSIConstant.Target_Audit_Database_Name).toString();//"R&D_NoSQL";

		MongoClient mongoClient = MongoConnection.getConnection(uriTarget);
		MongoDatabase database = mongoClient.getDatabase(dbTarget);


		MongoCollection<Document> auditCollection  = database.getCollection(SSIConstant.SSI_Real_time_MQ_Log); 

		//generate mq audit load document
		Document mqAuditDocument;
		try {

			mqAuditDocument = new Document("_id", getNextSequence("auditmqid",payload))
					.append(SSIConstant.mqID, payload.get(SSIConstant.MQ_ID))
					.append(SSIConstant.eventId, payload.get(SSIConstant.Event_Id))
					.append(SSIConstant.eventType, payload.get(SSIConstant.Event_type))
					.append(SSIConstant.mqStartTimeStamp, payload.get(SSIConstant.mqStartTimeStamp))
					.append(SSIConstant.mqEndTimeStamp, payload.get(SSIConstant.mqEndTimeStamp))
					.append(SSIConstant.mqCoreTimeStamp, payload.get(SSIConstant.mqCoreTimeStamp))
					.append(SSIConstant.status, 3)
					.append(SSIConstant.statusDesc, "completed");

			log.debug("mqAuditDocument : "+mqAuditDocument);

			auditCollection.insertOne(mqAuditDocument);

			log.info(" Audit MQ log : Committed");
		} catch (Exception e) {
			mongoClient.close();
			log.error(e.getMessage());
			e.printStackTrace();
		}

		try {
			//run ssi no-sql core job
			SSICoreInterface ssi = CoreFactory.getFormat(payload.get(SSIConstant.Format_type).toString());

			log.info("processing payload for nosql type : "+payload);
			if(ssi!=null) {
				ssi.runCoreJob(payload);
			}
		} catch (Exception e) {
			log.error("Error while processing : "+e.getLocalizedMessage());
			
			ssiCoreAuditError(payload,e);
			
			log.info("calling reprocessor of source type : "+payload.get(SSIConstant.Source_type));
			SourceInterface srcdb = SourceFactory.getSource(payload.get(SSIConstant.Source_type).toString());
			
			if(srcdb!=null) {
				srcdb.reproceser(payload,e);
			}
			
			//e.printStackTrace();
		}
	}

	
	private void ssiCoreAuditError(Map<String, Object> payload, Exception e) {

		String uriTarget = payload.get(SSIConstant.Target_Database_Uri).toString();
		String dbTarget = payload.get(SSIConstant.Target_Audit_Database_Name).toString();

		MongoClient mongoClient = MongoConnection.getConnection(uriTarget);
		MongoDatabase database = mongoClient.getDatabase(dbTarget);

		MongoCollection<Document> coreAuditCollection  = database.getCollection(SSIConstant.SSI_Real_time_Audit_log); 
		Document mqcoreAuditDocument;
		try {
			mqcoreAuditDocument = new Document(SSIConstant._id, getNextSequence("auditcoremqid",payload))
					.append(SSIConstant.mqID, payload.get(SSIConstant.MQ_ID))
					.append(SSIConstant.eventId, payload.get(SSIConstant.Event_Id))
					.append(SSIConstant.eventType, payload.get(SSIConstant.Event_type))
					.append(SSIConstant.eventStartTimeStamp, payload.get(SSIConstant.eventStartTimeStamp))
					.append(SSIConstant.eventEndTimeStamp, payload.get(SSIConstant.eventEndTimeStamp))
					.append(SSIConstant.status, 2)
					.append(SSIConstant.statusDesc, SSIConstant.ErrorEncoutered);

			log.debug("mqAuditDocument : "+mqcoreAuditDocument);
			coreAuditCollection.insertOne(mqcoreAuditDocument);

			log.info(" core Audit MQ log : Committed");
		} catch (Exception e1) {
			e1.printStackTrace();
		}		
	}

	private Object getNextSequence(String name, Map<String, Object> payload) throws Exception{
		String uriTarget = payload.get("Target_Database_Uri").toString();//"mongodb+srv://SapiensDBA:Sapiens123@datahub-freecluster.b0ho1.mongodb.net/test?retryWrites=true&w=majority";
		String dbTarget = payload.get("Target_Audit_Database_Name").toString();//"R&D_NoSQL";

		MongoClient mongoClient = MongoConnection.getConnection(uriTarget);
		MongoDatabase database = mongoClient.getDatabase(dbTarget);

		MongoCollection<Document> collection  = database.getCollection("counters");
		Document find = new Document();
		find.put("_id", name);
		Document update = new Document();
		update.put("$inc", new BasicDBObject("seq", 1));

		Document obj =  collection.findOneAndUpdate(find, update);
		log.info(" mq seq obj : "+obj);
		if(obj == null) {
			collection.insertOne(new Document("_id",name).append("seq", 2));
			return 1;
		}
		else
			return obj.get("seq");

	}


}

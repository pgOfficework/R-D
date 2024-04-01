package com.sapiens.ssi.noSql.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;

import com.api.jsonata4java.expressions.EvaluateException;
import com.api.jsonata4java.expressions.ParseException;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.connection.MongoConnection;
import com.sapiens.ssi.noSql.core.ingest.IngestFactory;
import com.sapiens.ssi.noSql.core.ingest.IngestInterface;
import com.sapiens.ssi.noSql.core.transformer.JsonHandler;
import com.sapiens.ssi.noSql.core.transformer.config.JNTConfig;
import com.sapiens.ssi.noSql.core.transformer.register.Register;


public class JsonImpl implements SSICoreInterface{

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(JsonImpl.class);
	//run core job of format type json
	@Override
	public void runCoreJob(Map<String, Object> payload)  {
		payload.put(SSIConstant.eventStartTimeStamp, convertTime(System.currentTimeMillis()));

		Register.CustFunc(payload);

		log.info("custum functions registered");
		String tranformResult = null;
		try {
			
//			int a = 1/0;
			switch (payload.get(SSIConstant.Source_type).toString().toLowerCase()) {
			case SSIConstant.kafka:
				String inputJson = payload.get(SSIConstant.source_data).toString();
				tranformResult = JsonHandler.jsonTransform(inputJson, JNTConfig.JNTConfigMap.get(payload.get(SSIConstant.Event_type).toString()),payload);

				break;
			case SSIConstant.file:
				File inputFile = new File(payload.get(SSIConstant.inputFilePath).toString());
				tranformResult = JsonHandler.jsonTransform(inputFile, JNTConfig.JNTConfigMap.get(payload.get(SSIConstant.Event_type).toString()),payload);

				break;

			default:
				break;
			}

			IngestInterface ingest = IngestFactory.getDB(payload.get(SSIConstant.Target_Database).toString());

			log.info(payload.get("SSI_Key"));
			log.debug( "tranformResult : "+tranformResult);
			if(ingest!=null) {
				ingest.load(tranformResult,payload);
			}
			payload.put(SSIConstant.eventEndTimeStamp, convertTime(System.currentTimeMillis()));


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
						.append(SSIConstant.status, 3)
						.append(SSIConstant.statusDesc, "completed");

				log.debug("mqAuditDocument : "+mqcoreAuditDocument);
				coreAuditCollection.insertOne(mqcoreAuditDocument);

				log.info(" core Audit MQ log : Committed");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (ParseException | IOException | EvaluateException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		log.info(payload.get("SSI_Key")+ " process Audit ..");

	}
	public static Object getNextSequence(String name, Map<String, Object> payload) throws Exception{
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

	public static String convertTime(long time){
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(date);
	}
}

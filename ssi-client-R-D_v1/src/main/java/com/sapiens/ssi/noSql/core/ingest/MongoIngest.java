package com.sapiens.ssi.noSql.core.ingest;

import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.connection.MongoConnection;
import com.sapiens.ssi.source.FileImpl;


public class MongoIngest implements IngestInterface {
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(MongoIngest.class);

	private static String uriTarget = null;
	private static String dbTarget = null;

	private static MongoClient mongoClient = null;
	private static MongoDatabase targetDatabase = null;

	public void load(String tranformResult,  Map<String, Object> payload) {  
		//initialize mongoClient
		if(mongoClient == null) {
			uriTarget = payload.get(SSIConstant.Target_Database_Uri).toString();//"mongodb+srv://SapiensDBA:Sapiens123@datahub-freecluster.b0ho1.mongodb.net/test?retryWrites=true&w=majority";
			mongoClient = MongoConnection.getConnection(uriTarget);
		}
		//initialize targetDatabase
		if(targetDatabase == null) {
			dbTarget = payload.get(SSIConstant.dbTarget).toString();
			targetDatabase = mongoClient.getDatabase(dbTarget);
		}

	
		String collectionTarget = payload.get(SSIConstant.Event_type).toString();//"contact";
		MongoCollection<Document> collection  = targetDatabase.getCollection(collectionTarget); 
		
		//parse the transform document
		Document jsonDocument = Document.parse(tranformResult);


		//contactCollection.insertOne(documentContact);
		//upsert operation 
		Bson filter = Filters.eq(SSIConstant._id, jsonDocument.get(SSIConstant._id));
		ReplaceOptions options = new ReplaceOptions().upsert(true);

		collection.replaceOne(filter, jsonDocument, options);
		//		
		log.info(payload.get("SSI_Key")+" - "+payload.get(SSIConstant.Event_type).toString().toUpperCase()+" record loaded successfully : "+ jsonDocument.get(SSIConstant._id));
		System.out.println(payload.get("SSI_Key")+" - "+payload.get(SSIConstant.Event_type).toString().toUpperCase()+" record loaded successfully : "+ jsonDocument.get(SSIConstant._id));

	} 
}

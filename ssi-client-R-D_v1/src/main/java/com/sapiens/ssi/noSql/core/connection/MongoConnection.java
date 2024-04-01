package com.sapiens.ssi.noSql.core.connection;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
	static MongoClient mongoClient =null ;
	
	public static MongoClient getConnection(String uriTarget) {
		if(mongoClient==null) {
//			System.out.println("mongoClient");
			ConnectionString connectionString = new ConnectionString(uriTarget);
			MongoClientSettings settings = MongoClientSettings.builder()
					.applyConnectionString(connectionString)
					.build();
			mongoClient = MongoClients.create(settings);
		}
//		MongoDatabase database = mongoClient.getDatabase("R&D_NoSQL");
		return mongoClient;
	}
	
	public static void closeConnection() {
		mongoClient.close();
	}
	
}

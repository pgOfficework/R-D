package com.sapiens.dba.dao;

import java.util.Arrays;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sapiens.dba.config.SearchAppConstants;
import com.sapiens.dba.connection.MongoClientConnectionPool;
import com.sapiens.dba.pojo.ClaimList;
import com.sapiens.dba.pojo.Contact;
import com.sapiens.dba.pojo.PolicyList;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class GenericSearch {

	@Autowired
	MongoClientConnectionPool mongoClientConnection;

	@Value("${targetDB}")
	private String targetDB;

	private static MongoClient mongoClient = null;
	private static MongoDatabase database = null;
	private static MongoCollection<Document> cust360;

	// fetching search result based on parameter
	// i.e arg=string to be search, sort=1 for asc & -1 for desc, limit= limiting
	// number of records
	public String getSearch(String arg, Long sort, Long limit) {

		JSONArray contacts = new JSONArray();
		JSONArray policies = new JSONArray();
		JSONArray claims = new JSONArray();

		log.info("\t getting Search result from database");
		if (mongoClient == null) {
			log.debug("Initialising mongoCilent from mongoCilentPool");
			mongoClient = mongoClientConnection.getMongoClient();
		}
		if (database == null) {
			log.debug("Initialising database for GenericSearch");
			database = mongoClient.getDatabase(targetDB);
		}
		if (cust360 == null) {
			log.debug("Initialising collection to perform mongoAtlas Search Queries");
			cust360 = database.getCollection(SearchAppConstants.cust360);
		}

		JSONObject allResults = new JSONObject();

		// try to get search result from contactNumber, _id, telephoneNumber,
		// policy.policyNumber,claim.claimNumber
		AggregateIterable<Document> cust360Result = cust360.aggregate(Arrays.asList(
				new Document("$search",
						new Document("index", "cust360").append("phrase",
								new Document("query", arg).append("path",
										Arrays.asList("contactNumber", "_id", "telephoneNumber", "policy.policyNumber",
												"claim.claimNumber")))),
				new Document("$sort", new Document("contactNumber", sort)), new Document("$limit", limit)));

		// try to get search result from fullName
		if ((cust360Result.cursor().available()) == 0) {
			cust360Result = cust360.aggregate(Arrays.asList(new Document("$match", new Document("fullName", arg)),
					new Document("$sort", new Document("fullName", sort)), new Document("$limit", limit)));
		}

		// try to get search result from email
		if ((cust360Result.cursor().available()) == 0) {
			cust360Result = cust360.aggregate(Arrays.asList(new Document("$match", new Document("email", arg)),
					new Document("$sort", new Document("email", sort)), new Document("$limit", limit)));
		}

		// structurising each object of cust360Result
		for (Document document : cust360Result) {

			Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create();
			Contact contact = gson.fromJson(document.toJson(), Contact.class);
			PolicyList policy = gson.fromJson(document.toJson(), PolicyList.class);
			ClaimList claim = gson.fromJson(document.toJson(), ClaimList.class);

			try {
				// structurising policy object
				JSONObject policyJsonObject = new JSONObject(policy.toString());
				JSONArray jsonArray = policyJsonObject.getJSONArray(SearchAppConstants.policy);
				if (!jsonArray.isNull(0)) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						policies.put(jsonObject);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				// structurising claim object
				JSONObject ClaimJsonObject = new JSONObject(claim.toString());
				JSONArray jsonArray = ClaimJsonObject.getJSONArray(SearchAppConstants.claim);
				if (!jsonArray.isNull(0)) {
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObject = jsonArray.getJSONObject(i);

						claims.put(jsonObject);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				log.debug("contact : " + contact);
				//// structurising contact object
				JSONObject contactJsonObject = new JSONObject(contact.toString());
				log.debug("jsonObjectContact : " + contactJsonObject);
				contacts.put(contactJsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
		try {
			// adding structured object into parent hashmap object to get desired result
			allResults.put(SearchAppConstants.contacts, contacts);
			allResults.put(SearchAppConstants.policies, policies);
			allResults.put(SearchAppConstants.claims, claims);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		log.debug("Search result : \n\t contacts : " + contacts.length() + "\t policies : " + policies.length()
				+ "\t claims : " + claims.length());
		return allResults.toString();

	}
}

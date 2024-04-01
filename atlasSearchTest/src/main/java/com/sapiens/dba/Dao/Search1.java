package com.sapiens.dba.Dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Search1 {
	public static MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
			.applyConnectionString(
					new ConnectionString("mongodb+srv://SapiensDBA:Sapiens123@datahub-freecluster.b0ho1.mongodb.net/"))
			.applyToSocketSettings(builder -> builder.connectTimeout(5, TimeUnit.SECONDS)).build());;
	public static MongoDatabase database = mongoClient.getDatabase("R&D_NoSQL");
	public static MongoCollection<Document> policy = database.getCollection("policy");
	public static MongoCollection<Document> claim = database.getCollection("claim");
	public static MongoCollection<Document> contact = database.getCollection("contact");
	public static MongoCollection<Document> cust360 = database.getCollection("cust360");

	// public static void main(String[] args) {
	// getSearch("test test test", 1L); // 9359309532
	// }


	
	public static HashMap<String, List<Document>> getSearch(String arg) {

		// JSONObject allResults = new JSONObject();

		HashMap<String, List<Document>> allResults = new HashMap<>();

		// ArrayList<String> allResults = new ArrayList<>();

		System.out.println("in getSearch");
		ArrayList<Document> out = new ArrayList<>();

		// checking search possibilities in policy collection
		AggregateIterable<Document> policyResult = policy
				.aggregate(Arrays.asList(new Document("$search",
						new Document("index", "policy").append("text",
								new Document("query", arg).append("path",
										Arrays.asList("policyNumber", "policyHolder.emails.email",
												"policyHolder.telephoneNumbers.telephoneNumber",
												"policyHolder.firstName"))))));

		List<Document> policy = new ArrayList<>();

		for (Document document : policyResult) {
			policy.add(document);
			// out.add(document);
		}
		allResults.put("policies", policy);
		// System.out.println(allResults);

		System.out.println("policies : " + policy.size());

		// checking search possibilities in claim collection
		AggregateIterable<Document> claimResult = claim
				.aggregate(Arrays.asList(new Document("$search", new Document("index", "claim").append("text",
						new Document("query", arg).append("path", "claimNumber")))));

		List<Document> claim = new ArrayList<>();

		for (Document document : claimResult) {
			claim.add(document);
			// out.add(document);
		}
		allResults.put("claims", claim);
		// System.out.println(allResults);
		System.out.println("claims : " + claim.size());

		// checking search possibilities in quote collection
		System.out.println(arg);

		AggregateIterable<Document> contactResult = contact.aggregate(Arrays.asList(
				new Document("$search", new Document("index", "contact").append("text", new Document("query", arg)
						.append("path", Arrays.asList("_id", "fullName", "telephoneNumbers.telephoneNumber"))))));

		List<Document> contact = new ArrayList<>();

		for (Document document : contactResult) {
			contact.add(document);
			// out.add(document);
		}
		allResults.put("contact", contact);
		// System.out.println(allResults);

		AggregateIterable<Document> cust360Result = cust360
				.aggregate(Arrays.asList(new Document("$search",
						new Document("index", "cust360").append("text",
								new Document("query", arg).append("path", Arrays.asList("contactId", "dateOfBirth",
										"email", "fullName", "telephoneNumber", "policy._id", "policy.policyId",
										"policy.policyNumber", "claim._id", "claim.claimId", "claim.claimNumber"))))));

		List<Document> cust360 = new ArrayList<>();

		for (Document document : cust360Result) {
			cust360.add(document);
			// out.add(document);
		}
		allResults.put("cust360", cust360);
		// System.out.println(allResults);

		System.out.println(allResults);
		return allResults;
	}
}

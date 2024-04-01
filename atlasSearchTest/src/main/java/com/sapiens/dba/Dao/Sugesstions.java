package com.sapiens.dba.Dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Sugesstions {
	public static MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()
			.applyConnectionString(
					new ConnectionString("mongodb+srv://SapiensDBA:Sapiens123@datahub-freecluster.b0ho1.mongodb.net/"))
			.applyToSocketSettings(builder -> builder.connectTimeout(5, TimeUnit.SECONDS)).build());;
	public static MongoDatabase database = mongoClient.getDatabase("R&D_NoSQL");
	public static MongoCollection<Document> policy = database.getCollection("policy");
	public static MongoCollection<Document> claim = database.getCollection("claim");
	public static MongoCollection<Document> contact = database.getCollection("contact");

	public static AggregateIterable<Document> policyResult1 = null;
	public static AggregateIterable<Document> policyResult2 = null;
	public static AggregateIterable<Document> claimResult = null;
	public static AggregateIterable<Document> contactResult = null;
	public static AggregateIterable<Document> contactResult1 = null;

	// @BeforeAll
	// public static void setUpDB() throws IOException {
	// mongoClient = MongoClients.create();
	// database = mongoClient.getDatabase("atlassearch");
	// collection = database.getCollection("policy");
	// }
	//
	 public static void main(String[] args) {
	 getSugesstions("1000161"); // 9359309532
	 }

	public static HashSet<String> getSugesstions(String arg) {
		System.out.println("in getSugesstions");

		HashSet<String> out=new HashSet(); 

		// from policy getting policyNumber, policyHolder.emails.email
		// .aggregate(Arrays.asList(new Document("$search", new Document("index",
		// "policy") .append("compound", new Document("should", Arrays.asList(new
		// Document("autocomplete", new Document("query", "888") .append("path",
		// "policyNumber")), new Document("autocomplete", new Document("query", "888")
		// .append("path", "policyHolder.emails.email")), new Document("autocomplete",
		// new Document("query", "888") .append("path",
		// "policyHolder.telephoneNumbers.telephoneNumber")), new
		// Document("autocomplete", new Document("query", "rqal") .append("path",
		// "policyHolder.firstName")))))), new Document("$project", new Document("_id",
		// 0L) .append("policyNumber", 1L) .append("policyHolder,emails.email", 1L)
		// .append("policyHolder.telephoneNumbers.telephoneNumber", 1L)
		// .append("policyHolder.firstName", 1L)), new Document("$limit", 10L)));

		// getting Sugesstions from policy collection for policyNumber field
		policyResult1 = policy
				.aggregate(
						Arrays.asList(
								new Document("$search",
										new Document("index", "policy").append("compound", new Document("should",
												Arrays.asList(new Document("autocomplete",
														new Document("query", arg).append("path", "policyNumber")))))),

								new Document("$sort", new Document("policyNumber", 1L)),
								// new Document("$project", new Document("_id", 0L).append("policyNumber", 1L)
								// .append("policyHolder.firstName", 1L)),
								new Document("$limit", 10L)));
		// adding result into list
		for (Document document : policyResult1) {
			out.add(document.get("policyNumber").toString());
		}

		// getting Sugesstions from policy collection for telephoneNumber, email fields
		policyResult2 = policy
				.aggregate(Arrays.asList(
						new Document("$search",
								new Document("index", "policy").append("compound",
										new Document("should", Arrays.asList(
												new Document("autocomplete",
														new Document("query", arg).append("path",
																"policyHolder.emails.email")),
												new Document("autocomplete",
														new Document("query", arg).append("path",
																"policyHolder.telephoneNumbers.telephoneNumber")))))),
						new Document("$sort", new Document("policyHolder.telephoneNumbers.telephoneNumber", 1L)),
						new Document("$project",
								new Document("policyHolder.emails.email", 1L)
										.append("policyHolder.telephoneNumbers.telephoneNumber", 1L)),
						new Document("$limit", 10L)));

		// sorting result according to field and adding it into the list
		if (Character.isDigit(arg.charAt(0))) {
			for (Document document : policyResult2) {
				out.add(((Document) ((Document) document.get("policyHolder")).get("telephoneNumbers"))
						.get("telephoneNumber").toString());
			}
		} else {
			for (Document document : policyResult2) {
				out.add(((Document) ((Document) document.get("policyHolder")).get("emails")).get("email").toString());
			}
		}

		// getting Sugesstions from claim collection for claimNumber field
		claimResult = claim
				.aggregate(
						Arrays.asList(
								new Document("$search",
										new Document("index", "claim").append("compound", new Document("should",
												Arrays.asList(new Document("autocomplete",
														new Document("query", arg).append("path", "claimNumber")))))),
								new Document("$project", new Document("_id", 0L).append("claimNumber", 1L)),
								new Document("$limit", 10L)));

		// adding result into list
		for (Document document : claimResult) {
			out.add(document.get("claimNumber").toString());
		}

		// getting Sugesstions from quote collection for quoteNumber field
		contactResult = contact
				.aggregate(Arrays.asList(
						new Document("$search",
								new Document("index", "contact").append("compound",
										new Document("should", Arrays.asList(
												new Document("autocomplete",
														new Document("query", arg).append("path",
																"telephoneNumbers.telephoneNumber")),
												new Document("autocomplete",
														new Document("query", arg).append("path", "fullName")))))),
						new Document("$project",
								new Document("fullName", 1L).append("telephoneNumbers.telephoneNumber", 1L)),
						new Document("$limit", 10L)));

		if (Character.isDigit(arg.charAt(0))) {
			for (Document document : contactResult) {
				out.add(((Document) document.get("telephoneNumbers")).get("telephoneNumber").toString());
			}
		} else {
			for (Document document : contactResult) {
				out.add(document.get("fullName").toString());
			}
		}

		contactResult1 = contact
				.aggregate(
						Arrays.asList(
								new Document("$search",
										new Document("index", "contact").append("compound", new Document("should",
												Arrays.asList(new Document("autocomplete",
														new Document("query", arg).append("path", "_id")))))),
								new Document("$project", new Document("_id", 1L)), new Document("$limit", 10L)));
		
		for (Document document : contactResult1) {
			out.add(document.get("_id").toString());
		}

		System.out.println(out);
		return out;
	}

}

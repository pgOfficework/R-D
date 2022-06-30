package com.sapiens.dba.dao;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sapiens.dba.config.SearchAppConstants;
import com.sapiens.dba.connection.MongoClientConnectionPool;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class Sugesstions {

	@Value("${targetDB}")
	private String targetDB;

	@Autowired
	MongoClientConnectionPool mongoClientConnection;

	private static MongoClient mongoClient = null;
	private static MongoDatabase database = null;

	private static AggregateIterable<Document> cust360Result;
	private static AggregateIterable<Document> policyResult;
	private static AggregateIterable<Document> claimResult;

	private static MongoCollection<Document> policy;
	private static MongoCollection<Document> claim;
	private static MongoCollection<Document> cust360;

	public Set<String> getSugesstions(String arg) {
		log.info("\t getting Sugesstions from database");

		if (mongoClient == null) {
			log.debug("Initialising mongoCilent from mongoCilentPool");
			mongoClient = mongoClientConnection.getMongoClient();
		}
		if (database == null) {
			log.debug("Initialising database for GenericSearch");
			database = mongoClient.getDatabase(targetDB);
		}
		if (policy == null || claim == null || cust360 == null) {
			log.debug("Initialising collection to perform mongoAtlas Search Queries");
			policy = database.getCollection(SearchAppConstants.policy);
			claim = database.getCollection(SearchAppConstants.claim);
			cust360 = database.getCollection(SearchAppConstants.cust360);
		}

		Set<String> out = new LinkedHashSet<>();

		// try to get suggestions from fullName and contactNumber
		cust360Result = cust360.aggregate(Arrays.asList(new Document("$search",
				new Document("index", "cust360").append("compound", new Document("should", Arrays
						.asList(new Document("autocomplete", new Document("query", arg).append("path", "contactNumber")
				// keeping below commented code so if fuzzy functionality needs to be activate
				// .append("fuzzy",new Document("prefixLength", 3L))
				), new Document("autocomplete",
						new Document("query", arg).append("path", "fullName").append("fuzzy",
								new Document("maxEdits", 1L).append("prefixLength", 1L))))))),
				new Document("$project",
						new Document("fullName", 1L).append("contactNumber", 1L).append("_id", 0L).append("score",
								new Document("$meta", "searchScore"))),
				new Document("$sort", new Document("score", -1L)), new Document("$limit", 10L)));

		// adding result into list
		if (!arg.matches(".*[a-zA-Z].*")) {
			for (Document document : cust360Result) {
				out.add(document.get(SearchAppConstants.contactNumber).toString());
			}
		} else if (!arg.contains("@")) {

			for (Document document : cust360Result) {
				out.add(document.get(SearchAppConstants.fullName).toString());
			}

		}

		// try to get suggestions from email and telephoneNumber
		cust360Result = cust360.aggregate(Arrays.asList(new Document("$search",
				new Document("index", "cust360").append("compound", new Document("should", Arrays.asList(
						new Document("autocomplete", new Document("query", arg).append("path", "telephoneNumber")
						// keeping below commented code so if fuzzy functionality needs to be activate
						// .append("fuzzy",new Document("maxEdits", 1L).append("prefixLength",1L))
						), new Document("autocomplete", new Document("query", arg).append("path", "email")
						// keeping below commented code so if fuzzy functionality needs to be activate
						// .append("fuzzy", new Document("maxEdits", 1L).append("prefixLength",1L))
						))))),
				new Document("$project",
						new Document("telephoneNumber", 1L).append("email", 1L).append("_id", 0L).append("score",
								new Document("$meta", "searchScore"))),
				new Document("$sort", new Document("score", -1L)), new Document("$limit", 10L)));

		// adding result into list
		if (!arg.matches(".*[a-zA-Z].*")) {
			for (Document document : cust360Result) {
				out.add(document.get(SearchAppConstants.telephoneNumber).toString());
			}
		} else {

			for (Document document : cust360Result) {
				out.add(document.get(SearchAppConstants.email).toString());
			}

		}

		// try to get suggestions from policyNumber
		policyResult = policy.aggregate(Arrays.asList(new Document("$search", new Document("index", "policy")
				.append("autocomplete", new Document("query", arg).append("path", "policyNumber")
		// keeping below commented code so if fuzzy functionality needs to be activate
		// .append("fuzzy",new Document("prefixLength", 3L))
		)), new Document("$project",
				new Document("_id", 0L).append("policyNumber", 1L).append("score",
						new Document("$meta", "searchScore"))),
				new Document("$sort", new Document("score", -1L)), new Document("$limit", 10L)));

		// adding result into list
		for (Document document : policyResult) {
			out.add(document.get(SearchAppConstants.policyNumber).toString());
		}

		// try to get suggestions from claimNumber
		claimResult = claim.aggregate(Arrays.asList(new Document("$search", new Document("index", "claim")
				.append("autocomplete", new Document("query", arg).append("path", "claimNumber")
		// keeping below commented code so if fuzzy functionality needs to be activate
		// .append("fuzzy",new Document("prefixLength", 3L))
		)), new Document("$project",
				new Document("_id", 0L).append("claimNumber", 1L).append("score",
						new Document("$meta", "searchScore"))),
				new Document("$sort", new Document("score", -1L)), new Document("$limit", 10L)));

		// adding result into list
		for (Document document : claimResult) {
			out.add(document.get(SearchAppConstants.claimNumber).toString());
		}

		log.debug("Suggessions : " + out);

		return out;
	}

}

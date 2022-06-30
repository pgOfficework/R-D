package com.sapiens.dba.dao;

import java.util.Arrays;

import org.bson.BsonNull;
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
public class GenericSearchMQL {

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

		// try to get search result for "contactNumber","fullName", "_id",
		// "telephoneNumber", "email", "policy.policyNumber","claim.claimNumber"

		log.info("\t Searching for "+arg);


		AggregateIterable<Document> cust360Result = cust360.aggregate(Arrays.asList(new Document("$search",
				new Document("index", "cust360").append("text",
						new Document("query", arg).append("path", Arrays.asList("contactNumber", "fullName", "_id",
								"telephoneNumber", "email", "policy.policyNumber", "claim.claimNumber"))
				// .append("fuzzy", new Document("maxEdits", 1L).append("prefixLength",
				// prefixLength))
				)),
				new Document("$project", new Document("_id", 0L)
						.append("contacts",
								new Document("contactId", "$contactId").append("contactNumber", "$contactNumber")
										.append("dateOfBirth", "$dateOfBirth").append("email", "$email")
										.append("fullName", "$fullName").append("gender", "$gender")
										.append("telephoneNumber", "$telephoneNumber")
										.append("noOfClaims", "$noOfClaims").append("totalPremium", "$totalPremium"))
						.append("claims", "$claim").append("policies", "$policy")),
				new Document("$group",
						new Document("_id", new BsonNull()).append("contacts", new Document("$push", "$contacts"))
								.append("policies", new Document("$push", "$policies"))
								.append("claims", new Document("$push", "$claims"))),
				new Document("$project", new Document("_id", 0L).append("contacts", "$contacts")
						.append("policies", new Document("$reduce",
								new Document("input", "$policies").append("initialValue", Arrays.asList()).append("in",
										new Document("$concatArrays", Arrays.asList("$$value", "$$this")))))
						.append("claims",
								new Document("$reduce",
										new Document("input", "$claims").append("initialValue", Arrays.asList()).append(
												"in",
												new Document("$concatArrays", Arrays.asList("$$value", "$$this"))))))));

		log.debug("***** : "+cust360Result.cursor().available());

		if (!((cust360Result.cursor().available()) == 0)) {
			return cust360Result.first().toJson();
		}
		return null;

	}

}

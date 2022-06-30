package com.sapiens.dba.dao;

import java.util.Arrays;

import org.bson.BsonNull;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mongodb.MongoConfigurationException;
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

		// try to get search result for 'contactNumber', 'fullName', '_id',
		// 'telephoneNumber', 'email', 'policy.policyNumber', 'claim.claimNumber'

		AggregateIterable<Document> cust360Result = null;
			cust360Result = cust360.aggregate(Arrays.asList(
					new Document("$search",
							new Document("index", "cust360").append("phrase",
									new Document("query", arg).append(
											"path",
											Arrays.asList(
													"contactNumber", "fullName", "_id", "telephoneNumber", "email",
													"policy.policyNumber", "claim.claimNumber")))),
					new Document("$project", new Document("_id", 0L)
							.append("contacts", new Document("contactName",
									new Document("$ifNull", Arrays.asList("$fullName", new BsonNull())))
											.append("contactNumber",
													new Document("$ifNull",
															Arrays.asList("$contactNumber", new BsonNull())))
											.append("contactId",
													new Document("$ifNull",
															Arrays.asList("$contactId", new BsonNull())))
											.append("dateOfBirth",
													new Document(
															"$ifNull", Arrays.asList("$dateOfBirth", new BsonNull())))
											.append("maritalStatus",
													new Document("$ifNull",
															Arrays.asList("$maritalStatus", new BsonNull())))
											.append("contactStatus",
													new Document("$ifNull",
															Arrays.asList("$contactStatus", new BsonNull())))
											.append("contactAddress", new Document("$ifNull",
													Arrays.asList(new Document("addressLine1", new Document("$ifNull",
															Arrays.asList("$addresses.addressLine1", new BsonNull())))
																	.append("addressLine2", new Document("$ifNull",
																			Arrays.asList("$addresses.addressLine2",
																					new BsonNull())))
																	.append("city",
																			new Document("$ifNull", Arrays.asList(
																					"$addresses.city", new BsonNull())))
																	.append("state", new Document("$ifNull",
																			Arrays.asList("$addresses.state",
																					new BsonNull())))
																	.append("zipCode", new Document("$ifNull",
																			Arrays.asList("$addresses.zipCode",
																					new BsonNull()))),
															new BsonNull())))
											.append("activeClaims",
													new Document("$ifNull",
															Arrays.asList("$noOfClaims", new BsonNull()))))
							.append("policies", new Document("$map", new Document("input", "$policy")
									.append("as", "policy")
									.append("in", new Document("policyNumber", new Document("$ifNull",
											Arrays.asList("$$policy.policyNumber", new BsonNull()))).append(
													"policyStatus",
													new Document("$ifNull",
															Arrays.asList("$$policy.policyStatus", new BsonNull())))
													.append("policyEndDate",
															new Document("$ifNull",
																	Arrays.asList("$$policy.endDate", new BsonNull())))
													.append("yearlyPremium",
															new Document("$ifNull",
																	Arrays.asList("$$policy.totalPremium",
																			new BsonNull())))
													.append("lobDescription",
															new Document("$ifNull",
																	Arrays.asList("$$policy.lobDescription",
																			new BsonNull())))
													.append("policyHolderName",
															new Document("$ifNull", Arrays.asList(
																	"$$policy.policyHolderName", new BsonNull())))
													.append("assetDetails", new Document("assetDescription",
															new Document("$ifNull", Arrays.asList(
																	"$$policy.assetDetails.assetDescription",
																	new BsonNull()))).append(
																			"plateNumber",
																			new Document("$ifNull", Arrays.asList(
																					"$$policy.assetDetails.plateNumber",
																					new BsonNull())))))))
							.append("claims", new Document("$map",
									new Document("input", "$claim").append("as", "claim").append("in",
											new Document("claimNumber", new Document("$ifNull", Arrays.asList(
													"$$claim.claimNumber", new BsonNull()))).append("claimStatus",
															new Document("$ifNull",
																	Arrays.asList("$$claim.claimStatus",
																			new BsonNull())))
															.append("claimDate",
																	new Document("$ifNull", Arrays.asList(
																			"$$claim.eventDate", new BsonNull())))
															.append("claimAmount",
																	new Document("$ifNull", Arrays.asList(
																			"$$claim.claimAmount", new BsonNull())))
															.append("claimDescription", new Document("$ifNull", Arrays
																	.asList("$$claim.claimEventDescription",
																			new BsonNull())))
															.append("lobDescription",
																	new Document("$ifNull", Arrays.asList(
																			"$$claim.lobDescription", new BsonNull())))
															.append("policyHolderName", new Document("$ifNull",
																	Arrays.asList("$$claim.policyHolderName",
																			new BsonNull())))
															.append("assetDetails", new Document("assetDescription",
																	new Document("$ifNull", Arrays.asList(
																			"$$claim.assetDetails.assetDescription",
																			new BsonNull()))).append(
																					"plateNumber",
																					new Document("$ifNull",
																							Arrays.asList(
																									"$$claim.assetDetails.plateNumber",
																									new BsonNull())))))))),
					new Document("$limit", limit),
					new Document("$group",
							new Document("_id", new BsonNull()).append("contacts", new Document("$push", "$contacts"))
									.append("policies", new Document("$push", "$policies"))
									.append("claims", new Document("$push", "$claims"))),
					new Document("$project", new Document("_id", 0L).append("contacts", "$contacts")
							.append("policies", new Document("$reduce",
									new Document("input", "$policies").append("initialValue", Arrays.asList()).append(
											"in", new Document("$concatArrays", Arrays.asList("$$value", "$$this")))))
							.append("claims", new Document("$reduce",
									new Document("input", "$claims").append("initialValue", Arrays.asList()).append(
											"in", new Document("$concatArrays", Arrays.asList("$$value", "$$this")))))),
					new Document("$sort", new Document("contacts.contactNumber", sort))));

		 log.debug("cust360Result : " + cust360Result.cursor().available());
		if(cust360Result.cursor().available()==0) {
			return "null";
		}

		return cust360Result.first().toJson();

	}
	
}

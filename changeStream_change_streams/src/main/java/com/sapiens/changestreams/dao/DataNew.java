package com.sapiens.changestreams.dao;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.logging.Logger;

/**
 * Just as imple singleton for all DAO classes as this example code does not use any DPI
 */
public class DataNew {
    public static MongoClient mongoClient;
    public static MongoDatabase db;


    public static void initDAL(String mongoURI,String database) {
        LOGGER.info("connecting to MongoDB "+mongoURI);
        mongoClient= MongoClients.create(mongoURI);
        DataNew.db=mongoClient.getDatabase(database);

    }


    private static final Logger LOGGER = Logger.getLogger("DATA_ACCESS_LAYER");
}

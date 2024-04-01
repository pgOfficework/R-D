package com.sapiens.changestreams.config;

import java.util.ArrayList;

public class ConfigData {
    private MongoDb mongodb;
    private String kafkaBootstrapServers;
    private ArrayList<ChangeStreams> changeStreams;

    public ArrayList<ChangeStreams> getChangeStreams() {
        return changeStreams;
    }

    public void setChangeStreams(ArrayList<ChangeStreams> changeStreams) {
        this.changeStreams = changeStreams;
    }

    public MongoDb getMongodb() {
        return mongodb;
    }

    public void setMongodb(MongoDb mongodb) {
        this.mongodb = mongodb;
    }

    public String getKafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }

    public void setKafkaBootstrapServers(String kafkaBootstrapServers) {
        this.kafkaBootstrapServers = kafkaBootstrapServers;
    }
}

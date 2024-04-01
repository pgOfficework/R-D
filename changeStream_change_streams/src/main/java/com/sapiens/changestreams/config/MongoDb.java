package com.sapiens.changestreams.config;

public class MongoDb {
    private String uri;
    private String database;
    private String changeStreamThreads;
    private String changeStreamMaxBatchSize;
    private String changeStreamMaxBatchDurationMS;


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getChangeStreamThreads() {
        return changeStreamThreads;
    }

    public void setChangeStreamThreads(String changeStreamThreads) {
        this.changeStreamThreads = changeStreamThreads;
    }

    public String getChangeStreamMaxBatchSize() {
        return changeStreamMaxBatchSize;
    }

    public void setChangeStreamMaxBatchSize(String changeStreamMaxBatchSize) {
        this.changeStreamMaxBatchSize = changeStreamMaxBatchSize;
    }

    public String getChangeStreamMaxBatchDurationMS() {
        return changeStreamMaxBatchDurationMS;
    }

    public void setChangeStreamMaxBatchDurationMS(String changeStreamMaxBatchDurationMS) {
        this.changeStreamMaxBatchDurationMS = changeStreamMaxBatchDurationMS;
    }
}

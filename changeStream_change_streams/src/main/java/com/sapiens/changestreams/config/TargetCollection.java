package com.sapiens.changestreams.config;

import java.util.ArrayList;
import java.util.Map;

public class TargetCollection {
    private String collName;
    private String completeDocument;
    private String match;
    private ArrayList<Map<String,String>> directAttributes;
    private Map<String,String> calculatedAttributes;
    private boolean kafkaPushInd;
    private String kafkaTopic;

    //public TargetCollection(){}



    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getCompleteDocument() {
        return completeDocument;
    }

    public void setCompleteDocument(String completeDocument) {
        this.completeDocument = completeDocument;
    }

    public ArrayList<Map<String, String>> getDirectAttributes() {
        return directAttributes;
    }

    public void setDirectAttributes(ArrayList<Map<String, String>> directAttributes) {
        this.directAttributes = directAttributes;
    }

    public Map<String, String> getCalculatedAttributes() {
        return calculatedAttributes;
    }

    public void setCalculatedAttributes(Map<String, String> calculatedAttributes) {
        this.calculatedAttributes = calculatedAttributes;
    }

    public boolean isKafkaPushInd() {
        return kafkaPushInd;
    }

    public void setKafkaPushInd(boolean kafkaPushInd) {
        this.kafkaPushInd = kafkaPushInd;
    }

    public String getKafkaTopic() {
        return kafkaTopic;
    }

    public void setKafkaTopic(String kafkaTopic) {
        this.kafkaTopic = kafkaTopic;
    }
}

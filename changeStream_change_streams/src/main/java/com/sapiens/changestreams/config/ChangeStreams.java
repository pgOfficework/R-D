package com.sapiens.changestreams.config;

import java.util.ArrayList;

public class ChangeStreams {
    private String auditCollections ;
    private String watchedCollection;
    private ArrayList<TargetCollection> targetCollections;
    private ArrayList<TargetCollection> targetViews;


   /* public ChangeStreams(String baseCollection, ArrayList<TargetCollection> targetCollections) {
        this.baseCollection = baseCollection;
        this.targetCollections = targetCollections;
    }*/

    public String getAuditCollections() {
        return auditCollections;
    }

    public void setAuditCollections(String auditCollections) {
        this.auditCollections = auditCollections;
    }

    public String getWatchedCollection() {
        return watchedCollection;
    }

    public void setWatchedCollection(String watchedCollection) {
        this.watchedCollection = watchedCollection;
    }

    public ArrayList<TargetCollection> getTargetCollections() {
        return targetCollections;
    }

    public void setTargetCollections(ArrayList<TargetCollection> targetCollections) {
        this.targetCollections = targetCollections;
    }

    public ArrayList<TargetCollection> getTargetViews() {
        return targetViews;
    }

    public void setTargetViews(ArrayList<TargetCollection> targetViews) {
        this.targetViews = targetViews;
    }
}

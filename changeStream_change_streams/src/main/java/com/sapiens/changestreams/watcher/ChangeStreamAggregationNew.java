package com.sapiens.changestreams.watcher;

import java.util.List;
import java.util.Map;

public class ChangeStreamAggregationNew {

    String baseCollection="";
    String targetCollection="";
    String match = "";
    List<Map<String,String>> directAttributes=null;
    Map<String,String> calculatedAttributes = null;
    Boolean kafkaPushInd;
    String kafkaTopic ;

 /*   public ChangeStreamAggregationNew(String baseCollection, String targetCollection, String match, List<String> directAttributes) {
        this.baseCollection = baseCollection;
        this.targetCollection = targetCollection;
        this.match = match;
        this.directAttributes = directAttributes;
    }*/

    public ChangeStreamAggregationNew(String baseCollection, String targetCollection, String match, List<Map<String,String>> directAttributes,
                                      Map<String, String> calculatedAttributes, Boolean kafkaPushInd, String kafkaTopic) {
        this.baseCollection = baseCollection;
        this.targetCollection = targetCollection;
        this.match = match;
        this.directAttributes = directAttributes;
        this.calculatedAttributes = calculatedAttributes;
        this.kafkaPushInd = kafkaPushInd;
        this.kafkaTopic = kafkaTopic;
    }
}

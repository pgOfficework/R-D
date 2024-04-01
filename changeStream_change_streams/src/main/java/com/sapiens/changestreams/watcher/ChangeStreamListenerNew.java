package com.sapiens.changestreams.watcher;

import com.api.jsonata4java.Expression;
import com.api.jsonata4java.expressions.ParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.ChangeStreamIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;
import com.mongodb.client.model.changestream.OperationType;
import com.mongodb.client.result.UpdateResult;
import com.sapiens.changestreams.dao.DataNew;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.bson.*;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.excludeId;

public class ChangeStreamListenerNew {
    Thread watchThread = null;

    HashMap<String, List<ChangeStreamAggregationNew>> changeStreamAggregations = new HashMap<>();
    HashMap<String, List<ChangeStreamAggregationNew>> changeStreamAggregationViews = new HashMap<>();
    // List<ChangeStreamHandler> changeStreamHandlers = new ArrayList<>();


    Executor handlerExecuter = null;
    static int maxBatchSize = 100;
    static int maxBatchTimeMS = 2000;
    // public static String auditCollections = null;
    static List<String> auditCollectionsList;

    AtomicInteger currentBatchSize = new AtomicInteger(0);
    Phaser phaser = new Phaser();
    long batchStartTime;

    public ChangeStreamListenerNew(int executerThreads, int maxBatchSize, int maxBatchTimeMS) {
        handlerExecuter = Executors.newFixedThreadPool(executerThreads);
        this.maxBatchSize = maxBatchSize;
        this.maxBatchTimeMS = maxBatchTimeMS;
    }

    public ChangeStreamListenerNew() {

    }


    // method to add each change stream
    public void addChangeStreamAggregation(String watchedCollection, String targetCollection, String match, List<Map<String, String>> directAttributes,
                                           Map<String, String> calculatedAttributes, Boolean kafkaPushInd, String kafkaTopic) {
        ChangeStreamAggregationNew changeStreamAggregationNew = new ChangeStreamAggregationNew(watchedCollection, targetCollection,
                match, directAttributes, calculatedAttributes, kafkaPushInd, kafkaTopic);
        List<ChangeStreamAggregationNew> aggregations = changeStreamAggregations.get(watchedCollection);
        if (aggregations == null) {
            aggregations = new ArrayList<>();
            changeStreamAggregations.put(watchedCollection, aggregations);
        }
        aggregations.add(changeStreamAggregationNew);
    }

    public void addChangeStreamAggregationViews(String watchedCollection, String targetCollection, String match, List<Map<String, String>> directAttributes,
                                                Map<String, String> calculatedAttributes, Boolean kafkaPushInd, String kafkaTopic) {
        ChangeStreamAggregationNew changeStreamAggregationViewsNew = new ChangeStreamAggregationNew(watchedCollection, targetCollection,
                match, directAttributes, calculatedAttributes, kafkaPushInd, kafkaTopic);
        List<ChangeStreamAggregationNew> aggregationViews = changeStreamAggregationViews.get(watchedCollection);
        if (aggregationViews == null) {
            aggregationViews = new ArrayList<>();
            changeStreamAggregationViews.put(watchedCollection, aggregationViews);
        }
        aggregationViews.add(changeStreamAggregationViewsNew);
    }


    public void watcher() {

        boolean run = true;
        ChangeStreamIterable<Document> changeStreamIterable = null;
        while (run) {

            // initiate DB watch the collections specified in the config
            if (changeStreamAggregations != null) {
                for (String baseCollection : changeStreamAggregations.keySet()) {

                    System.out.println("watching collection : " + baseCollection);
                    changeStreamIterable = DataNew.db.getCollection(baseCollection).watch().fullDocument(FullDocument.UPDATE_LOOKUP);
                }
            }
            MongoCursor<ChangeStreamDocument<Document>> mongoDocumentIterator = changeStreamIterable.iterator();
            // loop through the changes in document if any
            while (mongoDocumentIterator.hasNext()) {
                ChangeStreamDocument<Document> changedEventDoc = mongoDocumentIterator.next();
                handleChangeEvent(changedEventDoc);
            }
        }
    }

    public void startWatching() {
        Thread watchThread = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            BsonTimestamp operationTime = null;
                            try {
                                BsonDocument resumeToken = getResumeToken();
                                ChangeStreamIterable<Document> csi = null;
                                if (resumeToken == null) {
                                    Document pingResult = DataNew.db.runCommand(Projections.include("serverStatus"));
                                    if (pingResult == null) {
                                        LOGGER.severe("ping command did not return anything");
                                    }
                                    operationTime = (BsonTimestamp) pingResult.get("operationTime");
                                    if (operationTime == null) {
                                        LOGGER.severe("operationTime in ping result was null:  " + pingResult.toJson());
                                    }
                                    csi = DataNew.db.watch().startAtOperationTime(operationTime).fullDocument(FullDocument.UPDATE_LOOKUP);
                                } else {
                                    csi = DataNew.db.watch().resumeAfter(resumeToken).fullDocument(FullDocument.UPDATE_LOOKUP);
                                }
                                MongoCursor<ChangeStreamDocument<Document>> iter = csi.iterator();
                                batchStartTime = System.currentTimeMillis();
                                while (iter.hasNext()) {
                                    ChangeStreamDocument<Document> doc = iter.next();

                                    int batchSize = currentBatchSize.getAndIncrement();
                                    BsonTimestamp clusterTime = doc.getClusterTime();
                                    if (operationTime != null && clusterTime.compareTo(operationTime) < 0) {
                                        LOGGER.warning("first change event has a lower optime that operationTime");
                                    }
                                    //register new task to wait for in the phaser:
                                    phaser.register();
                                    //let the executer handle the processing:
                                    handlerExecuter.execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {

                                                handleChangeEvent(doc);
                                            } finally {
                                                phaser.arriveAndDeregister();
                                            }
                                        }
                                    });

                                    //decide if we want to do a checkpoint
                                    if (batchSize >= maxBatchSize ||
                                            (System.currentTimeMillis() - batchStartTime) > maxBatchTimeMS) {
                                        //the batch should be finished and a new batch started
                                        //wait for whole batch to finish:
                                        phaser.register();
                                        phaser.arriveAndAwaitAdvance();
                                        LOGGER.info("Change Stream handler: Checkpoint reached,  storing resume token");
                                        //reset batch and store resume token
                                        storeResumeToken(doc.getResumeToken());
                                        phaser.arriveAndDeregister();
                                        batchStartTime = System.currentTimeMillis();
                                        currentBatchSize.set(0);
                                    }
                                }
                            } catch (Exception ex) {
                                LOGGER.severe("Error in changeStream Watch Thread:" + ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                }
        );
        watchThread.start();
    }


    public void startWatchingNew() {
        boolean run = true;
        while (run) {
            BsonTimestamp operationTime = null;
            try {
                BsonDocument resumeToken = getResumeToken();
                ChangeStreamIterable<Document> csi = null;
                if (resumeToken == null) {
                    Document pingResult = DataNew.db.runCommand(Projections.include("serverStatus"));
                    if (pingResult == null) {
                        LOGGER.severe("ping command did not return anything");
                    }
                    operationTime = (BsonTimestamp) pingResult.get("operationTime");
                    if (operationTime == null) {
                        LOGGER.severe("operationTime in ping result was null:  " + pingResult.toJson());
                    }
                    csi = DataNew.db.watch().startAtOperationTime(operationTime).fullDocument(FullDocument.UPDATE_LOOKUP);
                } else {
                    csi = DataNew.db.watch().resumeAfter(resumeToken).fullDocument(FullDocument.UPDATE_LOOKUP);
                }
                MongoCursor<ChangeStreamDocument<Document>> iter = csi.iterator();
                batchStartTime = System.currentTimeMillis();

                //10 changed doc
                while (iter.hasNext()) {

                    ChangeStreamDocument<Document> doc = iter.next();

                    int batchSize = currentBatchSize.getAndIncrement();
                    BsonTimestamp clusterTime = doc.getClusterTime();
                    if (operationTime != null && clusterTime.compareTo(operationTime) < 0) {
                        LOGGER.warning("first change event has a lower optime that operationTime");
                    }
                    //register new task to wait for in the phaser:
                    phaser.register();
                    //let the executer handle the processing:

                    // create thread
                    handlerExecuter.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                handleChangeEvent(doc);
                            } finally {
                                phaser.arriveAndDeregister();
                            }
                        }
                    });

                    //decide if we want to do a checkpoint
                    if (batchSize >= maxBatchSize ||
                            (System.currentTimeMillis() - batchStartTime) > maxBatchTimeMS) {
                        //the batch should be finished and a new batch started
                        //wait for whole batch to finish:
                        phaser.register();
                        phaser.arriveAndAwaitAdvance();
                        LOGGER.info("Change Stream handler: Checkpoint reached,  storing resume token");
                        //reset batch and store resume token
                        storeResumeToken(doc.getResumeToken());
                        phaser.arriveAndDeregister();
                        batchStartTime = System.currentTimeMillis();
                        currentBatchSize.set(0);
                    }
                }
            } catch (Exception ex) {
                LOGGER.severe("Error in changeStream Watch Thread:" + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }


    private void handleChangeEvent(ChangeStreamDocument<Document> eventDoc) {

        // code to handle audit flag for all collection in the config
        // check if the changed doc is part of audit collections in config
        String collName = eventDoc.getNamespaceDocument().getString("coll").getValue();
        //String collName = eventDoc.getNamespaceDocument().getString("coll").getValue();
        if (auditCollectionsList.contains(collName)) {
            updateInsertUpdateFlag(eventDoc);
        }
        //check if the document change type and process accordingly
        if (eventDoc.getOperationType() != OperationType.INSERT &&
                eventDoc.getOperationType() != OperationType.UPDATE && eventDoc.getOperationType() != OperationType.REPLACE) {
            LOGGER.severe("UNSUPPORTED OP: " + eventDoc.getOperationType());
        } else {
            //process  aggregations
            processChangeStreamAggregations(collName, eventDoc.getFullDocument());
            //System.out.println("event processed");
        }


    }

    private void updateInsertUpdateFlag(ChangeStreamDocument<Document> eventDoc) {
        String collNameIU = eventDoc.getNamespaceDocument().getString("coll").getValue();
        Document baseDocIU = eventDoc.getFullDocument();


        MongoCollection<Document> updateColIU = DataNew.db.getCollection(collNameIU);
        //form search query, set query and update fields and update the matching docs in the collection
        BasicDBObject searchQueryIU = new BasicDBObject("_id", baseDocIU.get("_id"));
        BasicDBObject setQueryIU = new BasicDBObject();
        BasicDBObject updateFieldsIU = new BasicDBObject();

        if (eventDoc.getOperationType() == OperationType.INSERT) {
            updateColIU.replaceOne(searchQueryIU, baseDocIU.append("AUD_IU_FLAG", "0"));
        } else if (eventDoc.getOperationType() == OperationType.UPDATE) {
            updateFieldsIU.append("AUD_IU_FLAG", "1");
            setQueryIU.append("$set", updateFieldsIU);
            updateColIU.updateOne(searchQueryIU, setQueryIU);
        }

    }

    private void processChangeStreamAggregations(String collection, Document baseDoc) {
        //get the base collection and its aggregations
        MongoCollection<Document> baseColl = DataNew.db.getCollection(collection);
        List<ChangeStreamAggregationNew> aggregations = changeStreamAggregations.get(collection);
        List<ChangeStreamAggregationNew> aggregationViews = changeStreamAggregationViews.get(collection);
        // process multiple target collection as per config match
        if (changeStreamAggregations.containsKey(collection) && aggregations != null) {
            for (ChangeStreamAggregationNew changeStream : aggregations) {
                if (changeStream.directAttributes != null)
                    processChangeStreamDirectAttributes(baseDoc, changeStream, false);
                if (changeStream.calculatedAttributes != null)
                    processChangeStreamCalculatedAttributes(baseDoc, changeStream, false);

            }
        }
        // process target views as defined in the config
        if (changeStreamAggregationViews.containsKey(collection) && aggregationViews != null) {
            for (ChangeStreamAggregationNew changeStream : aggregationViews) {
                if (changeStream.directAttributes != null)
                    processChangeStreamDirectAttributes(baseDoc, changeStream, true);
                if (changeStream.calculatedAttributes != null)
                    processChangeStreamCalculatedAttributes(baseDoc, changeStream, true);

            }
        }

    }


    private void processChangeStreamDirectAttributes(Document baseDoc, ChangeStreamAggregationNew changeStream, Boolean viewFlag) {

        MongoCollection<Document> targetColl = DataNew.db.getCollection(changeStream.targetCollection);

        String targetMatch = changeStream.match.split(",")[1];
        System.out.println("target match : "+targetMatch);
        //System.out.println("base doc : "+baseDoc.toJson());
        //jsonTransform(baseDoc.toJson().toString(),targetMatch);
        String targetMatchValue = String.valueOf(baseDoc.get(changeStream.match.split(",")[0]));
        //String targetMatchValue = jsonTransform(baseDoc.toJson().toString(),targetMatch);

        //form search query, set query and update fields and update the matching docs in the collection
        BasicDBObject searchQuery = new BasicDBObject(targetMatch, targetMatchValue);
        BasicDBObject setQuery = new BasicDBObject();
        BasicDBObject updateFields = new BasicDBObject();
        //List<Bson> arrFilters = new ArrayList<>();
        //get direct attributes from base doc and add to update fields
        if (changeStream.directAttributes != null) {


            for (Map<String, String> directAttributesMap : changeStream.directAttributes) {
                //updateFields.append(directAttributesMap.get("targetField"), baseDoc.get(directAttributesMap.get("watchField")));
                updateFields.append(directAttributesMap.get("targetField"), jsonTransform(baseDoc.toJson().toString(),directAttributesMap.get("watchField")));

            }
            System.out.println("updatefields : "+updateFields.toString());

            //System.out.println("array filters :" + arrFilters);
            //System.out.println("update fields : " + updateFields);
            setQuery.append("$set", updateFields);

            if (viewFlag) {
                UpdateOptions updateOptions = new UpdateOptions().upsert(true);
                targetColl.updateMany(searchQuery, setQuery, updateOptions);
            } else {

                targetColl.updateMany(searchQuery, setQuery);
            }
        }

    }

    private void processChangeStreamCalculatedAttributes(Document baseDoc, ChangeStreamAggregationNew changeStream, Boolean viewFlag) {
        System.out.println("watched collection : "+changeStream.baseCollection);

        MongoCollection<Document> targetColl = DataNew.db.getCollection(changeStream.targetCollection);

        String calSearchQuery = changeStream.calculatedAttributes.get("matchQuery");
        String matchAttributes = changeStream.calculatedAttributes.get("matchAttributes");


        String[] matchAttributesList = matchAttributes.split(",");
        HashMap<String, Object> matchAttributesMap = new HashMap<String, Object>();

        //System.out.println("base doc : " + baseDoc.toJson().toString());
        //jsonTransform(baseDoc.toJson().toString(),"policyHolder.contactId");



        //get the matchAtrributes from change stream doc
        for (String matchAttribute : matchAttributesList) {
            matchAttributesMap.put(matchAttribute, jsonTransform(baseDoc.toJson().toString(),matchAttribute));
           // System.out.println("match attribute :" + matchAttribute);
            //System.out.println("match from doc : " + jsonTransform(baseDoc.toJson().toString(),matchAttribute));
        }
        System.out.println("cal query before : " + calSearchQuery);

        // replace the $$ variables in config match mql query with values from changed doc
        for (Map.Entry<String, Object> matchAttributeKeyValue : matchAttributesMap.entrySet()) {
            System.out.println("matchAttribute Key " + matchAttributeKeyValue.getKey());
            System.out.println("matchAttribute Value " + matchAttributeKeyValue.getValue());
            calSearchQuery = calSearchQuery.replace("##" + matchAttributeKeyValue.getKey(), matchAttributeKeyValue.getValue().toString());

        }
        System.out.println("cal query after: " + calSearchQuery);
        Document calSearchQueryDoc = Document.parse(calSearchQuery);

        for (Map.Entry<String, String> mqlquery : changeStream.calculatedAttributes.entrySet()) {

            if (!(mqlquery.getKey().equalsIgnoreCase("matchQuery")
                    || mqlquery.getKey().equalsIgnoreCase("matchAttributes"))) {


                if (mqlquery.getKey().equalsIgnoreCase("mqlUpdate")) {
                    //System.out.println("individual query :" + mqlquery);
                    String mqlUpdateQuery = mqlquery.getValue();
                    for (Map.Entry<String, Object> matchAttributeKeyValue : matchAttributesMap.entrySet()) {
                        System.out.println("matchAttributeKeyValue" + matchAttributeKeyValue.getKey());
                        mqlUpdateQuery = mqlUpdateQuery.replace("##" + matchAttributeKeyValue.getKey(), matchAttributeKeyValue.getValue().toString());

                    }
                    BsonArray pipelineQuery = BsonArray.parse(mqlUpdateQuery);
                    ArrayList<Bson> pipeline = new ArrayList<>();
                    for (BsonValue v : pipelineQuery.getValues()) {
                        pipeline.add(v.asDocument());
                    }
                       /* System.out.println("calSearchQueryDoc : " + calSearchQueryDoc);
                        System.out.println("pipeline : " + p);*/
                    if (viewFlag) {
                        UpdateOptions updateOptions = new UpdateOptions().upsert(true);

                        UpdateResult updateCount = targetColl.updateMany(calSearchQueryDoc, pipeline, updateOptions);
                        //targetColl.aggregate()

                        //System.out.println("updated count : " + updateCount.getUpsertedId());
                        System.out.println("kafka ind : " + changeStream.kafkaPushInd);
                    } else {
                        targetColl.updateMany(calSearchQueryDoc, pipeline);
                    }
                    if (changeStream.kafkaPushInd)
                        pushChangesToKafka(targetColl, calSearchQueryDoc, changeStream.kafkaTopic);

                    //targetColl.aggregate(p).toCollection();
                } else if (mqlquery.getKey().equalsIgnoreCase("mqlAggregate")) {

                    String mqlUpdateQuery = mqlquery.getValue();
                    for (Map.Entry<String, Object> matchAttributeKeyValue : matchAttributesMap.entrySet()) {
                        System.out.println("matchAttributeKeyValue" + matchAttributeKeyValue.getKey());
                        mqlUpdateQuery = mqlUpdateQuery.replace("##" + matchAttributeKeyValue.getKey(), matchAttributeKeyValue.getValue().toString());

                    }
                    BsonArray pipelineQuery = BsonArray.parse(mqlUpdateQuery);
                    ArrayList<Bson> pipeline = new ArrayList<>();
                    for (BsonValue v : pipelineQuery.getValues()) {
                        pipeline.add(v.asDocument());
                    }
                   // System.out.println("pipeline : "+pipeline);
                    targetColl.aggregate(pipeline).toCollection();

                    if (changeStream.kafkaPushInd)
                        pushChangesToKafka(targetColl, calSearchQueryDoc, changeStream.kafkaTopic);
                }
            }
        }

    }

    private void pushChangesToKafka(MongoCollection<Document> targetColl, Document calSearchQueryDoc, String kafkaTopic) {

        FindIterable<Document> changedDocs = targetColl.find(calSearchQueryDoc);
        Iterator changedDoc = changedDocs.iterator();
        while (changedDoc.hasNext()) {
            //System.out.println("message to be pushed to kafka : " + changedDoc.next());
            ProducerRecord<String, String> recordInsert = new ProducerRecord<String, String>(kafkaTopic, changedDoc.next().toString());

            ChangeStreamConfig.producer.send(recordInsert);
            ChangeStreamConfig.producer.flush();
            //ChangeStreamConfig.producer.close();
            //System.out.println("pushed to Kafka "+kafkaTopic);

        }

    }


    /**/

    /**
     * Retrieve the resumeToken from resumeTokensColl, based on the uuid
     *
     * @return the resumeToken
     */
    protected BsonDocument getResumeToken() {
        MongoCollection<BsonDocument> coll = DataNew.db.getCollection("resumeToken", BsonDocument.class);
        MongoCursor<BsonDocument> cursor = coll.find(eq("_id", "resumeToken")).limit(1).projection(excludeId()).iterator();
        return coll.find().limit(1).projection(excludeId()).first();
    }

    /**
     * Store resume tokens always
     *
     * @param resumeToken
     */
    protected void storeResumeToken(BsonDocument resumeToken) {
        // System.out.println("storing resume token");
        MongoCollection<BsonDocument> coll = DataNew.db.getCollection("resumeToken", BsonDocument.class);
        FindOneAndReplaceOptions opt = new FindOneAndReplaceOptions().upsert(true);
        coll.findOneAndReplace(eq("_id", "resumeToken"), resumeToken, opt);
    }

    private static final Logger LOGGER = Logger.getLogger("CHANGE_STREAM_LISTENER_NEW");

    public void addAuditCollections(String auditCollections) {
        auditCollectionsList = Arrays.asList(auditCollections.split(","));

    }

    public static String jsonTransform(String input, String jnExpr) {
        // log.info("handling tranform..");
        String jnRes = null;
        try {
//          int a = 1/0;
            //Generate a jsonata Expression based on evaluating the supplied expression
            Expression expression = Expression.jsonata(jnExpr);
            //read json data
            JsonNode obj = new ObjectMapper().readTree(input);
            //Generate a result form the Expression's parsed ssi config jsonata expression
            JsonNode result = expression.evaluate(obj);
            System.out.println("result  : "+result);
            jnRes = result.toString();
            System.out.println("jnres : "+jnRes);
            jnRes = jnRes.replaceAll("\"","");
            System.out.println("jnres after : "+jnRes);
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return jnRes;
    }
}




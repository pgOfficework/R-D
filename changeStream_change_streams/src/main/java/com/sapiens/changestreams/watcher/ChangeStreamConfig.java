package com.sapiens.changestreams.watcher;

import com.sapiens.changestreams.config.ChangeStreams;
import com.sapiens.changestreams.config.ConfigData;
import com.sapiens.changestreams.config.TargetCollection;
import com.sapiens.changestreams.dao.DataNew;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.errors.TopicExistsException;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class ChangeStreamConfig {
    static ChangeStreamListenerNew changeStreamListenerNew;
    public static KafkaProducer<String, String> producer;
    public static String auditCollections = null;

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Change Streams ");
        //read config files
        Constructor constructor = new Constructor(ConfigData.class);
        // need to specify nested list for YAML load
        TypeDescription configDescription = new TypeDescription(ConfigData.class);
        configDescription.putListPropertyType("changeStreams", ChangeStreams.class);
        constructor.addTypeDescription(configDescription);
        configDescription.putListPropertyType("targetCollections", TargetCollection.class);
        constructor.addTypeDescription(configDescription);
        configDescription.putListPropertyType("directAttributes", String.class);
        constructor.addTypeDescription(configDescription);
        TypeDescription configDescription2 = new TypeDescription(ConfigData.class);
        configDescription2.putMapPropertyType("calculatedAttributes", String.class, String.class);
        constructor.addTypeDescription(configDescription2);

        Yaml yaml = new Yaml(constructor);
        //InputStream inputStream = new FileInputStream("configNew.yaml");
        InputStream inputStream = new FileInputStream(args[0]);
        ConfigData configData = yaml.load(inputStream);
        // get Mongo DB connection
        DataNew.initDAL(configData.getMongodb().getUri(), configData.getMongodb().getDatabase());
        //changeStreamListenerNew = new ChangeStreamListenerNew();
        changeStreamListenerNew = new ChangeStreamListenerNew(
                Integer.valueOf(configData.getMongodb().getChangeStreamThreads()),
                Integer.valueOf(configData.getMongodb().getChangeStreamMaxBatchSize()),
                Integer.valueOf(configData.getMongodb().getChangeStreamMaxBatchDurationMS())
        );

        //get kafka server details

        // create producer configuration
        //String bootstrapServers = configData.getKafkaBootstrapServers();
        //KafkaNew.kafkaProps(bootstrapServers);
        Properties properties = new Properties();
        properties = loadConfig("kafkaConfigProps.properties");

        //producer= new KafkaProducer<String, String>(KafkaNew.kafkaProps(bootstrapServers));
       // producer = new KafkaProducer<String, String>(properties);

        //check if config has change streams and add to the aggregations

        if (configData.getChangeStreams().size() > 0) {


            for (ChangeStreams cs : configData.getChangeStreams()) {
                auditCollections = cs.getAuditCollections();
                if (auditCollections != null)
                    changeStreamListenerNew.addAuditCollections(auditCollections);
                if (cs.getTargetCollections() != null) {
                    for (TargetCollection tc : cs.getTargetCollections()) {
                        changeStreamListenerNew.addChangeStreamAggregation(cs.getWatchedCollection(), tc.getCollName(), tc.getMatch(), tc.getDirectAttributes(), tc.getCalculatedAttributes(), tc.isKafkaPushInd(), tc.getKafkaTopic());
                    }
                }
                if (cs.getTargetViews() != null) {
                    for (TargetCollection tc : cs.getTargetViews()) {
                        changeStreamListenerNew.addChangeStreamAggregationViews(cs.getWatchedCollection(), tc.getCollName(), tc.getMatch(), tc.getDirectAttributes(), tc.getCalculatedAttributes(), tc.isKafkaPushInd(), tc.getKafkaTopic());
                    }
                }
            }
        }
        //start watching
        //changeStreamListenerNew.watcher();
        changeStreamListenerNew.startWatchingNew();
    }


    public static Properties loadConfig(final String configFile) throws IOException {
        if (!Files.exists(Paths.get(configFile))) {
            throw new IOException(configFile + " not found.");
        }
        final Properties cfg = new Properties();
        try (InputStream inputStream = new FileInputStream(configFile)) {
            cfg.load(inputStream);
        }
        return cfg;
    }

    public static void createTopic(final String topic,
                                   final Properties cloudConfig, int numPartitions) throws InterruptedException, ExecutionException {

        final NewTopic newTopic = new NewTopic(topic, numPartitions, (short) 3);

        AdminClient admin = AdminClient.create(cloudConfig);

        boolean alreadyExists = admin.listTopics().names().get().stream()
                .anyMatch(existingTopicName -> existingTopicName.equalsIgnoreCase(topic.toString()));

        if (alreadyExists) {
            System.out.printf("topic already exits: %s%n", newTopic);
        } else {
            try (final AdminClient adminClient = AdminClient.create(cloudConfig)) {
                adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            } catch (final InterruptedException | ExecutionException e) {
                // Ignore if TopicExistsException, which may be valid if topic exists
                if (!(e.getCause() instanceof TopicExistsException)) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static final Logger LOGGER = Logger.getLogger("ChangeStreamConfig");


}

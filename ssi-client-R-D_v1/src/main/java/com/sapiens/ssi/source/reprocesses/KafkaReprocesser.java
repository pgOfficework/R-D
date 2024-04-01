package com.sapiens.ssi.source.reprocesses;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

import com.sapiens.ssi.bean.SSIBean;
import com.sapiens.ssi.client.ClientFactory;
import com.sapiens.ssi.client.ClientInterface;
import com.sapiens.ssi.config.kafka.RebalanceListner;
import com.sapiens.ssi.config.log.SSILogConfig;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.sql.core.connection.ConnectionPoolInstance;

//import ssi_nrt_configdata_4thoct.ssi_nrt_configdata_0_1.SSI_NRT_ConfigData;
import ssi_nrt_kafkaconsumer_2sept2021.ssi_core_interface_0_1.SSI_Core_Interface;


public class KafkaReprocesser {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(KafkaReprocesser.class);


	public static String convertTime(long time){
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(date);
	}


	private static KafkaConsumer<String, String> consumer ;


	//reprocess ssi of source type kafka
	public  void ssiReprocess(Map<String, Object> payload) {
		final Thread reprocessesThread = Thread.currentThread();
		log.info("reprocess-kafka-for-key-"+payload.get(SSIConstant.SSI_Key));
		ClientInterface cdb = ClientFactory.getSource(payload.get(SSIConstant.ssiType).toString());

		//		KafkaImpl.initialize(payload);
		String Topics= payload.get(SSIConstant.Topics).toString();

		//intialize kafka properties
		//		initialize(payload);

		String bootstrapServers= payload.get(SSIConstant.bootstrapServers).toString();  
		String grp_id= payload.get(SSIConstant.reprocess_grp_id).toString();

		Properties properties=new Properties();  
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);  
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());  
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());  
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,grp_id);  
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, SSIConstant.falseBool);
		//
		//		String kafkaResetOffsetConfig = payload.get(SSIConstant.kafka_Offset).toString(); 
		//
		//		if(!kafkaResetOffsetConfig.equals(SSIConstant.NA)) {
		//			properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,kafkaResetOffsetConfig);  
		//			log.info("set kafka offset : "+kafkaResetOffsetConfig);
		//		}

		String exclusionEvents = payload.get(SSIConstant.exclusion_events).toString();

//		List<String> TopicList=null;
		List<String> exclusionEventsList=null;

		//		RebalanceListner rebalanceListner = null;
		//		TopicList = Arrays.asList(Topics.split(","));
		exclusionEventsList = Arrays.asList(exclusionEvents.split(","));

		//declare audit parameters 
		StringBuilder ssiKey = new StringBuilder();
		String mqCoreTimeStamp = null;
		String mqStartTimeStamp = null;
		String mqEndTimeStamp = null;
		String MQ_ID = null;
		String eventType = null;
		String eventId = null;
		String eventInfo = null;
		String sourceSystem = payload.get(SSIConstant.source_system).toString();


		//		if(payload.containsKey(SSIConstant.Source_MD_CMS_Table_Name))
		//			payload.put(SSIConstant.Source_MD_CMS_Table_Name, payload.get(SSIConstant.Source_MD_CMS_Table_Name));

		// Registering a shutdown hook so we can exit cleanly
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				log.info("Starting reprocesses exit...");
				// Note that shutdownhook runs in a separate thread, so the only thing we can safely do to a consumer is wake it up
				consumer.wakeup();
				try {
					reprocessesThread.join();					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

		consumer = new KafkaConsumer<>(properties);

		log.info("reprocess topic : "+payload.get(SSIConstant.topic)+" partition:"+payload.get(SSIConstant.partition)+",offset : "+payload.get(SSIConstant.offset));

		log.info("reprocess grp id : "+payload.get(SSIConstant.reprocess_grp_id));
		TopicPartition topicPartition = new TopicPartition(payload.get(SSIConstant.topic).toString(), (int)payload.get(SSIConstant.partition));

		consumer.assign(Collections.singleton(topicPartition));

		log.info("seeking the reprocess offset of topic ",payload.get(SSIConstant.topic));
//		Set<TopicPartition> assignedPartitions = consumer.assignment();
//		consumer.seekToBeginning(assignedPartitions);
		consumer.seek(topicPartition, (long) payload.get(SSIConstant.offset));
		boolean isReprocessed = false;
		try {

			//			rebalanceListner = new RebalanceListner(consumer);
			//			consumer.subscribe(TopicList,rebalanceListner);


			//polling  
			while(true)	{

				ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(10000));

				log.trace("ConsumerRecords count :"+records.count());
				for(ConsumerRecord<String,String> record: records){
					isReprocessed = true;

					try {

						mqStartTimeStamp = convertTime(System.currentTimeMillis());
						mqCoreTimeStamp = convertTime(record.timestamp());
						payload.put(SSIConstant.mqStartTimeStamp, mqStartTimeStamp);
						payload.put(SSIConstant.mqCoreTimeStamp, mqCoreTimeStamp);
						
						
						eventId = null;
						eventType = null;

						log.info("Key: "+ record.key() );
						log.debug( "Value:" +record.value());  
						log.info("Partition:" + record.partition()+",Offset:"+record.offset()+",timestamp:"+mqCoreTimeStamp); 

						//define ssiKey for each record wrt core system

						MQ_ID = sourceSystem+"_Source_Data"+"#"+record.partition()+"#"+record.offset();

						switch (sourceSystem.toLowerCase()) {
						case SSIConstant.idit:{
							if(record.key() !=null && record.key().contains("_")) {
								String[] keySplit = record.key().split("_");
								eventType = keySplit[0]; //policy
								eventId = keySplit[1];   // policy id
								eventInfo = keySplit[2]+"_"+keySplit[3];
							}
							ssiKey.append(MQ_ID).append("&").append(eventType).append("&").append(eventId).append("&").append(eventInfo);
						}
						break;
						case SSIConstant.coresuite:{
							eventType=record.topic().substring(record.topic().lastIndexOf('.') + 1);
							eventId = record.key();
							ssiKey.append(MQ_ID).append("&").append(eventType).append("&").append(eventId);
						}
						default:
							break;
						}
						if(exclusionEventsList.contains(eventType)) {
							log.trace("skipping based on exclusion list : "+eventType);
							continue;
						}
						payload.put(SSIConstant.Source_type, SSIConstant.kafka);
						payload.put(SSIConstant.Format_type, payload.get(SSIConstant.Format_type));
						payload.put(SSIConstant.source_data, record.value());
						payload.put(SSIConstant.SSI_Key, ssiKey);
						payload.put(SSIConstant.MQ_ID, MQ_ID);
						payload.put(SSIConstant.Event_type, eventType);
						payload.put(SSIConstant.Event_Id, eventId);

						if(payload.containsKey(SSIConstant.Handle_Dedup_Ind)){
							if((int)payload.get(SSIConstant.Handle_Dedup_Ind) == 1) {
								payload.put(SSIConstant.Handle_Dedup_Ind, payload.get(SSIConstant.Handle_Dedup_Ind));
								payload.put(SSIConstant.dedup_url, payload.get(SSIConstant.dedup_url));
							}
						}

						log.info("SSI_Key : "+ssiKey);

						mqEndTimeStamp = convertTime(System.currentTimeMillis());
						payload.put(SSIConstant.mqEndTimeStamp, mqEndTimeStamp);
						payload.put(SSIConstant.isReprocessed, isReprocessed);
						
						if(cdb!=null) {
							cdb.processPayload(payload);
						}


					} catch (Exception e) {
						log.error("error while re-processing <key : "+record.key() +"> "+e.getLocalizedMessage());
						e.printStackTrace();
					}
					break;
				}
				if(isReprocessed) {
					break;
				}
			}
		}catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
		finally {
			consumer.close();
		}
	}

	public void coreReprocessor(Map<String, Object> payload) {
		log.info("initiating coreReprocessor ");
		JSONObject sourceJson = new JSONObject(payload.get(SSIConstant.source_data).toString());

		if(!sourceJson.has(SSIConstant.__origin__)) {
			JSONObject originObj = new JSONObject();

			originObj.put(SSIConstant.producer,payload.get(SSIConstant.topic));
			originObj.put(SSIConstant.offset,payload.get(SSIConstant.offset));

			sourceJson.put(SSIConstant.__origin__,originObj);
		}
		JSONObject jsonMsg = new JSONObject(sourceJson.get("__origin__").toString());
		//		JSONObject jsonMsg = new JSONObject(sourceJson.toString());

		int retry = (int)payload.get(SSIConstant.RP_Retry);
		log.info("origin msg : "+jsonMsg);
		if(jsonMsg.has(SSIConstant.errorObj)) {

			retry = (Integer) jsonMsg.getJSONObject(SSIConstant.errorObj).get(SSIConstant.retry) - 1;
			log.info("decrement retry : "+retry);
			jsonMsg.getJSONObject(SSIConstant.errorObj).put(SSIConstant.retry,retry);
		}
		else{

			//			String  producerErr = "SSI_"+payload.get(SSIConstant.SSI_Key);//SSINrtKey.getMQID(context.SSI_Key)+"&"+SSINrtKey.getEventId(context.SSI_Key)+"&"+SSINrtKey.getEventType(context.SSI_Key);
			long   timeErr = System.currentTimeMillis();
			//			String errorErr = globalMap.get(SSIConstant.Technical_Error_Desc).toString();

			JSONObject errorObj = new JSONObject();

			errorObj.put(SSIConstant.retry, retry);
			//			errorObj.put(SSIConstant.producer, producerErr);
			errorObj.put(SSIConstant.time, timeErr);
			//			errorObj.put(SSIConstant.error, errorErr);

			jsonMsg.put(SSIConstant.errorObj, errorObj);
		}
		log.info("jsonMsg : "+jsonMsg);

		if(retry>0) {
			log.info("reprocess retry : "+retry);

			//String kafka_kafkaBootstrapServersRP = "10.245.128.143:9092";
			String kafkaTopicRP = payload.get(SSIConstant.RP_Kafka_Topic).toString(); //parameterize
			String kafkaBootstrapServersRP = payload.get(SSIConstant.RP_Kafka_BootstrapServer).toString(); //parameterize

			Properties properties=new Properties();  
			properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaBootstrapServersRP);  
			properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class.getName());  
			properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

			KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties); 


			ProducerRecord<String, String> record=new ProducerRecord<String, String>(kafkaTopicRP,payload.get(SSIConstant.recordKey).toString(),jsonMsg.toString());

			producer.send(record);

			producer.flush();
			log.info("reprocess msg sent to reprossess <topic : "+kafkaTopicRP +">--<offset : "+payload.get(SSIConstant.offset)+">");
			producer.close();
		}
	}

}

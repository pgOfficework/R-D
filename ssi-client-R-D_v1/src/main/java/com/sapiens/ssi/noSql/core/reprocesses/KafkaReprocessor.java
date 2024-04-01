package com.sapiens.ssi.noSql.core.reprocesses;

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


public class KafkaReprocessor {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(KafkaReprocessor.class);


	public static String convertTime(long time){
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(date);
	}

	//reprocess ssi of source type kafka
	public  void reprocessSSIRecord(Map<String, Object> payload) {
		
		int RP_Ind = 0;
		if(RP_Ind==1){
			JSONObject inputContents = new JSONObject(payload.get(""));

			JSONObject jsonMsg = new JSONObject(inputContents.get("__origin__").toString());


			//String repeatQuery = "select count(1) from SIT_Real_time_Audit_log where MQ_ID = '"+SSINrtKey.getMQID(context.SSI_Key)+"' AND  Event_ID = '"+SSINrtKey.getEventId(context.SSI_Key)+"' AND Event_Type = '"+SSINrtKey.getEventType(context.SSI_Key)+"'";

			int retry = (int) payload.get("RP_Retry");
			if(jsonMsg.has(SSIConstant.errorObj)) {
				retry = (Integer) jsonMsg.getJSONObject(SSIConstant.errorObj).get(SSIConstant.retry) - 1;
						jsonMsg.getJSONObject(SSIConstant.errorObj).put(SSIConstant.retry,retry);
			}
			else{

//				String  producerKey = "SSI_"+SSINrtKey.getMQID(context.SSI_Key)+"&"+SSINrtKey.getEventId(context.SSI_Key)+"&"+SSINrtKey.getEventType(context.SSI_Key);
				long   timeErr = System.currentTimeMillis();
//				String errorErr = globalMap.get(SSIConstant.Technical_Error_Desc).toString();

				JSONObject errorObj = new JSONObject();


				errorObj.put(SSIConstant.retry, retry);
//				errorObj.put(SSIConstant.producer, producerKey);
				errorObj.put(SSIConstant.time, timeErr);
//				errorObj.put(SSIConstant.error, errorErr);

				jsonMsg.put(SSIConstant.errorObj, errorObj);
			}
				log.info("jsonMsg : "+jsonMsg);



				//String kafka_kafkaBootstrapServersRP = "10.245.128.143:9092";
				String kafkaTopicRP = payload.get(SSIConstant.RP_Kafka_Topic).toString(); //parameterize
				String kafkaBootstrapServersRP=payload.get(SSIConstant.RP_Kafka_BootstrapServer).toString(); //parameterize

				Properties properties=new Properties();  
				properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaBootstrapServersRP);  
				properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class.getName());  
				properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

				KafkaProducer<String,String> producer = new KafkaProducer<String, String>(properties); 


				ProducerRecord<String, String> record=new ProducerRecord<String, String>(kafkaTopicRP,"aggregator",jsonMsg.toString());

				producer.send(record);

				producer.flush();
				System.out.println("msg sent");
				producer.close();
		}
	}


}

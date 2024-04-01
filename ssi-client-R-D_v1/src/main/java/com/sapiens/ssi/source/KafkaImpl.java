package com.sapiens.ssi.source;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONObject;

import com.sapiens.ssi.client.ClientFactory;
import com.sapiens.ssi.client.ClientInterface;
import com.sapiens.ssi.config.kafka.RebalanceListner;
import com.sapiens.ssi.config.log.SSILogConfig;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.source.reprocesses.KafkaReprocesser;


public class KafkaImpl implements SourceInterface{

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(KafkaImpl.class);


	public static String convertTime(long time){
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(date);
	}

	private KafkaConsumer<String, String> consumer;

	//initiate ssi of source type kafka
	@Override
	public void startSSI(Map<String, Object> targetConfig, Map<String, Object> sourceConfig, String ssiType,String event) {

		if(targetConfig.containsKey(SSIConstant.Log_Mode))
			SSILogConfig.ChangeLevel(targetConfig.get(SSIConstant.Log_Mode).toString());

		try {
			final Thread mainThread = Thread.currentThread();


			log.info("start-kafka-Impl-0-0---");
			KafkaImpl KafkaImpl = new KafkaImpl();

			ClientInterface cdb = ClientFactory.getSource(ssiType);

			//		KafkaImpl.initialize(sourceConfig);
			String Topics= sourceConfig.get(SSIConstant.Topics).toString();

			//intialize kafka properties
			//		initialize(sourceConfig);

			String bootstrapServers= sourceConfig.get(SSIConstant.bootstrapServers).toString();  
			String grp_id= sourceConfig.get(SSIConstant.grp_id).toString();    
			
			

			Properties properties=new Properties();  
			properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);  
			properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());  
			properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());  
			properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,grp_id);  
			properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, SSIConstant.falseBool);

			String kafka_Offset = sourceConfig.get(SSIConstant.kafka_Offset).toString(); 

			if(!kafka_Offset.equals(SSIConstant.NA)) {
				properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,kafka_Offset);  
				log.info("set kafka offset : "+kafka_Offset);
			}

			String exclusionEvents = sourceConfig.get(SSIConstant.exclusion_events).toString();

			List<String> TopicList=null;
			List<String> exclusionEventsList=null;

			RebalanceListner rebalanceListner = null;
			TopicList = Arrays.asList(Topics.split(","));
			exclusionEventsList = Arrays.asList(exclusionEvents.split(","));

			//declare audit parameters 
			String mqCoreTimeStamp = null;
			String mqStartTimeStamp = null;
			String mqEndTimeStamp = null;
			String MQ_ID = null;
			String eventType = null;
			String eventId = null;
			String eventInfo = null;
			String sourceSystem = sourceConfig.get(SSIConstant.source_system).toString();

			Map<String , Object> payload = new LinkedHashMap<String, Object>();


			if(sourceConfig.containsKey(SSIConstant.Source_MD_CMS_Table_Name))
				payload.put(SSIConstant.Source_MD_CMS_Table_Name, sourceConfig.get(SSIConstant.Source_MD_CMS_Table_Name));

			payload.putAll(targetConfig);
			// Registering a shutdown hook so we can exit cleanly
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					log.info("Starting exit...");
					// Note that shutdownhook runs in a separate thread, so the only thing we can safely do to a consumer is wake it up
					consumer.wakeup();
					try {
						mainThread.join();					
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			try {
				log.info("setting to consumer propertities ");

				consumer = new KafkaConsumer<>(properties);

				rebalanceListner = new RebalanceListner(consumer);
				log.info("subscribing to topics : "+TopicList);

				consumer.subscribe(TopicList,rebalanceListner);

				log.info("subscribed to topics : "+TopicList);

				//polling  
				while(true)	{

					ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(10000)); 
					log.trace("ConsumerRecords count :",records.count());
					for(ConsumerRecord<String,String> record: records){ 

						StringBuilder ssiKey = new StringBuilder();
						
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
							payload.put(SSIConstant.Format_type, sourceConfig.get(SSIConstant.Format_type));
							payload.put(SSIConstant.Event_type, event);
							payload.put(SSIConstant.source_data, record.value());
							payload.put(SSIConstant.SSI_Key, ssiKey);
							payload.put(SSIConstant.MQ_ID, MQ_ID);
							payload.put(SSIConstant.Event_type, eventType);
							payload.put(SSIConstant.Event_Id, eventId);

							if(sourceConfig.containsKey(SSIConstant.Handle_Dedup_Ind)){
								if((int)sourceConfig.get(SSIConstant.Handle_Dedup_Ind) == 1) {
									payload.put(SSIConstant.Handle_Dedup_Ind, sourceConfig.get(SSIConstant.Handle_Dedup_Ind));
									payload.put(SSIConstant.dedup_url, sourceConfig.get(SSIConstant.dedup_url));
								}
							}

							log.info("SSI_Key : "+ssiKey);
							log.trace("configSSI : "+targetConfig);
							log.trace("configKafka : "+sourceConfig);

							mqEndTimeStamp = convertTime(System.currentTimeMillis());
							payload.put(SSIConstant.mqEndTimeStamp, mqEndTimeStamp);

							payload.put(SSIConstant.ssiType,ssiType);

							payload.put(SSIConstant.recordKey,record.key());
							payload.put(SSIConstant.topic,record.topic());
							payload.put(SSIConstant.partition,record.partition());
							payload.put(SSIConstant.offset,record.offset());
							payload.putAll(sourceConfig);

							if(cdb!=null) {
								cdb.processPayload(payload);
							}

							for (TopicPartition tp: consumer.assignment()) {
								if(!records.isEmpty())
									log.info("Committing offset at position:"+ consumer.position(tp)+" for partition:"+tp);
							}


						} catch (Exception e) {
							log.error("error while processing kafka record <key : ",record.key() ,"> "+e.getMessage());

//						payload.put(SSIConstant.ssiType,ssiType);
//						payload.put(SSIConstant.topic,record.topic());
//						payload.put(SSIConstant.partition,record.partition());
//						payload.put(SSIConstant.offset,record.offset());
//						payload.putAll(sourceConfig);

//	StandaloneConsumerSeekOffset.testreset();
							log.info("initiating reprocessing for <key : "+record.key() +"> "+e);
							KafkaReprocesser kreprocesses = new KafkaReprocesser();
							kreprocesses.ssiReprocess(payload);

							e.printStackTrace();
						}
						/*finally {

							switch(ssiType.toLowerCase()) {

							case "sql" : { 

							}
							case "nosql":{

							}

							}
						}*/

					}
					consumer.commitAsync();
				}
			}catch (Exception e) {
				log.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
			finally {


				try {
					//				consumer.commitSync();
					try {
						consumer.commitSync(); 
					} catch (CommitFailedException e) {
						log.error("commit failed"+ e) ;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					consumer.close();
				}
			}
		} catch (Exception e) {
			log.error("error initiating the kafka : "+e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	
	
	//set kafka properties
	private void initialize(Map<String, Object> sourceConfig) {
		String bootstrapServers= sourceConfig.get(SSIConstant.bootstrapServers).toString();  
		String grp_id= sourceConfig.get(SSIConstant.grp_id).toString();    

		Properties properties=new Properties();  
		properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapServers);  
		properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());  
		properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());  
		properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,grp_id);  
		properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, SSIConstant.falseBool);

		String kafka_Offset = sourceConfig.get(SSIConstant.kafka_Offset).toString(); 

		if(!kafka_Offset.equals(SSIConstant.NA)) {
			properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,kafka_Offset);  
			log.info("set kafka offset : "+kafka_Offset);
		}
		consumer = new KafkaConsumer<String,String>(properties);  
	}





	@Override
	public void reproceser(Map<String, Object> payload, Exception e) {
		JSONObject sourceJson = new JSONObject(payload.get(SSIConstant.source_data).toString());
		KafkaReprocesser kreprocesses = new KafkaReprocesser();
		
		if(!(payload.containsKey(SSIConstant.isReprocessed) && (boolean)payload.get(SSIConstant.isReprocessed)) && (!sourceJson.has(SSIConstant.__origin__))) {
			try {
				log.error("error while processing payload <key : "+payload.get(SSIConstant.recordKey) +">--<MQ_ID:"+payload.get(SSIConstant.MQ_ID)+">"+e.getMessage());

				log.info("initiating reprocessing for <key : "+payload.get(SSIConstant.recordKey) +"> "+e);
				kreprocesses.ssiReprocess(payload);
			}
			catch (Exception e1) {
				//					log.error("error while re-processing payload from source topic <key : "+payload.get(SSIConstant.recordKey) +"> "+e.getMessage());
				e1.printStackTrace();
			}
		}else {
			log.error("error while re-processing payload from source topic <key : "+payload.get(SSIConstant.recordKey) +">--<MQ_ID:"+payload.get(SSIConstant.MQ_ID)+">"+e.getMessage());
			if((int)payload.get(SSIConstant.RP_Ind) ==1)
				kreprocesses.coreReprocessor(payload);

		}
	}

}

package com.sapiens.ssi.noSql.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import com.sapiens.ssi.bean.SSIBean;
import com.sapiens.ssi.client.ClientFactory;
import com.sapiens.ssi.client.ClientInterface;
import com.sapiens.ssi.config.kafka.RebalanceListner;


public class XlsxImpl implements SSICoreInterface{

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(XlsxImpl.class);

	@Override
	public void runCoreJob(Map<String, Object> payload) {
		// TODO Auto-generated method stub
		
	}




}

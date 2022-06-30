package com.sapiens.dba.connection;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoConfigurationException;
import com.mongodb.MongoSecurityException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@ControllerAdvice
public class MongoClientConnectionPool {
	@Value("${mongoConnectionStringUrl}")
	private String mongoConnectionStringUrl;
	@Value("${maxWaitTime}")
	private int maxWaitTime;
	@Value("${maxPoolSize}")
	private int maxPoolSize;

	public static MongoClient mongoClient = null;

	// creating MongoClient pool here and using it all over the project

	public MongoClient getMongoClient() {
		log.debug("creating mongoClient pool");

		if (mongoClient == null) {
			mongoClient = MongoClients.create(
					MongoClientSettings.builder().applyConnectionString(new ConnectionString(mongoConnectionStringUrl))
							.applyToConnectionPoolSettings(
									builder -> builder.maxWaitTime(maxWaitTime, TimeUnit.SECONDS).maxSize(maxPoolSize))
							.build());
		}
		log.debug("mongoClient pool created");

		return mongoClient;
	}

	@ExceptionHandler(value = MongoSecurityException.class)
	public void exception(MongoSecurityException exception) {
		log.error("Authentication Failed due to wrong credentials - " + exception.getLocalizedMessage());

	}

	@ExceptionHandler(value = MongoConfigurationException.class)
	public void exception(MongoConfigurationException exception) {
		log.error("Host address is not found - " + exception.getLocalizedMessage());

	}

	@ExceptionHandler(value = IllegalArgumentException.class)
	public void exception(IllegalArgumentException exception) {
		log.error("The connection string is invalid. Connection strings must start with either 'mongodb://' or 'mongodb+srv://");
	}

	@ExceptionHandler(value = Exception.class)
	public void exception(Exception exception) {
		log.error(exception);
	}
}
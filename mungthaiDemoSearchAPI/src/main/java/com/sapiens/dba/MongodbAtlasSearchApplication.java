package com.sapiens.dba;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class })
public class MongodbAtlasSearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongodbAtlasSearchApplication.class, args);
	}
}

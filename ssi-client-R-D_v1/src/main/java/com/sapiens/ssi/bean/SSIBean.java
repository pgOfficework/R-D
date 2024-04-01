package com.sapiens.ssi.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
public class SSIBean {
	//log 4j config param
	private String logProjectName;
	private String logJob;
	private String logFileLocation;
	private String logFileName;
	private String logFileSize;
	private String logRollOverSize;

	//Source Kafka Properties
	String bootstrapServers;
	
	//Datahub Kafka Properties
}

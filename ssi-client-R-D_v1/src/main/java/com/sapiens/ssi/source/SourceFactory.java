package com.sapiens.ssi.source;

public class SourceFactory {
	private static final String FileName = null;

	public static SourceInterface getSource(String type) {
		
		if(type.equalsIgnoreCase("kafka")) {
			return new KafkaImpl();
		}
		else if(type.equalsIgnoreCase("file")) {
			return new FileImpl();
		}
		else
			return null;
	}
}

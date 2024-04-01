package com.sapiens.ssi.noSql.core.ingest;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class IngestFactory {
	private static final String FileName = null;

	public static IngestInterface getDB(String type) {
		
		if(type.toLowerCase().equalsIgnoreCase("mongodb")) {
			return new MongoIngest();
		}
		else
			return null;
	}


}

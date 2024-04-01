package com.sapiens.ssi.client;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.sapiens.ssi.constants.SSIConstant;
/*
client factory to call the ssi clients such as sql & nosql
*/
public class ClientFactory {
	
	public static ClientInterface getSource(String type) {
		
		//log.trace(type);
		if(type.equalsIgnoreCase(SSIConstant.sql)) {
			return new SqlImpl();
		}
		else if(type.equalsIgnoreCase(SSIConstant.nosql)) {
			return new NoSqlImpl();
		}
		else
			return null;
	}
}

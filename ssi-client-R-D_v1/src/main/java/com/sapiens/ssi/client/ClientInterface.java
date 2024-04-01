package com.sapiens.ssi.client;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sapiens.ssi.bean.SSIBean;

/*
client interface to call clients & process payload of respective payload streamed by clients
 */
public interface ClientInterface {

	//start ssi clients of sources like kafka ,files .. and events of source system like contact,policy,cliams..
	///void callSSINRT(SSIBean ssiBean, Map<String, Object> configSSI,String event,LinkedHashMap<String, Object> configSource);
	void callClient(Map<String, Object> configSSI, String ssiType);

	//process payload of consumed data streamed by clients 
	void processPayload(Map<String, Object> payload);
}

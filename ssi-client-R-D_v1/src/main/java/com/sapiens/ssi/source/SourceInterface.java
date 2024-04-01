package com.sapiens.ssi.source;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sapiens.ssi.bean.SSIBean;


public interface SourceInterface {

//	void callSSINRT(SSIBean ssiBean, Map<String, Object> configSSI,String event,LinkedHashMap<String, Object> configSource);

	void startSSI(Map<String, Object> targetConfig, Map<String, Object> sourceConfig, String ssiType, String event);


	void reproceser(Map<String, Object> payload, Exception e);

}

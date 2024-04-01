package com.sapiens.ssi.bean;

public class SSIBeanSetters {
	
	public static void setters(SSIBean ssiBean){
		
		ssiBean.setLogProjectName("SSI");
		ssiBean.setLogJob("Main");
		ssiBean.setLogFileLocation("D:/GPF_PROJECT/LOG");
		ssiBean.setLogFileName("Log");
		ssiBean.setLogFileSize("100KB");
		ssiBean.setLogRollOverSize("5");
		
		
		ssiBean.setBootstrapServers("bootstrapServers");
	}
}

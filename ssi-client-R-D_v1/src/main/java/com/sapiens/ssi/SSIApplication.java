package com.sapiens.ssi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.yaml.snakeyaml.Yaml;

import com.sapiens.ssi.bean.SSIBean;
import com.sapiens.ssi.bean.SSIBeanSetters;
import com.sapiens.ssi.client.ClientFactory;
import com.sapiens.ssi.client.ClientInterface;
import com.sapiens.ssi.config.log.SSILogConfig;
import com.sapiens.ssi.config.log.SSILogger;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.transformer.config.JntTransformationLoad;


public class SSIApplication {

	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(SSIApplication.class);

	public static void main(String[] args) throws IOException {
		//initialize log-4j parameters
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File("log4j2.xml");
		context.setConfigLocation(file.toURI());

		//set log config
		SSILogger.setLog(log);
		SSILogConfig.logConfig(SSIConstant.logProjectName,SSIConstant.logJob,SSIConstant.logFileLocation,SSIConstant.logFileName,SSIConstant.logFileSize,SSIConstant.logRollOverSize);

		//config constants setters
		SSIBean ssiBean=new SSIBean();
		SSIBeanSetters  beanSetters = new SSIBeanSetters();
		beanSetters.setters(ssiBean);

		//read yaml configurations
		Yaml yaml = new Yaml();
		//		File configFile = new File("configTestEvents.yaml");
		File configFile = new File("configTestEventsNoSQL.yaml");
		//		File configFile = new File("configTestEventsNoSQLFile.yaml");

		Map<String, Object> config = new LinkedHashMap<String, Object>();

		try {
			//load yaml config
			FileInputStream fis = new FileInputStream(configFile);
			config = yaml.load(fis);
			fis.close();
		} catch (IOException e) {
			log.fatal("IO Error reading config "+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

//		Map<String, Object> configSSI = new LinkedHashMap<String, Object>();
//		configSSI.putAll((LinkedHashMap<String, Object>)config.get("ssi"));

//		Map<String, LinkedHashMap<String, Object>> configSource = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
//		JntTransformationLoad.tLoad((Map<String, Object>) ((Map<String, Object>)config.get(SSIConstant.ssi)).get(SSIConstant.nosql));
		//process ssi client - sql & no sql
		((Map<String, Object>)config.get(SSIConstant.ssi)).entrySet().parallelStream().
		forEach(ssi -> {
			ClientInterface cdb = ClientFactory.getSource(ssi.getKey());
			if(cdb!=null) {
				cdb.callClient(((Map<String, Object>)ssi.getValue()),ssi.getKey());
			}
		});

	}
}
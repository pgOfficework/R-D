package com.sapiens.ssi.config.log;



import java.io.File;
import java.io.IOException;

import javax.print.attribute.standard.JobName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;



public class SSILogConfig {

	public static String jobName =null;;
	public static String packageName=null;
	
	public static DocumentBuilder documentBuilder=null;
	public static Document document=null;
	public static Transformer transformer=null;
	public static TransformerFactory transformerFactory=null;
	public static DOMSource domSource=null;
	public static String xmlFilePath=null;
	
	static {
	DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	transformerFactory = TransformerFactory.newInstance(); 
    try {
		documentBuilder = documentBuilderFactory.newDocumentBuilder();
		transformer = transformerFactory.newTransformer();
	} catch (ParserConfigurationException | TransformerConfigurationException e) {
		SSILogger.log.error("<"+SSILogConfig.class.getSimpleName()+">"+"ERROR OCCURED");
		e.printStackTrace();
	}
 
	}
    
	public static void ChangeLevel(String level) {
		if(level.toLowerCase().equals("debug"))
	    	Configurator.setRootLevel(Level.DEBUG);
		else if(level.toLowerCase().equals("trace"))
	    	Configurator.setRootLevel(Level.TRACE);
		else if(level.toLowerCase().equals("fatal"))
	    	Configurator.setRootLevel(Level.FATAL);
		else if(level.toLowerCase().equals("info"))
	    	Configurator.setRootLevel(Level.INFO);
		else if(level.toLowerCase().equals("warn"))
	    	Configurator.setRootLevel(Level.WARN);
		else if(level.toLowerCase().equals("error"))
			Configurator.setRootLevel(Level.ERROR);
		
//		LOGGER.log.info("Log Mode set as "+level.toLowerCase());
	}
	
	public static void logConfig(String projectName,String job,String logFileLocation,String LogFileName,String LogFileSize,String rollOverSize) {
		
		xmlFilePath = "log4j2.xml";	
//		System.out.println("xmlFilePath "+ xmlFilePath); 
	    try {
	        document = documentBuilder.parse(xmlFilePath);
//	        System.out.println("document "+ document); 
	        Node Property = document.getElementsByTagName("Property").item(0);
	        NodeList list = Property.getChildNodes(); 
	        Node location = list.item(0);
	        location.setTextContent(logFileLocation);	 
	       /* 
	        Node Property1 = document.getElementsByTagName("Property").item(1);
	        NodeList list1 = Property1.getChildNodes(); 
	        Node filename = list1.item(0);
	        filename.setTextContent(LogFileName);*/
	        
	        Node policies = document.getElementsByTagName("SizeBasedTriggeringPolicy").item(0);
	        NamedNodeMap attr1 = policies.getAttributes();
	        Node Size = attr1.getNamedItem("size");
	        Size.setTextContent(LogFileSize);
	       /* 
	        Node loggerPackage = document.getElementsByTagName("Logger").item(0);
	        NamedNodeMap attr = loggerPackage.getAttributes();
	        Node nodeAttr = attr.getNamedItem("name");
	        nodeAttr.setTextContent(projectName.toLowerCase());
	        */
	        Node rollOver = document.getElementsByTagName("DefaultRolloverStrategy").item(0);
	        NamedNodeMap strategy = rollOver.getAttributes();
	        Node def = strategy.getNamedItem("max");
	        def.setTextContent(rollOverSize);
	        
	        // write the object to the file
	        domSource = new DOMSource(document);
	        StreamResult streamResult = new StreamResult(new File(xmlFilePath));
	        transformer.transform(domSource, streamResult);
	        
	        File file=new File(xmlFilePath);
	        SSILogger.context.setConfigLocation(file.toURI());
	        
//			System.out.println("Log file path set as "+ logFileLocation); 
//			System.out.println("Log file name set as "+ LogFileName); 
//			System.out.println("Log file size set as "+ LogFileSize);
//			System.out.println("Log file rollOverSize set as "+ rollOverSize);


	    } catch (IOException ioe) {
	        ioe.printStackTrace();
	    } catch (SAXException sae) {
	        sae.printStackTrace();
	    } catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
}

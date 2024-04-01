package com.sapiens.ssi.source;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sapiens.ssi.client.ClientFactory;
import com.sapiens.ssi.client.ClientInterface;
import com.sapiens.ssi.config.log.SSILogConfig;
import com.sapiens.ssi.constants.SSIConstant;


public class FileImpl implements SourceInterface{
	private static org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
			.getLogger(FileImpl.class);

	//initiate ssi of source type file
	@Override
	public void startSSI(Map<String, Object> targetConfig, Map<String, Object> sourceConfig, String ssiType,
			String event) {
		
		//reseting log mode
		if(targetConfig.containsKey(SSIConstant.Log_Mode))
			SSILogConfig.ChangeLevel(targetConfig.get(SSIConstant.Log_Mode).toString());

		log.debug("File Implementation - "+sourceConfig);
		//process ssi client - sql & no sql
		ClientInterface cdb = ClientFactory.getSource(ssiType);

		Map<String , Object> payload = new LinkedHashMap<String, Object>();

		if(sourceConfig.containsKey(SSIConstant.Source_MD_CMS_Table_Name))
			payload.put(SSIConstant.Source_MD_CMS_Table_Name, sourceConfig.get(SSIConstant.Source_MD_CMS_Table_Name));

		payload.putAll(targetConfig);
		payload.put(SSIConstant.Format_type, sourceConfig.get(SSIConstant.Format_type));

        // We obtain the file system of the Path
		FileSystem fs = FileSystems.getDefault();
		Path watchPath = fs.getPath(sourceConfig.get(SSIConstant.Source_Folder_Path).toString());
		// We create the new WatchService using the new try() block
		WatchService watchService = null;
		try {
			watchService = fs.newWatchService();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
            //  register the path to the service
            //  watch for creation events
			watchPath.register( watchService, StandardWatchEventKinds.ENTRY_CREATE );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true) {

			//System.out.println("file : "+(count++));
			try {

				WatchKey changeKey = null;
				try {
					changeKey = watchService.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				 // Dequeueing events
				List<WatchEvent<?>> watchEvents = changeKey.pollEvents();
				
				for ( WatchEvent<?> watchEvent : watchEvents )
				{

					payload.put(SSIConstant.mqStartTimeStamp, convertTime(System.currentTimeMillis()));
					// Ours are all Path type events:
					WatchEvent<Path> pathEvent = (WatchEvent<Path>)watchEvent;

					Path path = pathEvent.context();
					 // Get the type of the event
					WatchEvent.Kind<Path> eventKind = pathEvent.kind();
					log.info( eventKind + " for path: " + path );
//					payload.put("Source_MD_CMS_Table_Name", sourceConfig.get("Source_MD_CMS_Table_Name"));
					payload.put(SSIConstant.inputFilePath, sourceConfig.get("Source_Folder_Path")+"/"+path);

					//get core timestamp from file created dt
					Path p = Paths.get(payload.get(SSIConstant.inputFilePath).toString());
					BasicFileAttributes view = Files.readAttributes(p, BasicFileAttributes.class);

					payload.put(SSIConstant.mqCoreTimeStamp, convertTime(view.creationTime().toMillis()));

					StringBuilder ssiKey = new StringBuilder();
					ssiKey.append(path.getFileName());
					payload.put(SSIConstant.Source_type, SSIConstant.file);
					payload.put(SSIConstant.Event_type, event);
					payload.put(SSIConstant.SSI_Key, ssiKey);
					//configSSI.put("Event_type", eventType);
					log.info("SSI_Key : "+ssiKey);
					/*List<String> arrList =new LinkedList<String>();
					for (Entry<String, Object> configKV : configSSI.entrySet()) {
						arrList.add("--context_param "+configKV.getKey()+"="+configKV.getValue());

					}
					for (Entry<String, Object> configKV : configSrc.entrySet()) {
						arrList.add("--context_param "+configKV.getKey()+"="+configKV.getValue());

					}
					System.out.println("arrList : "+arrList);
					String[] contextLoad = arrList.stream().toArray(String[]::new);

					ssi.runJob(contextLoad);*/
					payload.put(SSIConstant.mqEndTimeStamp, convertTime(System.currentTimeMillis()));

					//process payload for file events
					if(cdb!=null) {
						cdb.processPayload(payload);
					}
				}

				changeKey.reset();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static String convertTime(long time){
		Date date = new Date(time);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(date);
	}

	@Override
	public void reproceser(Map<String, Object> payload, Exception e) {
		// TODO Auto-generated method stub
		
	}

}

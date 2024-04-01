package com.sapiens.ssi.constants;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;

public class SSIConstant {

	public static final String ssi = "ssi";

	//ssi default log constants
	public static final String logProjectName = "SSI";
	public static final String logJob = "Main";
	public static final String logFileLocation = "SSI_CLIENT/LOG";
	public static final String logFileName = "ssilog";
	public static final String logFileSize = "10MB" ;
	public static final String logRollOverSize = "5";

	//ClientFactory constants
	public static final String sql = "sql";
	public static final String nosql = "noSql";

	//ClientImpl constants
	public static final String source = "source";
	public static final String target = "target";

	//Audit Fields constants
	public static final String SSI_Real_time_MQ_Log = "SSI_Real_time_MQ_Log";
	public static final String SSI_Real_time_Audit_log = "SSI_Real_time_Audit_log";

	public static final String MQ_ID = "MQ_ID";
	public static final String Event_Id = "Event_Id";
	public static final String Event_type = "Event_type";
	public static final String mqStartTimeStamp = "mqStartTimeStamp";
	public static final String mqEndTimeStamp = "mqEndTimeStamp";
	public static final String mqCoreTimeStamp = "mqCoreTimeStamp";
	public static final String Target_Audit_Schema_Nm = "Target_Audit_Schema_Nm";
	public static final String Target_Audit_JDBC_URL = "Target_Audit_JDBC_URL";
	public static final String Target_Audit_User_Id = "Target_Audit_User_Id";
	public static final String Target_Audit_Password = "Target_Audit_Password";
	//nosql
	public static final String mqID = "mqID";
	public static final String eventId = "eventId";
	public static final String eventType = "eventType";
	public static final String status = "status";
	public static final String statusDesc = "statusDesc";
	public static final String Target_Database_Uri = "Target_Database_Uri";
	public static final String Target_Audit_Database_Name = "Target_Audit_Database_Name";

	public static final String Format_type = "Format_type";

	public static final String Commit = "Commit";

	public static final String json = "json";
	public static final String xlsx = "xlsx";

	public static final String Source_type = "Source_type";
	public static final String source_data = "source_data";
	public static final String inputFilePath = "inputFilePath";
	public static final String Target_Database = "Target_Database";

	public static final String eventStartTimeStamp = "eventStartTimeStamp";
	public static final String eventEndTimeStamp = "eventEndTimeStamp";

	public static final String kafka = "kafka";
	public static final String file = "file";

	public static final String Log_Mode = "Log_Mode";
	public static final String Source_MD_CMS_Table_Name = "Source_MD_CMS_Table_Name";
	public static final String Source_Folder_Path = "Source_Folder_Path";
	public static final String SSI_Key = "SSI_Key";

	public static final String bootstrapServers = "bootstrapServers";
	public static final String grp_id = "grp_id";
	public static final String Topics = "Topics";
	public static final String kafka_Offset = "kafka_Offset";
	public static final String exclusion_events = "exclusion_events";

	public static final String source_system = "source_system";
	public static final String idit = "idit";
	public static final String coresuite = "coresuit";
	public static final String falseBool = "false";
	public static final String NA = "NA";

	public static final Object dbTarget = "Target_Database_Name";

	public static final String _id = "_id";


	// dedup
	public static final String dedup_url = "dedup_url";
	public static final String Handle_Dedup_Ind = "Handle_Dedup_Ind";
	public static final String dedupJNfunction = "$dedup";

	public static final String ssiType = "ssiType";

	//reprocess global Constants
	public static final String reprocess_grp_id = "reprocess_grp_id";
	public static final String topic = "topic";
	public static final String offset = "offset";
	public static final String  partition = "partition";
	public static final String errorObj = "error";
	public static final String retry = "retry";
	public static final String time = "time";
	public static final String error = "error";
	public static final String RP_Kafka_Topic = "RP_Kafka_Topic";
	public static final String RP_Kafka_BootstrapServer = "RP_Kafka_BootstrapServer";
	public static final String recordKey = "recordKey";
	public static final String __origin__ = "__origin__";
	public static final String isReprocessed = "isReprocessed";
	public static final String RP_Ind = "RP_Ind";
	public static final String RP_Retry = "RP_Retry";

	public static final String producer = "producer";

	public static final String ErrorEncoutered = " ErrorEncoutered ";
	
	public static final String storedTrans = "storedTrans";
	public static final String JNT_Collection = "JNT_Collection";
	public static final String JNT_Read="JNT_Read";
	public static final String JNT_File_Path="JNT_File_Path";
	public static final String DB_Name="DB_Name";
	public static final String db="db";

	public static final Object JNTRead = "JNTRead";


}

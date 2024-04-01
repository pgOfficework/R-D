package com.sapiens.ssi.noSql.core.transformer.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.sapiens.ssi.constants.SSIConstant;
import com.sapiens.ssi.noSql.core.connection.MongoConnection;

public class JntTransformationLoad {
	private static String uriTarget = null;
	private static String dbTarget = null;

	private static MongoClient mongoClient = null;
	private static MongoDatabase targetDatabase = null;
	
	public static void tLoad(Map<String, Object> payload) throws IOException {

		if(((Map<String, Object>)payload.get(SSIConstant.JNTRead)).containsKey(SSIConstant.file))
			fromFile((Map<String, Object>)payload.get(SSIConstant.JNTRead));
		else
		fromDb(payload);

	}
	
	public static void fromDb(Map<String, Object> payload) {
		

		if(mongoClient == null) {

			uriTarget = ((Map<String, Object>)payload.get(SSIConstant.target)).get(SSIConstant.Target_Database_Uri).toString();
			mongoClient = MongoConnection.getConnection(uriTarget);
		}
		MongoDatabase database = mongoClient.getDatabase(((Map<String, Object>)((Map<String, Object>)payload.get(SSIConstant.target)).get(SSIConstant.Target_Audit_Database_Name)).toString());
		
		MongoCollection<Document> collection  = database.getCollection(((Map<String, Object>)((Map<String, Object>)payload.get(SSIConstant.JNTRead)).get(SSIConstant.db)).get(SSIConstant.JNT_Collection).toString()); 
		
		try (MongoCursor<Document> cur = collection.find().iterator()) {

            while (cur.hasNext()) {
                Document doc = cur.next();
                ArrayList coll = new ArrayList<>(doc.values());
                 
                 JNTConfig.JNTConfigMap.put(coll.get(1).toString(), coll.get(2).toString());
            }
        }
//		 System.out.println(JNTConfig.JNTConfigMap.size());
	
	
	}
	
	public static void fromFile(Map<String, Object> configStoredTrans) throws IOException {
		
		long start = System.currentTimeMillis();
		System.out.println(((Map<String, Object>)configStoredTrans.get(SSIConstant.file)).get(SSIConstant.JNT_File_Path));
		FileInputStream fis = new FileInputStream(((Map<String, Object>)configStoredTrans.get(SSIConstant.file)).get(SSIConstant.JNT_File_Path).toString());
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet("jsonata");

		System.out.println(sheet.getLastRowNum());
		
		Iterator<Row> rowIterator = sheet.iterator();
		int noOfColumns = sheet.getRow(0).getLastCellNum();
		int i = 0;
		
		Row nextRow1 = rowIterator.next(); // get DataType row and then skip for data
		
		while(rowIterator.hasNext()) {
			nextRow1=rowIterator.next();
			JNTConfig.JNTConfigMap.put(nextRow1.getCell(i++).toString(), nextRow1.getCell(i--).toString());
		}
//		System.out.println(JNTConfig.JNTConfigMap);
		
		
}

}

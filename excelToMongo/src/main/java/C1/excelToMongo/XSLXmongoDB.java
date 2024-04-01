package C1.excelToMongo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class XSLXmongoDB {

    public static void main(String args[]) throws IOException {
        try {
            MongoClient mongo = new MongoClient( "localhost" , 27017 );
            System.out.println("Connected to the database successfully");
            //Accessing the database
            MongoDatabase database = mongo.getDatabase("TestDB");
            MongoCollection dbcoll = database.getCollection("employee");

            List<Document> empRecords = new ArrayList();
            File myFile = new File("/home/manjeet/empRecords.xlsx");
            FileInputStream fis = new FileInputStream(myFile);
            // Finds the workbook instance for XLSX file
            Workbook myWorkBook = new XSSFWorkbook(fis);
            // Return first sheet from the XLSX workbook
            Sheet mySheet = myWorkBook.getSheetAt(0);
            System.out.println(mySheet.getSheetName());
            String headerArr[] = new String[10];

            // Get iterator to all the rows in current sheet
            Iterator<Row> rowIterator = mySheet.iterator();
            Row headerRow = rowIterator.next();
            Iterator<Cell> headerCellIterator = headerRow.cellIterator();
            int i= 0;
            while (headerCellIterator.hasNext()){
                Cell headerCell = headerCellIterator.next();
                headerArr[i] = headerCell.toString();
                i++;
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Document emp = new Document();
                i=0;
                // For each row, iterate through each columns
                Iterator<Cell> cellIterator = row.cellIterator();
                    while (cellIterator.hasNext()) {
                        try {
                            Cell cell = cellIterator.next();
                            if(cell.getCellType() ==1)
                            emp.put(headerArr[i],cell.getStringCellValue());
                            else
                            emp.put(headerArr[i], (int)cell.getNumericCellValue());
                            i++;
                        }catch(Exception e){
                        }
                    }
                    empRecords.add(emp);
            }
            dbcoll.insertMany(empRecords);
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
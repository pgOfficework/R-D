package batchPerformanceRnD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainClass {

	public static void main(String[] args) throws SQLException, ClassNotFoundException {
		SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss"); 
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn=DriverManager.getConnection("jdbc:sqlserver://si-core-etl:1433;databaseName=DATAONE_DEV;user=bi_user1;password=sql");
//		PreparedStatement ps=conn.prepareStatement(Query.query);
//		int a=new Date().getSeconds();
//		System.out.println(a);
	//	Date d1=format.parse(startDate);
		
//		ResultSet rs=ps.executeQuery();
		int b=new Date().getSeconds();
	//	Date d2 =format.parse(startDate);
		System.out.println(b);
		long start = System.currentTimeMillis();
		System.out.println(start);
//		long seconds = (d2.getTime()-d1.getTime())/1000;
	}

}

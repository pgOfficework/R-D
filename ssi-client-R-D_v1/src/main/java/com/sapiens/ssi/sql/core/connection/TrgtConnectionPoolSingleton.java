package com.sapiens.ssi.sql.core.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class TrgtConnectionPoolSingleton {
	 private final static int MAX_CONNECTIONS = 4;
	    private static TrgtConnectionPoolSingleton instance = null;  
	    private static Connection[] connections = new Connection[MAX_CONNECTIONS];
		
	    private static int counter;

	    private TrgtConnectionPoolSingleton() { }
	    
	    public static TrgtConnectionPoolSingleton getInstance(String url,String user,String password) {
	    	 if(instance == null ) {
	        	synchronized(TrgtConnectionPoolSingleton.class) {
	                if(instance == null) {
	                    instance = new TrgtConnectionPoolSingleton();
	                    initializeConnections(url,user,password);
	                    counter = 0;
	                }
	            }
	        }

	        return instance;
	    }

	    private static void initializeConnections(String dbUrl,String user,String password) {
	        for(int i = 0; i < MAX_CONNECTIONS; i++) {
	            try {
	                connections[i] = DriverManager.getConnection(dbUrl,user,password);
	            } catch (SQLException e) {
	                e.printStackTrace();
	        }}
	    }

	    public static Connection getConnection() {
	        counter++;
	        if(counter == Integer.MAX_VALUE)
	            counter = 0;

	        return connections[counter%MAX_CONNECTIONS];
	    }
}

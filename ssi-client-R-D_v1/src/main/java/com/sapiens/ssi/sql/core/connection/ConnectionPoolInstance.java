package com.sapiens.ssi.sql.core.connection;

public class ConnectionPoolInstance {
	static TrgtConnectionPoolSingleton poolTrgt = null;

	public static TrgtConnectionPoolSingleton getTrgtPoolObj(String dbUrl,String user,String password){

		if(poolTrgt==null) {
			poolTrgt = TrgtConnectionPoolSingleton.getInstance(dbUrl,user,password);
		}
		return poolTrgt;
	}

}
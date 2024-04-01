package com.sapiens.ssi.client.singleton;

public class SqlCore {
    private static SqlCore sql_core = null;
    public String s;
 
    private SqlCore()
    {
    }
 
    public static SqlCore getInstance()
    {
        if (sql_core == null)
        	sql_core = new SqlCore();
 
        return sql_core;
    }
}

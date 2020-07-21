package com.cmwebgame;

import java.sql.Connection;
import java.sql.DriverManager;

import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;

public class JDBCConnection {
	//初始化連接
	public static Connection getConnection() throws Exception{
		Connection conn = null;
        String ClassForName = SystemGlobals.getValue(ConfigKeys.DATABASE_CONNECTION_DRIVER);
        String dbUrl = SystemGlobals.getValue(ConfigKeys.DATABASE_CONNECTION_STRING);
        try{
            Class.forName(ClassForName);
            conn=DriverManager.getConnection(dbUrl);
        } 
        catch(Exception e){
            e.printStackTrace();
        }
    	
    	return conn;
	}
}

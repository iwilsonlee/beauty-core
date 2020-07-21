/*
 * Copyright (c) CMWEBGAME Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution.
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 * 
 * This file creation date: 25/08/2004 23:03:14
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import java.sql.Connection;


import org.apache.log4j.Logger;

import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;

/**
 * Base class for all database connection implementations that
 * may be used with CMWEBGAME.
 * Default implementations are <code>PooledConnection</code>, which
 * is the defeault connection pool implementation, and <code>SimpleConnection</code>,
 * which opens a new connection on every request.  
 * 
 * @author Rafael Steil
 * @version $Id: DBConnection.java,v 1.14 2006/08/23 02:24:06 rafaelsteil Exp $
 */
public abstract class DBConnection
{
	private static final Logger logger = Logger.getLogger(DBConnection.class);
	protected boolean isDatabaseUp;
	protected boolean isDatabaseUp2;
	
	private static DBConnection instance;
	private static DBConnection instance2;

	/**
	 * Creates an instance of some <code>DBConnection </code>implementation. 
	 * 
	 * @return <code>true</code> if the instance was successfully created, 
	 * or <code>false</code> if some exception was thrown.
	 */
	public static boolean createInstance()
	{
		try {
			instance = (DBConnection)Class.forName(SystemGlobals.getValue(
					ConfigKeys.DATABASE_CONNECTION_IMPLEMENTATION)).newInstance();
		}
		catch (Exception e) {
			 logger.warn("Error creating the database connection implementation instance. " + e);
			 e.printStackTrace();
			 return false;
		}
		
		return true;
	}
	public static boolean createInstance2()
	{
		try {
			instance2 = (DBConnection)Class.forName(SystemGlobals.getValue(
					ConfigKeys.DATABASE_CONNECTION_IMPLEMENTATION2)).newInstance();
		}
		catch (Exception e) {
			logger.warn("Warnning： creating the database connection implementation instance. "
					+ "maybe you haven't defined the database.connection.implementation2 " + e);
			//e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the current <code>DBConnection</code> implementation's instance
	 * 
	 * @return DBConnection
	 */
	public static DBConnection getImplementation()
	{
		return instance;
	}
	public static DBConnection getImplementation2()
	{
		return instance2;
	}
	
	/**
	 * Checks if database connection is up.
	 *  
	 * @return <code>true</code> if a connection to the database
	 * was successfully created, or <code>false</code> if not.
	 */
	public boolean isDatabaseUp()
	{
		return this.isDatabaseUp;
	}
	public boolean isDatabaseUp2()
	{
		return this.isDatabaseUp2;
	}
	
	/**
	 * Inits the implementation. 
	 * Connection pools may use this method to init the connections from the
	 * database, while non-pooled implementation can provide an empty method
	 * block if no other initialization is necessary.
	 * <br>
	 * Please note that this method will be called just once, at system startup. 
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;
	
	/**
	 * Gets a connection.
	 * Connection pools' normal behaviour will be to once connection
	 * from the pool, while non-pooled implementations will want to
	 * go to the database and get the connection in time the method
	 * is called.
	 * 
	 * @return Connection
	 */
	public abstract Connection getConnection();
	
	/**
	 * Releases a connection.
	 * Connection pools will want to put the connection back to the pool list,
	 * while non-pooled implementations should call <code>close()</code> directly
	 * in the connection object.
	 * 
	 * @param conn The connection to release
	 */
	public abstract void releaseConnection(Connection conn);
	
	/**
	 * Close all open connections.
	 * 
	 * @throws Exception
	 */
	public abstract void realReleaseAllConnections() throws Exception;
}

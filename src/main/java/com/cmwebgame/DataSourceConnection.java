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
 * Created on Jan 7, 2005 7:44:40 PM
 *
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import java.sql.Connection;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.cmwebgame.exceptions.DatabaseException;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;


/**
 * DataSource connection implementation for CMWEBGAME.
 * The datasourcename should be set in the key 
 * <code>database.datasource.name</code> at 
 * SystemGlobals.properties.
 * 
 * @author Rafael Steil
 * @version $Id: DataSourceConnection.java,v 1.10 2006/08/23 02:24:05 rafaelsteil Exp $
 */
public class DataSourceConnection extends DBConnection
{
	private DataSource ds;
	
	/**
	 * @see com.cmwebgame.DBConnection#init()
	 */
	public void init() throws Exception 
	{
		System.out.println("=======================================DataSource");
		Context context = new InitialContext();
		this.ds = (DataSource)context.lookup(SystemGlobals.getValue(
				ConfigKeys.DATABASE_DATASOURCE_NAME));
	}
	/**
	 * @see com.cmwebgame.DBConnection#getConnection()
	 */
	public Connection getConnection()
	{
		try {
			return this.ds.getConnection();
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new DatabaseException(e);
		}
	}

	/**
	 * @see com.cmwebgame.DBConnection#releaseConnection(java.sql.Connection)
	 */
	public void releaseConnection(Connection conn)
	{
        if (conn==null) {
            return;
        }
		try {
			conn.close();
		}
		catch (Exception e) {
            // catch error of close of connection
        }
	}

	/**
	 * @see com.cmwebgame.DBConnection#realReleaseAllConnections()
	 */
	public void realReleaseAllConnections() throws Exception {}
}

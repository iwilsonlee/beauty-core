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
 * Created on 30/11/2005 17:07:51
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.cmwebgame.exceptions.DatabaseException;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;
import com.google.common.collect.Lists;

/**
 * Druid连接池操作类
 * @see https://github.com/alibaba/druid
 * @author wilson
 */
public class PooledDruidConnection2 extends DBConnection
{
	private DruidDataSource ds;
	
	/**
	 * 
	 * @see com.cmwebgame.DBConnection#init()
	 */
	public void init() throws Exception
	{
		this.ds = new DruidDataSource();
		
		this.ds.setName("beauty-core_connection2");
		this.ds.setDriverClassName(SystemGlobals.getValue(ConfigKeys.DATABASE_CONNECTION_DRIVER2));
		this.ds.setUrl(SystemGlobals.getValue(ConfigKeys.DATABASE_CONNECTION_STRING2));
		this.ds.setMinIdle(SystemGlobals.getIntValue(ConfigKeys.DATABASE_POOL_MIN2));
		this.ds.setMaxActive(SystemGlobals.getIntValue(ConfigKeys.DATABASE_POOL_MAX2));
		this.ds.setInitialSize(2);
		this.ds.setMaxWait(SystemGlobals.getIntValue(ConfigKeys.DATABASE_MAXIDLETIME2));
		this.ds.setUseUnfairLock(true);//使用非公平锁
		this.ds.setValidationQuery("select * test_table limit 1");
		this.ds.setTestWhileIdle(true);
		this.ds.setTimeBetweenEvictionRunsMillis(60*1000L);
		
		// 以下开启慢速sql的log记录功能(会消耗一定的性能)，start
		this.ds.setFilters("log4j");// 使用Logger
		StatFilter sfFilter = new StatFilter();
		sfFilter.setSlowSqlMillis(10);// 慢速sql查询，设置慢速值为10秒
		sfFilter.setLogSlowSql(true);// 记录慢速sql到log
		sfFilter.setMergeSql(true);// 使用sql合并统计
		List<Filter> list = Lists.newArrayList();
		list.add(sfFilter);
		this.ds.setProxyFilters(list);
		// end
		
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
			e.printStackTrace();
		}
	}

	/**
	 * @see com.cmwebgame.DBConnection#realReleaseAllConnections()
	 */
	public void realReleaseAllConnections() throws Exception
	{
		if(this.ds != null)
		    this.ds.close();
	}
}

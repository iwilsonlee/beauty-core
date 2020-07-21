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
 * This file creation date: 29/01/2006 - 12:19:11
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.cmwebgame.context.PortalContext;
import com.cmwebgame.context.RequestContext;
import com.cmwebgame.context.ResponseContext;
import com.cmwebgame.exceptions.PortalException;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;

/**
 * Data execution context. 
 * 
 * @author Rafael Steil
 * @version $Id: CMWEBGAMEExecutionContext.java,v 1.10 2006/10/10 01:59:55 rafaelsteil Exp $
 */
public class GPortalExecutionContext
{
    private static ThreadLocal userData = new ThreadLocal();
	private static Logger logger = Logger.getLogger(GPortalExecutionContext.class);
	private static Configuration templateConfig;
	
	private Connection conn;
	private Connection conn2;//number 2 database connection
    private PortalContext forumContext;
    private SimpleHash context = new SimpleHash(ObjectWrapper.BEANS_WRAPPER);
    private String redirectTo;
    private String renderView;
    private String contentType;
    private boolean isCustomContent;
    private boolean enableRollback;
    
	
	/**
	 * Gets the execution context.
	 * @return CMWEBGAMEExecutionContext
	 */
	public static GPortalExecutionContext get()
	{
		GPortalExecutionContext ex = (GPortalExecutionContext)userData.get();

		if (ex == null) {
			ex = new GPortalExecutionContext();
			userData.set(ex);
		}
		
		return ex;
	}
	
	/**
	 * Checks if there is an execution context already set
	 * @return <code>true</code> if there is an execution context
	 * @see #get()
	 */
	public static boolean exists()
	{
		return (userData.get() != null);
	}
	
	/**
	 * Sets the default template configuration 
	 * @param config The template configuration to set
	 */
	public static void setTemplateConfig(Configuration config)
	{
		templateConfig = config;
	}
	
	/**
	 * Gets a reference to the default template configuration settings.
	 * @return The template configuration instance
	 */
	public static Configuration templateConfig()
	{
		return templateConfig;
	}
	
	/**
	 * Sets the execution context
	 * @param ex CMWEBGAMEExecutionContext
	 */
	public static void set(GPortalExecutionContext ex)
	{
		userData.set(ex);
	}
	
	/**
	 * Sets a connection
	 * @param conn The connection to use
	 */
	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}
	
	public static Connection getConnection2() {
		return getConnection2(true);
	}

	public void setConnection2(Connection conn2) {
		this.conn2 = conn2;
	}

	/**
	 * Gets the current thread's connection
	 * @return Connection
	 */
	public static Connection getConnection() 
	{
		return getConnection(true);
	}
	
	public static Connection getConnection(boolean validate)
	{
		GPortalExecutionContext ex = get();
		Connection c =  ex.conn;
		if (validate && c == null) {
			c = DBConnection.getImplementation().getConnection();
			
			try {
				c.setAutoCommit(!SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS));
			}
			catch (Exception e) {
                //catch error autocommit
            }
			ex.setConnection(c);
			set(ex);
		}
	    
		return c; 
	}
	public static Connection getConnection2(boolean validate)
	{
		GPortalExecutionContext ex = get();
		Connection c =  ex.conn2;
		
		if (validate && c == null) {
			c = DBConnection.getImplementation2().getConnection();
			
			try {
				c.setAutoCommit(!SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS2));
			}
			catch (Exception e) {
				//catch error autocommit
			}
			
			ex.setConnection2(c);
			set(ex);
		}
		
		return c; 
	}

    public static PortalContext getForumContext()
    {
        return ((GPortalExecutionContext)userData.get()).forumContext;
    }

    public void setForumContext(PortalContext forumContext)
    {
        this.forumContext = forumContext;
    }

    /**
	 * Gets the current thread's request
	 * @return WebContextRequest
	 */
	public static RequestContext getRequest() {
		return getForumContext().getRequest();
	}
	
	/**
	 * Gets the current thread's response
	 * @return HttpServletResponse
	 */
	public static ResponseContext getResponse() {
		return getForumContext().getResponse();
	}

	/**
	 * Gets the current thread's template context
	 * @return SimpleHash
	 */
	public static SimpleHash getTemplateContext() {
		return ((GPortalExecutionContext)userData.get()).context;
	}

	/**
	 * Gets the current thread's <code>DataHolder</code> instance
     * @param redirect String
     */
	public static void setRedirect(String redirect) {
		((GPortalExecutionContext)userData.get()).redirectTo = redirect;
	}
	
	public static void setRenderView(String renderView) {
		((GPortalExecutionContext)userData.get()).renderView = renderView;
	}

	/**
	 * Sets the content type for the current http response.
	 * @param contentType String
	 */
	public static void setContentType(String contentType) {
		((GPortalExecutionContext)userData.get()).contentType = contentType;
	}
	
	/**
	 * Gets the content type for the current request.
	 * @return String
	 */
	public static String getContentType()
	{
		return ((GPortalExecutionContext)userData.get()).contentType;
	}

	/**
	 * Gets the URL to redirect to, if any.
	 * @return The URL to redirect, of <code>null</code> if none.
	 */
	public static String getRedirectTo()
	{
		GPortalExecutionContext ex = (GPortalExecutionContext)userData.get();
		return (ex != null ? ex.redirectTo : null);
	}
	
	public static String getRenderView()
	{
		GPortalExecutionContext ex = (GPortalExecutionContext)userData.get();
		return (ex != null ? ex.renderView : null);
	}

	/**
	 * Marks the request to use a binary content-type.
	 * @param enable boolean
	 */
	public static void enableCustomContent(boolean enable) {
		((GPortalExecutionContext)userData.get()).isCustomContent = enable;
	}
	
	/**
	 * Checks if the current request is binary
	 * @return <code>true</code> if the content tyee for the current request is 
	 * any binary data.
	 */
	public static boolean isCustomContent()
	{
		return ((GPortalExecutionContext)userData.get()).isCustomContent;
	}

	/**
	 * Forces the request to not commit the connection.
	 */
	public static void enableRollback() {
		((GPortalExecutionContext)userData.get()).enableRollback = true;
	}

	/**
	 * Check if commit is disabled or not for the current request.
	 * @return <code>true</code> if a commit should NOT be made
	 */
	public static boolean shouldRollback() {
		return ((GPortalExecutionContext)userData.get()).enableRollback;
	}

    /**
     * Send UNAUTHORIZED to the browser and ask user to login via basic authentication
     */
	public static void requestBasicAuthentication()  
	{
		getResponse().addHeader("WWW-Authenticate", "Basic realm=\"CMWEBGAME\"");
		
		try {
			getResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
		catch (IOException e) {
			throw new PortalException(e);
		}
		
		enableCustomContent(true);
    }
	
	/**
	 * Finishes the execution context
	 */
	public static void finish()
	{
		Connection conn = GPortalExecutionContext.getConnection(false);
		
		try {
			if (conn != null) {
				logger.warn("the connection isClosed status is " + conn.isClosed());
				if (SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS)) {
					if (GPortalExecutionContext.shouldRollback()) {
						conn.rollback();
					}
					else {
						conn.commit();
					}
				}
				
			}else {
				logger.debug("finish connection is ==" + conn);
			}
			DBConnection.getImplementation().releaseConnection(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		userData.set(null);
	}
}

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
 * Created on 26/08/2006 21:56:05
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame.context.standard;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;

import com.cmwebgame.context.RequestContext;
import com.cmwebgame.context.SessionContext;


/**
 * Request context non-dependent of HTTP 
 * @author Rafael Steil
 * @version $Id: StandardRequestContext.java,v 1.6 2007/09/20 16:07:09 rafaelsteil Exp $
 */
public class StandardRequestContext implements RequestContext
{
	private Hashtable data;
	private SessionContext sessionContext;
	
	public StandardRequestContext()
	{
		this.data = new Hashtable();
		this.sessionContext = new StandardSessionContext();
	}
	
	/**
	 * @see com.cmwebgame.context.RequestContext#addParameter(java.lang.String, java.lang.Object)
	 */
	public void addParameter(String name, Object value)
	{
		if (this.data.contains(name)) {
			this.data.remove(name);
		}
		
		this.data.put(name, value);
	}
	
	/**
	 * @see com.cmwebgame.context.RequestContext#addOrReplaceParameter(java.lang.String, java.lang.Object)
	 */
	public void addOrReplaceParameter(String name, Object value) 
	{
		this.addParameter(name, value);
	}

	public RequestDispatcher getRequestDispatcher(String viewName){
    	return null;
    }
	
	/**
	 * @see com.cmwebgame.context.RequestContext#getAction()
	 */
	public String getAction()
	{
		
		return null;
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String name)
	{
		return this.getParameter(name);
	}

	/**
	 * This method will always return null
	 */
	public String getContextPath()
	{
		return null;
	}

	/**
	 * This method will always return null
	 */
	public Cookie[] getCookies()
	{
		return null;
	}

	/**
	 * This method will always return null
	 */
	public String getHeader(String name)
	{
		return null;
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getIntParameter(java.lang.String)
	 */
	public int getIntParameter(String parameter)
	{
		return Integer.parseInt(this.getParameter(parameter));
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getModule()
	 */
	public String getModule()
	{
		
		return null;
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getObjectParameter(java.lang.String)
	 */
	public Object getObjectParameter(String parameter)
	{
		return this.data.get(parameter);
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getParameter(java.lang.String)
	 */
	public String getParameter(String name)
	{
		Object value = this.data.get(name);
		return value != null ? value.toString() : null;
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getParameterNames()
	 */
	public Enumeration getParameterNames()
	{
		return this.data.elements();
	}

	/**
	 * This method will always return null;
	 */
	public String[] getParameterValues(String name)
	{
		return null;
	}

	/**
	 * This method will always return null
	 */
	public String getQueryString()
	{
		return null;
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getRemoteAddr()
	 */
	public String getRemoteAddr()
	{
		
		return null;
	}

	/**
	 * This method will always return null
	 */
	public String getRemoteUser()
	{
		return null;
	}
	/**
	 * This method will always return null
	 */
	public String getRemoteEmail()
	{
		return null;
	}

	/**
	 * This method will always return null
	 */
	public String getRequestURI()
	{
		return null;
	}

	/**
	 * This method will always return null
	 */
	public String getScheme()
	{
		return null;
	}

	/**
	 * This method will always return null
	 */
	public String getServerName()
	{
		return null;
	}

	/**
	 * This method will always return 0
	 */
	public int getServerPort()
	{
		return 0;
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#getSessionContext()
	 */
	public SessionContext getSessionContext()
	{
		return this.sessionContext;
	}

	/**
	 * This method is equal to {@link #getSessionContext()}
	 */
	public SessionContext getSessionContext(boolean create)
	{
		return this.getSessionContext();
	}

	/**
	 * @see com.cmwebgame.context.RequestContext#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String name)
	{
		this.data.remove(name);
	}

	/**
	 * This method is equal to {@link #addParameter(String, Object)}
	 */
	public void setAttribute(String name, Object o)
	{
		this.addParameter(name, o);
	}

	/**
	 * This method does nothing 
	 */
	public void setCharacterEncoding(String env) throws UnsupportedEncodingException {}

	public Locale getLocale() {
		
		return null;
	}
}

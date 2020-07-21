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
 * Created on Mar 17, 2005 5:38:11 PM
 *
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import org.apache.log4j.Logger;

import com.cmwebgame.context.PortalContext;
import com.cmwebgame.context.RequestContext;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;

import freemarker.template.SimpleHash;

/**
 * Common methods used by the controller.
 * 
 * @author wilson
 * @version $Id: ControllerUtils.java,v 1.38 2007/08/01 22:30:06 rafaelsteil Exp $
 */
public class ControllerUtils {
	
	private static final Logger logger = Logger.getLogger(ControllerUtils.class);
	/**
	 * Setup common variables used by almost all templates.
	 * 
	 * @param context SimpleHash The context to use
	 * @param jforumContext GPortalContext
	 */
	public void prepareJSPContext(ServletContext context, PortalContext portalContext) {
		RequestContext request = GPortalExecutionContext.getRequest();

		//		context.put("karmaEnabled", SecurityRepository.canAccess(SecurityConstants.PERM_KARMA_ENABLED));
		context.setAttribute("dateTimeFormat", SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		context.setAttribute("autoLoginEnabled", SystemGlobals.getBoolValue(ConfigKeys.AUTO_LOGIN_ENABLED));
		context.setAttribute("sso", ConfigKeys.TYPE_SSO.equals(SystemGlobals.getValue(ConfigKeys.AUTHENTICATION_TYPE)));
		context.setAttribute("contextPath", request.getContextPath());
		context.setAttribute("serverName", request.getServerName());
		context.setAttribute("templateName", SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR));
		context.setAttribute("extension", SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		context.setAttribute("serverPort", Integer.toString(request.getServerPort()));
		context.setAttribute("version", SystemGlobals.getValue(ConfigKeys.VERSION));

		context.setAttribute("homepageLink", SystemGlobals.getValue(ConfigKeys.HOMEPAGE_LINK));

		context.setAttribute("encoding", SystemGlobals.getValue(ConfigKeys.ENCODING));
		//		context.put("bookmarksEnabled", SecurityRepository.canAccess(SecurityConstants.PERM_BOOKMARKS_ENABLED));
		//		context.put("canAccessModerationLog", SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_LOG));
		context.setAttribute("GPortalContext", portalContext);
		context.setAttribute("timestamp", new Long(System.currentTimeMillis()));
	}

	public void prepareTemplateContext(SimpleHash context, PortalContext portalContext) {
		RequestContext request = GPortalExecutionContext.getRequest();

		//		context.put("karmaEnabled", SecurityRepository.canAccess(SecurityConstants.PERM_KARMA_ENABLED));
		context.put("dateTimeFormat", SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		context.put("autoLoginEnabled", SystemGlobals.getBoolValue(ConfigKeys.AUTO_LOGIN_ENABLED));
		context.put("sso", ConfigKeys.TYPE_SSO.equals(SystemGlobals.getValue(ConfigKeys.AUTHENTICATION_TYPE)));
		context.put("contextPath", request.getContextPath());
		context.put("serverName", request.getServerName());
		context.put("templateName", SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR));
		context.put("extension", SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		context.put("serverPort", Integer.toString(request.getServerPort()));
		context.put("version", SystemGlobals.getValue(ConfigKeys.VERSION));
		context.put("homepageLink", SystemGlobals.getValue(ConfigKeys.HOMEPAGE_LINK));
		context.put("encoding", SystemGlobals.getValue(ConfigKeys.ENCODING));
		//		context.put("bookmarksEnabled", SecurityRepository.canAccess(SecurityConstants.PERM_BOOKMARKS_ENABLED));
		//		context.put("canAccessModerationLog", SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_LOG));
		context.put("GPortalContext", portalContext);
		context.put("timestamp", new Long(System.currentTimeMillis()));
	}

	

	
	
	
	

	/**
	 * Gets a cookie by its name.
	 * 
	 * @param name The cookie name to retrieve
	 * @return The <code>Cookie</code> object if found, or <code>null</code> oterwhise
	 */
	public static Cookie getCookie(String name) {
		Cookie[] cookies = GPortalExecutionContext.getRequest().getCookies();

		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				Cookie c = cookies[i];

				if (c.getName().equals(name)) {
					return c;
				}
			}
		}

		return null;
	}

	/**
	 * Template method to get a cookie.
	 * Useful to situations when a subclass
	 * wants to have a different way to 
	 * retrieve a cookie.
	 * @param name The cookie name to retrieve
	 * @return The Cookie object if found, or null otherwise
	 * @see #getCookie(String)
	 */
	protected Cookie getCookieTemplate(String name) {
		return ControllerUtils.getCookie(name);
	}

	/**
	 * Add or update a cookie. This method adds a cookie, serializing its value using XML.
	 * 
	 * @param name The cookie name.
	 * @param value The cookie value
	 */
	public static void addCookie(String name, String value) {
		int maxAge = -1;

		if (value == null) {
			maxAge = 0;
			value = "";
		}

		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		cookie.setDomain(SystemGlobals.getValue(ConfigKeys.COOKIE_DOMAIN));
//		cookie.setDomain(SystemGlobals.getValue(ConfigKeys.COOKIE_SPREAD_DOMAIN));
		GPortalExecutionContext.getResponse().addCookie(cookie);
	}

	/**
	 * Template method to add a cookie.
	 * Useful to suatins when a subclass wants to add
	 * a cookie in a fashion different than the normal 
	 * behaviour
	 * @param name The cookie name
	 * @param value The cookie value
	 * @see #addCookie(String, String)
	 */
	protected void addCookieTemplate(String name, String value) {
		ControllerUtils.addCookie(name, value);
	}
}

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
 * This file creation date: Mar 3, 2003 / 10:55:19 AM
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.cmwebgame.context.RequestContext;
import com.cmwebgame.context.ResponseContext;
import com.cmwebgame.exceptions.PortalException;
import com.cmwebgame.exceptions.TemplateNotFoundException;
import com.cmwebgame.repository.Tpl;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals;
import com.cmwebgame.util.preferences.TemplateKeys;
import com.google.common.base.Objects;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * <code>Command</code> Pattern implementation.
 * All View Helper classes, which are intead to configure and processs
 * presentation actions must extend this class. 
 * 
 * @author Rafael Steil
 * @version $Id: Command.java,v 1.27 2007/07/28 14:17:11 rafaelsteil Exp $
 */
public abstract class Command 
{
	private static Class[] NO_ARGS_CLASS = new Class[0];
	private static Object[] NO_ARGS_OBJECT = new Object[0];
	
	private boolean ignoreAction;
	
	protected String templateName;
	protected String viewName;
	protected RequestContext request;
	protected ResponseContext response;
	protected SimpleHash context;
	protected ServletContext servletContext;
	
	protected void setTemplateName(String templateName)
	{
		this.templateName = Tpl.name(templateName);
	}
	
	protected void setViewName(String viewName)
	{
		this.viewName = viewName;
	}
	
	protected void ignoreAction()
	{
		this.ignoreAction = true;
	}
	
	
	/**
	 * 默認首頁
	 */
	public void home(){} ;
	
	public abstract void list() ;
	/**
	 * sendRedirect
	 * @param model
	 * @param action
	 * @param parameters
	 */
	public void setRedirect(String model, String action, String parameters)
	{
		String path = this.request.getContextPath() + "/" + model + "/" + action;
		path += SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) + parameters;
		
		GPortalExecutionContext.setRedirect(path);
	}
	
	/**
	 * Beetl模板渲染
	 * @param req
	 * @param request
	 * @param response
	 * @param context
	 * @return
	 */
	public String processBeetlTamplete(HttpServletRequest req,RequestContext request,
			ResponseContext response,ServletContext context){
		this.request = request;
		this.response = response;
		this.servletContext = context;
		this.invokeAction(this.request);
		if (GPortalExecutionContext.getRedirectTo() != null) {
			this.setViewName("");
		}else if(req.getAttribute("viewName") != null){
			this.setViewName((String)req.getAttribute("viewName"));
		}
		
		if (GPortalExecutionContext.isCustomContent()) {
			return null;
		}
		return this.viewName;
	}
	
	/**
	 * handlebars模板渲染
	 * @param req
	 * @param request
	 * @param response
	 * @param context
	 * @return
	 */
	public String processHandlebarsTamplete(HttpServletRequest req,RequestContext request,
			ResponseContext response,ServletContext context){
		this.request = request;
		this.response = response;
		this.servletContext = context;
		this.invokeAction(this.request);
		if (GPortalExecutionContext.getRedirectTo() != null) {
			this.setViewName("");
		}else if(req.getAttribute("viewName") != null){
			this.setViewName((String)req.getAttribute("viewName"));
		}
		
		if (GPortalExecutionContext.isCustomContent()) {
			return null;
		}
		return this.viewName;
	}
	
	/**
	 * jsp渲染
	 * @param req
	 * @param request
	 * @param response
	 * @param context
	 * @return
	 */
	public RequestDispatcher getRequestDispatcher(HttpServletRequest req,RequestContext request,
			ResponseContext response,ServletContext context){
		this.request = request;
		this.response = response;
		this.servletContext = context;
		
		this.invokeAction(this.request);
		
		if (GPortalExecutionContext.getRedirectTo() != null) {
			this.setViewName("");
		}else if(req.getAttribute("viewName") != null){
			this.setViewName((String)req.getAttribute("viewName"));
		}
		
		if (GPortalExecutionContext.isCustomContent()) {
			return null;
		}
    	return req.getRequestDispatcher(this.viewName);
    }
	/**
	 * freemarker渲染
	 * Process and manipulate a requisition.
	 * @return <code>Template</code> reference
     * @param request WebContextRequest
     * @param response WebContextResponse
	 */
	public Template process(RequestContext request, ResponseContext response, SimpleHash context)
	{
		this.request = request;
		this.response = response;
		this.context = context;
		
		this.invokeAction(this.request);
		
		if (GPortalExecutionContext.getRedirectTo() != null) {
			this.setTemplateName(TemplateKeys.EMPTY);
		}
		else if (request.getAttribute("template") != null) {
			this.setTemplateName((String)request.getAttribute("template"));
		}
		
		if (GPortalExecutionContext.isCustomContent()) {
			return null;
		}
		
		if (this.templateName == null) {
			throw new TemplateNotFoundException("Template for action " + this.request.getAction() + " is not defined");
		}

        try {
            return GPortalExecutionContext.templateConfig().getTemplate(
                new StringBuffer(SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR)).
                append('/').append(this.templateName).toString());
        }
        catch (IOException e) {
            throw new PortalException( e);
        }
    }
	
	/**
	 * 根据请求执行action方法
	 * @author wilson
	 * @param request
	 */
	private void invokeAction(RequestContext request){
		String action = request.getAction();

		if (!this.ignoreAction) {
			try {
				this.getClass().getMethod(action, NO_ARGS_CLASS).invoke(this, NO_ARGS_OBJECT);
			}
			catch (NoSuchMethodException e) {
				this.home();		
			}
			catch (Exception e)
            {
                throw new PortalException(e);
			}
		}
	}
}

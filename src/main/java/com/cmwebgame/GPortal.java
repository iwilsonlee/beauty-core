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
 * This file creation date: Mar 3, 2003 / 11:43:35 AM
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.beetl.ext.servlet.ServletGroupTemplate;

import com.cmwebgame.context.GPortalContext;
import com.cmwebgame.context.RequestContext;
import com.cmwebgame.context.ResponseContext;
import com.cmwebgame.context.web.WebRequestContext;
import com.cmwebgame.context.web.WebResponseContext;
import com.cmwebgame.exceptions.ExceptionWriter;
import com.cmwebgame.exceptions.PortalStartupException;
import com.cmwebgame.handle.GlobalHandle;
import com.cmwebgame.handle.MemberSessionHandle;
import com.cmwebgame.repository.ModulesRepository;
import com.cmwebgame.template.BwebRender;
import com.cmwebgame.template.HandlebarsRender;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.ImplGlobals;
import com.cmwebgame.util.preferences.SystemGlobals;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.ServletContextTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.base.Strings;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * Front Controller.
 * 
 * @author Rafael Steil
 * @version $Id: CMWEBGAME.java,v 1.116 2007/10/10 04:54:20 rafaelsteil Exp $
 */
public class GPortal extends GPortalBaseServlet {
	
	private static Logger logger = Logger.getLogger(GPortal.class);
	
	private static boolean isDatabaseUp;
	
	

	/**
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		super.startApplication();

		// Start database 初始化数据库连接服务
		isDatabaseUp = PortalStartup.startDatabase();
		
		try {
			Connection conn = DBConnection.getImplementation().getConnection();
			conn.setAutoCommit(!SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS));
			
			Connection conn2 = null;
			if(DBConnection.getImplementation2() != null){
				conn2 = DBConnection.getImplementation2().getConnection();
				conn2.setAutoCommit(!SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS2));
			}
			
			
			// Try to fix some MySQL problems
//			MySQLVersionWorkarounder dw = new MySQLVersionWorkarounder();
//			dw.handleWorkarounds(conn);

			// Continues loading the project
			GPortalExecutionContext ex = GPortalExecutionContext.get();
			ex.setConnection(conn);
			ex.setConnection2(conn2);
			GPortalExecutionContext.set(ex);
			// Init general project stuff启动缓存
//			PortalRepository.loadGames();
//			PortalRepository.loadServers();
//			PortalRepository.loadCounties();
//			
//			PaymentAPIRepository.loadPaymentsAPI();
//			VipSettingRepository.loadVipSetting();
//			RankingSettingRepository.loadRankingSetting();
//			ConsigneeInfoRepository.loadConsigneeInfos();
//			ArticleRepository.loadAboutArticles();
//			AdvertisingRepository.loadAdvertising();
//			GuildInfoRepository.loadGuildInfoes();
//			GameHhdhRepository.loadTaxis();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new PortalStartupException("Error while starting gportal", e);
		} finally {
			GPortalExecutionContext.finish();
		}
		
		ConfigLoader.startGroupBuyCheckService();//启动团购检查服务
	}

	/**
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

		Writer out = null;
		GPortalContext portalContext = null;
		RequestContext request = null;
		ResponseContext response = null;
		String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);

		try {
			// Initializes the execution context
			GPortalExecutionContext ex = GPortalExecutionContext.get();

			request = new WebRequestContext(req);
			response = new WebResponseContext(res);

			this.checkDatabaseStatus();

			portalContext = new GPortalContext(request.getContextPath(), SystemGlobals
					.getValue(ConfigKeys.SERVLET_EXTENSION), request, response);
			ex.setForumContext(portalContext);

			GPortalExecutionContext.set(ex);

			// Setup stuff
//			SimpleHash context = GPortalExecutionContext.getTemplateContext();
			ServletContext context = this.getServletContext();
			ControllerUtils utils = new ControllerUtils();
			/*此段代码用下面的MemberSessionHandle.RefreshAndSetAttribute实现
			utils.refreshSession();
			context.setAttribute("logged", MemberSessionFacade.isLogged());
			*/
			Object object = ImplGlobals.getHandle("MemberSessionHandleImpl");
			MemberSessionHandle  memberSessionHandle = null;
			if(object != null){//此处定义每次访问都刷新MemberSession状态
				memberSessionHandle = (MemberSessionHandle)object ;
				memberSessionHandle.refreshSession();
			}else {
				System.out.println("Warnning: the class MemberSessionHandleImpl is not found! and the MemberSessionHandle.refreshSession() is not executed!");
			}
			
			

			// Process security data
//			if(MemberSessionFacade.getMemberSession().getMemberId() != null ){
//				SecurityRepository.load(MemberSessionFacade.getMemberSession().getMemberId().intValue());
//			}
			

			utils.prepareJSPContext(context, portalContext);

			String module = request.getModule();

			// Gets the module class name
			String moduleClass = module != null ? ModulesRepository.getModuleClass(module) : null;

			if (moduleClass == null) {
				// Module not found, send 404 not found response
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			} else {
//				boolean shouldBan = this.shouldBan(request.getRemoteAddr());

//				if (!shouldBan) {
					context.setAttribute("moduleName", module);
					String teString = request.getAction();
					context.setAttribute("action", teString);
//				} else {
//					moduleClass = ModulesRepository.getModuleClass("index");
//					context.setAttribute("moduleName", "index");
//					((WebRequestContext) request).changeAction("banned");
//				}

				if ( SystemGlobals.getBoolValue(ConfigKeys.BANLIST_SEND_403FORBIDDEN)) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
				} else {
//					context.put("language", I18n.getUserLanguage());
					if(memberSessionHandle != null){//每次访问都把最新的MemberSession赋值到上下文中
						memberSessionHandle.setMemberSessionToContext(context);
					}
					context.setAttribute("request", req);
					context.setAttribute("response", response);
					
					//					out = this.processCommand(out, request, response, encoding, context, moduleClass);
//					this.processCommandJSP(out, request, response, req, res, encoding, context, moduleClass);
					
					Object globalHandleObject = ImplGlobals.getHandle("GlobalHandleImpl");
					GlobalHandle  globalHandle = null;
					boolean isRedirect = false;
					if(globalHandleObject != null){//
						globalHandle = (GlobalHandle)globalHandleObject ;
						//设置必要的全局变量
						globalHandle.setAttribute(request);
						//检查request请求，在此位置进行检查的目的是为了能使用GPortalExecutionContext，这样就能使用cache或dao之类
						isRedirect = globalHandle.checkRequest(request, response);
					}else {
						System.out.println("Warnning: the class AttributeHandleImpl is not found! and the AttributeSettingHandle.setAttribute is not executed!");
					}
					
					if (isRedirect) {
						return;
					}else {
						this.processCommandBeetl(out, request, response, req, res, encoding, context, moduleClass);
					}
					
				}
			}
		} catch (Exception e) {
			this.handleException(out, response, encoding, e, request);
		} finally {
			this.handleFinally(out, portalContext, response);
		}
	}

	/**
	 * 使用jsp模板
	 * @author Wilson
	 * @param out
	 * @param request
	 * @param response
	 * @param req
	 * @param res
	 * @param encoding
	 * @param context
	 * @param moduleClass
	 * @throws Exception
	 */
	private void processCommandJSP(Writer out, RequestContext request, ResponseContext response,
			HttpServletRequest req, HttpServletResponse res, String encoding, ServletContext context, String moduleClass)
			throws Exception {
		// Here we go, baby
		Command c = this.retrieveCommand(moduleClass);
		RequestDispatcher rd = c.getRequestDispatcher(req, request, response,context);
		//		Template template = c.process(request, response, context);
		if (GPortalExecutionContext.getRedirectTo() == null) {
			String contentType = GPortalExecutionContext.getContentType();

			if (contentType == null) {
				contentType = "text/html; charset=" + encoding;
			}

			response.setContentType(contentType);

			// Binary content are expected to be fully 
			// handled in the action, including outputstream
			// manipulation
			if (!GPortalExecutionContext.isCustomContent()) {
				//				OutputStreamWriter os = new OutputStreamWriter(response.getOutputStream(), encoding);
				//				out = new BufferedWriter(os);
				//				template.process(GPortalExecutionContext.getTemplateContext(), out);
				//				out.flush();
				if(rd != null){
					rd.forward(req, res);
				}
				
			}
		}
	}
	
	/**
	 * 使用beetl模板
	 * @author Wilson
	 * @param out
	 * @param request
	 * @param response
	 * @param req
	 * @param res
	 * @param encoding
	 * @param context
	 * @param moduleClass
	 * @throws Exception
	 */
	private void processCommandBeetl(Writer out, RequestContext request, ResponseContext response,
			HttpServletRequest req, HttpServletResponse res, String encoding, ServletContext context, String moduleClass)
					throws Exception {
		// Here we go, baby
		Command c = this.retrieveCommand(moduleClass);
		String template = c.processBeetlTamplete(req, request, response, context);
		if (GPortalExecutionContext.getRedirectTo() == null && !Strings.isNullOrEmpty(template)) {
			String contentType = GPortalExecutionContext.getContentType();

			if (contentType == null) {
				contentType = "text/html; charset=" + encoding;
			}

			response.setContentType(contentType);

			// Binary content are expected to be fully 
			// handled in the action, including outputstream
			// manipulation
			if (!GPortalExecutionContext.isCustomContent()) {
				logger.info("visiting the view name is : " + template);
				
				BwebRender webRender = new BwebRender(ServletGroupTemplate.instance().getGroupTemplate());
				webRender.render(template, req, res, context, request.getParameterNames());
			}
		}

	}
	
	/**
	 * 使用handlebars模板
	 * @param out
	 * @param request
	 * @param response
	 * @param req
	 * @param res
	 * @param encoding
	 * @param context
	 * @param moduleClass
	 * @throws Exception
	 */
	private void processCommandHandlebars(Writer out, RequestContext request, ResponseContext response,
			HttpServletRequest req, HttpServletResponse res, String encoding, ServletContext context, String moduleClass)
					throws Exception {
		// Here we go, baby
		Command c = this.retrieveCommand(moduleClass);
		String template = c.processHandlebarsTamplete(req, request, response, context);
		if (GPortalExecutionContext.getRedirectTo() == null) {
			String contentType = GPortalExecutionContext.getContentType();
			
			if (contentType == null) {
				contentType = "text/html; charset=" + encoding;
			}
			
			response.setContentType(contentType);
			
			// Binary content are expected to be fully 
			// handled in the action, including outputstream
			// manipulation
			if (!GPortalExecutionContext.isCustomContent()) {
				logger.info("visiting the view name is : " + template);
//				ServletContext servletContext = context;
				String themeName = template.substring(0, template.indexOf("/"));
				template = template.substring(template.indexOf("/")+1,template.length());
				HandlebarsRender webRender = new HandlebarsRender(new ServletContextTemplateLoader(context),themeName);
				webRender.render(template, req, res, request.getParameterNames());
			}
		}
		
	}

	/**
	 * 使用freemarker模板
	 * @author wilson
	 * @param out
	 * @param request
	 * @param response
	 * @param encoding
	 * @param context
	 * @param moduleClass
	 * @return
	 * @throws Exception
	 */
	private Writer processCommand(Writer out, RequestContext request, ResponseContext response, String encoding,
			SimpleHash context, String moduleClass) throws Exception {
		// Here we go, baby
		Command c = this.retrieveCommand(moduleClass);
		Template template = c.process(request, response, context);

		if (GPortalExecutionContext.getRedirectTo() == null) {
			String contentType = GPortalExecutionContext.getContentType();

			if (contentType == null) {
				contentType = "text/html; charset=" + encoding;
			}

			response.setContentType(contentType);

			// Binary content are expected to be fully 
			// handled in the action, including outputstream
			// manipulation
			if (!GPortalExecutionContext.isCustomContent()) {
				OutputStreamWriter os = new OutputStreamWriter(response.getOutputStream(), encoding);
				out = new BufferedWriter(os);
				template.process(GPortalExecutionContext.getTemplateContext(), out);
				out.flush();
			}
		}

		return out;
	}

	private void checkDatabaseStatus() {
		if (!isDatabaseUp) {
			synchronized (this) {
				if (!isDatabaseUp) {
					isDatabaseUp = PortalStartup.startDatabase();
				}
			}
		}
	}

	private void handleFinally(Writer out, GPortalContext portalContext, ResponseContext response) throws IOException {
		try {
			if (out != null) {
				out.close();
			}
		} catch (Exception e) {
			// catch close error 
		}

		String redirectTo = GPortalExecutionContext.getRedirectTo();
		GPortalExecutionContext.finish();

		if (redirectTo != null) {
			if (portalContext != null && portalContext.isEncodingDisabled()) {
				response.sendRedirect(redirectTo);
			} else {
				response.sendRedirect(response.encodeRedirectURL(redirectTo));
			}
		}
	}

	private void handleException(Writer out, ResponseContext response, String encoding, Exception e,
			RequestContext request) throws IOException {
		GPortalExecutionContext.enableRollback();

		if (e.toString().indexOf("ClientAbortException") == -1) {
			response.setContentType("text/html; charset=" + encoding);
			if (out != null) {
				new ExceptionWriter().handleExceptionData(e, out, request);
			} else {
				new ExceptionWriter().handleExceptionData(e, new BufferedWriter(new OutputStreamWriter(response
						.getOutputStream())), request);
			}
		}
	}

//	private boolean shouldBan(String ip) {
//		Banlist b = new Banlist();
//
//		b.setUserId(MemberSessionFacade.getMemberSession().getMemberId().intValue());
//		b.setIp(ip);
//
//		return BanlistRepository.shouldBan(b);
//	}

	private Command retrieveCommand(String moduleClass) throws Exception {
		return (Command) Class.forName(moduleClass).newInstance();
	}

	/** 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		super.destroy();
		System.out.println("Destroying CMWEBGAME...");

		try {
			DBConnection.getImplementation().realReleaseAllConnections();
			DBConnection dbConnection2 = DBConnection.getImplementation2();  
			if(dbConnection2!=null){
				dbConnection2.realReleaseAllConnections();
			}
			ConfigLoader.stopCacheEngine();
		} catch (Exception e) {
		}
	}
}

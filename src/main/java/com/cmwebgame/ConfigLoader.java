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
 * Created on 02/11/2004 12:45:37
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;

import com.cmwebgame.cache.CacheEngine;
import com.cmwebgame.cache.Cacheable;
import com.cmwebgame.cache.CacheableTwoLevel;
import com.cmwebgame.exceptions.CacheEngineStartupException;
import com.cmwebgame.exceptions.PortalException;
import com.cmwebgame.handle.DataDriverHandle;
import com.cmwebgame.handle.GlobalHandle;
import com.cmwebgame.quartz.SummaryScheduler;
import com.cmwebgame.sso.LoginAuthenticator;
import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.ImplGlobals;
import com.cmwebgame.util.preferences.SystemGlobals;

/**
 * General utilities methods for loading configurations for CMWEBGAME.
 * 
 * @author Rafael Steil
 * @version $Id: ConfigLoader.java,v 1.30 2007/07/27 15:42:56 rafaelsteil Exp $
 */
public class ConfigLoader 
{
	private static final Logger logger = Logger.getLogger(ConfigLoader.class);
	private static CacheEngine cache;
	private static CacheEngine cache2;
	
	/**
	 * Start ( or restart ) <code>SystemGlobals</code>.
	 * This method loads all configuration keys set at
	 * <i>SystemGlobals.properties</i>, <i>&lt;user.name&gt;.properties</i>
	 * and database specific stuff.
	 * 
	 * @param appPath The application root's directory
	 */
	public static void startSystemglobals(String appPath)
	{
		SystemGlobals.initGlobals(appPath, appPath + "/WEB-INF/config/SystemGlobals.properties");
		SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_CONFIG));
		if(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_CONFIG2) != null){//加载mysql2配置文件
			SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_CONFIG2));
		}
		
		
		if (new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG)).exists()) {
			SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
		}
		if (new File(SystemGlobals.getValue("impl.config")).exists()) {
			SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue("impl.config"));
		}
		if (new File(SystemGlobals.getValue(ConfigKeys.SMS_FILTER_KEY_WORDS_CONFIG)).exists()) {
			SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.SMS_FILTER_KEY_WORDS_CONFIG));
		}
	}
	
	/**
	 * Loads module mappings for the system.
	 * 
	 * @param baseConfigDir The directory where the file <i>modulesMapping.properties</i> is.
	 * @return The <code>java.util.Properties</code> instance, with the loaded modules 
	 */
	public static Properties loadModulesMapping(String baseConfigDir)
	{
		FileInputStream fis = null;
		
		try {
			Properties modulesMapping = new Properties();
			fis = new FileInputStream(baseConfigDir + "/modulesMapping.properties");
			modulesMapping.load(fis);

			return modulesMapping;
		}
		catch (IOException e) {
			throw new PortalException( e);
		}
		finally {
			if (fis != null) {
				try { fis.close(); } catch (Exception e) {}
			}
		}
    }
	
	public static void createLoginAuthenticator()
	{
		/*wilson 2014-8-11
		String className = SystemGlobals.getValue(ConfigKeys.LOGIN_AUTHENTICATOR);

		try {
			LoginAuthenticator loginAuthenticator = (LoginAuthenticator) Class.forName(className).newInstance();
			SystemGlobals.setObjectValue(ConfigKeys.LOGIN_AUTHENTICATOR_INSTANCE, loginAuthenticator);
		}
		catch (Exception e) {
			throw new PortalException("Error while trying to create a login.authenticator instance ("
				+ className + "): " + e, e);
		}*/
		Object object = ImplGlobals.getHandle("LoginAuthenticator");
		if(object != null){
			LoginAuthenticator loginAuthenticator = (LoginAuthenticator)object;
			SystemGlobals.setObjectValue(ConfigKeys.LOGIN_AUTHENTICATOR_INSTANCE, loginAuthenticator);
		}else{
			//throw new PortalException("Error while trying to create a login.authenticator instance ('LoginAuthenticator'): class not found!");
			System.out.println("Error while trying to create a login.authenticator instance ('LoginAuthenticator'): class not found! so the LoginAuthenticator can not init!");
		}
	}
	
	/**
	 * Load url patterns.
	 * The method tries to load url patterns from <i>WEB-INF/config/urlPattern.properties</i>
	 */
	public static void loadUrlPatterns()  
	{
		FileInputStream fis = null;
		
		try {
			Properties p = new Properties();
			fis = new FileInputStream(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/urlPattern.properties");
			p.load(fis);

			for (Iterator iter = p.entrySet().iterator(); iter.hasNext(); ) {
				Map.Entry entry = (Map.Entry) iter.next();
				UrlPatternCollection.addPattern((String)entry.getKey(), (String)entry.getValue());
			}
		}
		catch (IOException e) {
			throw new PortalException(e);
		}
		finally {
			if (fis != null) {
				try { fis.close(); } catch (Exception e) {}
			}
		}
    }
	
	/**
	 * Listen for changes in common configuration files.
	 * The watched files are: <i>generic_queries.sql</i>, 
	 * <i>&lt;database_name&gt;.sql</i>, <i>SystemGlobals.properties</i>
	 * and <i>&lt;user.name&gt;.properties</i>
	 * 增加properties文件的change事件监听
	 */
	public static void listenForChanges()
	{
		int fileChangesDelay = SystemGlobals.getIntValue(ConfigKeys.FILECHANGES_DELAY);
		
		if (fileChangesDelay > 0) {
			/*不再进行监听2011-12-26
			// Queries
			FileMonitor.getInstance().addFileChangeListener(new QueriesFileListener(),
				SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_GENERIC), fileChangesDelay);

			FileMonitor.getInstance().addFileChangeListener(new QueriesFileListener(),
				SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER), fileChangesDelay);

			// System Properties
			
			FileMonitor.getInstance().addFileChangeListener(new SystemGlobalsListener(),
				SystemGlobals.getValue(ConfigKeys.DEFAULT_CONFIG), fileChangesDelay);
				
			FileMonitor.getInstance().addFileChangeListener(new ImplGlobalsListener(),
					SystemGlobals.getValue("impl.config"), fileChangesDelay);
				
				*/

			ConfigLoader.listenInstallationConfig();
        }
	}
	
	
	public static void listenInstallationConfig()
	{
		int fileChangesDelay = SystemGlobals.getIntValue(ConfigKeys.FILECHANGES_DELAY);
		
		if (fileChangesDelay > 0) {
			if (new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG)).exists()) {
				/*不再进行监听2011-12-26
				FileMonitor.getInstance().addFileChangeListener(new SystemGlobalsListener(),
						SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG), fileChangesDelay);
						*/
			}
		}
	}
	
	public static void loadDaoImplementation()
	{
		// Start the dao.driver implementation
		Object object = ImplGlobals.getHandle("DataDriverHandleImpl");
		if(object != null){
			DataDriverHandle dataDriverHandle = (DataDriverHandle)object;
			dataDriverHandle.init();
		}else{
			//throw new PortalException("the DataDriverHandleImpl class is not found!");
			System.out.println("Warnning : the DataDriverHandleImpl class is not found! so the database's access can not init!");
		}
    }
	
	public static void startCacheEngine()
	{
		try {
			String cacheImplementation = SystemGlobals.getValue(ConfigKeys.CACHE_IMPLEMENTATION);
			String cacheImplementation2 = SystemGlobals.getValue(ConfigKeys.CACHE_IMPLEMENTATION2);
			logger.info("Using cache engine: " + cacheImplementation);
			cache = (CacheEngine)Class.forName(cacheImplementation).newInstance();
			cache.init();
			//如果二級緩存存在，則加載
			if(cacheImplementation2 != null && !cacheImplementation2.equals("")){
				logger.info("Using cache2 engine: " + cacheImplementation2);
				cache2 = (CacheEngine)Class.forName(cacheImplementation2).newInstance();
				cache2.init();
			}
			
			String s = SystemGlobals.getValue(ConfigKeys.CACHEABLE_OBJECTS);
			if (s == null || s.trim().equals("")) {
				logger.warn("Cannot find Cacheable objects to associate the cache engine instance.");
				return;
			}
			
			String[] cacheableObjects = s.split(",");
			for (int i = 0; i < cacheableObjects.length; i++) {
				logger.info("Creating an instance of " + cacheableObjects[i]);
				Object o = Class.forName(cacheableObjects[i].trim()).newInstance();
				
				if (o instanceof Cacheable) {
					((Cacheable)o).setCacheEngine(cache);
				}else if(o instanceof CacheableTwoLevel){
					((CacheableTwoLevel)o).setCacheEngine(cache);
					((CacheableTwoLevel)o).setLevelTwoCacheEngine(cache2);
				}else {
					logger.error(cacheableObjects[i] + " is not an instance of com.cmwebgame.cache.Cacheable");
				}
			}
		}
		catch (Exception e) {
			throw new CacheEngineStartupException("Error while starting the cache engine", e);
		}
	}
	
	public static void stopCacheEngine()
	{
		if (cache != null) {
			cache.stop();
		}
		if(cache2 != null){
			cache2.stop();
		}
	}
	
	public static void startSearchIndexer()
	{
//		SearchFacade.init();
	}
	
	public static void startGroupBuyCheckService(){
		Object globalHandleObject = ImplGlobals.getHandle("GlobalHandleImpl");
		GlobalHandle  globalHandle = null;
		if(globalHandleObject != null){//
			globalHandle = (GlobalHandle)globalHandleObject ;
			if (globalHandle != null) {
				globalHandle.runService();
			}
		}
	}

	/**
	 * Init a Job who will send e-mails to the all users with a summary of posts...
	 * @throws SchedulerException
	 * @throws IOException
	 */
	public static void startSummaryJob() throws SchedulerException {
		String quartzClass = SystemGlobals.getValue("com.cmwebgame.quartz.class");
		if(quartzClass != null && !quartzClass.trim().equals("")){
			SummaryScheduler.startJob();
		}
	}
	
	public static void startPop3Integration() throws SchedulerException
	{
//		POPJobStarter.startJob();
	}
}

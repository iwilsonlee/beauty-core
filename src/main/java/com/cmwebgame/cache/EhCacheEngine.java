/******************************************************************************
 * Sony Online Entertainment
 * Application Engineering
 *
 * Unpublished work Copyright 2005 Sony Online Entertainment Inc.
 * All rights reserved.
 * Created on Oct 11, 2005
 ******************************************************************************/
package com.cmwebgame.cache;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;

import org.apache.log4j.Logger;

import com.cmwebgame.GPortalExecutionContext;
import com.cmwebgame.context.RequestContext;
import com.cmwebgame.util.preferences.SystemGlobals;

/**
 * The rest of the application seems to make some invalid assumptions about how
 * things are cached.  Those assumptions might be benign, but it is hard to tell
 * without deep testing.  Until this is finishe the JBossCacheEngine should be 
 * configured in a local mode.
 *
 * Created on Oct 11, 2005 
 *
 * @author Jake Fear
 * @version $Id: EhCacheEngine.java,v 1.1 2005/10/14 00:15:54 rafaelsteil Exp $
 */
public class EhCacheEngine implements CacheEngine {

	private static final Logger log = Logger.getLogger(EhCacheEngine.class);

	private CacheManager manager;

//	protected RequestContext request;

//	protected ServletContext servletContext;

	public void init() {
		try {
			if (System.getSecurityManager() == null) {
	            System.setSecurityManager ( new RMISecurityManager() );
	        }
			/*
			String rootPath = SystemGlobals.getApplicationPath();
			rootPath = rootPath.substring(0, rootPath.lastIndexOf(File.separator));
			rootPath = rootPath.substring(0, rootPath.lastIndexOf(File.separator));
			String filePath = rootPath + File.separator + "conf" + File.separator + "ehcache.xml";
			*/
			String homePath = System.getProperty("catalina.home");
			String filePath = homePath + File.separator + "conf" + File.separator + "ehcache.xml";
//			URL url = getClass().getResource("/ehcache.xml");
			log.warn("EhCache's filePath is : " + filePath);
			manager = CacheManager.create(filePath);
			//			manager = CacheManager.create();
			//			manager = CacheManager.create(SystemGlobals.getValue("ehcache.cache.properties"));
		} catch (CacheException ce) {
			log.error("EhCache could not be initialized", ce);
			ce.printStackTrace();
			throw new RuntimeException(ce);
		}
	}

	public void stop() {
		if(manager != null)
		    manager.shutdown();
		manager = null;
	}

	public void add(String key, Object value) {
		if (log.isDebugEnabled()) {
			log.debug("Caching " + value + " with key " + key);
		}
		add(DUMMY_FQN, key, value);
	}

	public void add(String fullyQualifiedName, String key, Object value) {
		//ehcache不保存session，如果不是session对象则进行保存
		if(fullyQualifiedName.indexOf("sessions") == -1){
			if (manager.getStatus().equals(Status.STATUS_ALIVE)) {
				if (!manager.cacheExists(fullyQualifiedName)) {
					try {
						manager.addCache(fullyQualifiedName);
					} catch (CacheException ce) {
						log.error(ce, ce);
						throw new RuntimeException(ce);
					}
				}
			} else {
				this.init();
			}
			Cache cache = manager.getCache(fullyQualifiedName);

			Element element = new Element(key, (Serializable) value);
			cache.put(element);
		}
		
	}

	public Object get(String fullyQualifiedName, String key) {
		try {
			if (manager.getStatus().equals(Status.STATUS_ALIVE)) {
				if (!manager.cacheExists(fullyQualifiedName)) {
					manager.addCache(fullyQualifiedName);
					return null;
				}
				Cache cache = manager.getCache(fullyQualifiedName);
				Element element = cache.get(key);
				if (element != null) {
					return element.getValue();
				}
			} else {
				//如果ehcahce未激活，則重新初始化
				this.init();
			}
			return null;

		} catch (CacheException ce) {
			log.error("EhCache could not be shutdown", ce);
			throw new RuntimeException(ce);
		}
	}

	public Object get(String fullyQualifiedName) {
		if (manager.getStatus().equals(Status.STATUS_ALIVE)) {
			if (!manager.cacheExists(fullyQualifiedName)) {
				try {
					manager.addCache(fullyQualifiedName);
				} catch (CacheException ce) {
					log.error("EhCache could not be shutdown", ce);
					throw new RuntimeException(ce);
				}
			}
			Cache cache = manager.getCache(fullyQualifiedName);
			return cache;
		} else {
			//如果ehcahce未激活，則重新初始化
			this.init();
			return null;
		}
	}

	public Collection getValues(String fullyQualifiedName) {
		try {
			if (manager.getStatus().equals(Status.STATUS_ALIVE)) {
				if (!manager.cacheExists(fullyQualifiedName)) {
					manager.addCache(fullyQualifiedName);
					return new ArrayList();
				}
				Cache cache = manager.getCache(fullyQualifiedName);
				List values = new ArrayList(cache.getSize());
				List keys = cache.getKeys();

				for (Iterator iter = keys.iterator(); iter.hasNext();) {
					Element e = cache.get((Serializable) iter.next());
					Object object = e.getValue();
					values.add(object);
					//				values.add(cache.get((Serializable)iter.next());
				}

				return values;
			} else {
				//如果ehcahce未激活，則重新初始化
				this.init();
				return new ArrayList();
			}
		} catch (CacheException ce) {
			log.error("EhCache could not be shutdown", ce);
			throw new RuntimeException(ce);
		}
	}

	public void remove(String fullyQualifiedName, String key) {
		Cache cache = manager.getCache(fullyQualifiedName);

		if (cache != null) {
			cache.remove(key);
		}
	}

	public void remove(String fullyQualifiedName) {
		if (manager.cacheExists(fullyQualifiedName)) {
			manager.removeCache(fullyQualifiedName);
		}
	}

}

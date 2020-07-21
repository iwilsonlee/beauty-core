/*
 * Copyright (c)Rafael Steil
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
 * Created on Feb 1, 2005 7:30:35 PM
 * The CMWEBGAME Project
 * http://www.cmwebgame.com
 */
package com.cmwebgame.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * 基于Google Guava的cache类库封装的本地cacheEngine
 * @author Wilson
 * @version $Id: GuavaCacheEngine.java,v 1.9 2014/08/20 14:40:28 rafaelsteil Exp $
 */
public class GuavaCacheEngine implements CacheEngine
{
	private Logger logger = Logger.getLogger(GuavaCacheEngine.class);
	
	private Cache<String, Object> cache;
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#add(java.lang.String, java.lang.Object)
	 */
	public void add(String key, Object value)
	{
		this.cache.put(key, value);
//		logger.info("executing add(String key, Object value), the key="+key + "value="+value.toString());
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#add(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void add(String fqn, String key, Object value)
	{
		@SuppressWarnings("unchecked")
		Map<String, Object> m = (Map<String, Object>)this.get(fqn);
		if(Objects.equal(m, null)){
			m = Maps.newHashMap();
		}
		m.put(key, value);
		this.cache.put(fqn, m);

//		logger.info("executing add(String fqn, String key, Object value), the fqn="+fqn + " | key="+key + "value="+value.toString());
		
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#get(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Object get(String fqn, String key)
	{
//		logger.info("executing get(String fqn, String key), the fqn="+fqn + " | key="+key);
		Map<String, Object> m = Maps.newHashMap();
		try {//可以使用cache.getIfPresent(key)获取value，但是getIfPresent获取的value如果是null的话，guava cache会视为缓存丢失(即没能命中缓存，miss了)
			m = (Map<String, Object>)this.cache.get(fqn, new Callable<Object>() {
				public Object call() {
					return Maps.newHashMap();
				}
			});
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return (m!=null&&m.get(key)!=null)?m.get(key):null;
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#get(java.lang.String)
	 */
	public Object get(String fqn)
	{
//		logger.info("executing get(String fqn), the fqn="+fqn);
		return this.cache.getIfPresent(fqn);
		
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#getValues(java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	public Collection getValues(String fqn)
	{
		Map m = Maps.newHashMap();
		m = (Map)this.get(fqn);
		return (m==null)?Lists.newArrayList():m.values();
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#init()
	 */
	public void init()
	{
		this.cache = CacheBuilder.newBuilder().maximumSize(30000).expireAfterWrite(30, TimeUnit.MINUTES).build();  
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#stop()
	 */
	public void stop() {
		this.cache.invalidateAll();
		this.cache = null;
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#remove(java.lang.String, java.lang.String)
	 */
	public void remove(String fqn, String key)
	{
		@SuppressWarnings("rawtypes")
		Map m = (Map)this.get(fqn);
		if (m != null)
			m.remove(key);
		
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#remove(java.lang.String)
	 */
	public void remove(String fqn)
	{
		this.cache.invalidate(fqn);
	}
}

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

/**
 * @author Rafael Steil
 * @version $Id: DefaultCacheEngine.java,v 1.9 2005/09/25 02:40:28 rafaelsteil Exp $
 */
public class DefaultCacheEngine implements CacheEngine
{
	private Map cache = new HashMap();
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#add(java.lang.String, java.lang.Object)
	 */
	public void add(String key, Object value)
	{
		this.cache.put(key, value);
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#add(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void add(String fqn, String key, Object value)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			m = new HashMap();
		}

		m.put(key, value);
		this.cache.put(fqn, m);
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#get(java.lang.String, java.lang.String)
	 */
	public Object get(String fqn, String key)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			return null;
		}
		
		return m.get(key);
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#get(java.lang.String)
	 */
	public Object get(String fqn)
	{
		return this.cache.get(fqn);
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#getValues(java.lang.String)
	 */
	public Collection getValues(String fqn)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			return new ArrayList();
		}
		
		return m.values();
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#init()
	 */
	public void init()
	{
		this.cache = new HashMap();
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#stop()
	 */
	public void stop() {}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#remove(java.lang.String, java.lang.String)
	 */
	public void remove(String fqn, String key)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m != null) {
			m.remove(key);
		}
	}
	
	/**
	 * @see com.cmwebgame.cache.CacheEngine#remove(java.lang.String)
	 */
	public void remove(String fqn)
	{
		this.cache.remove(fqn);
	}
}

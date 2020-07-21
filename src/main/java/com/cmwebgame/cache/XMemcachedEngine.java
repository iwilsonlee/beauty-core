
package com.cmwebgame.cache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.CASOperation;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.buffer.CachedBufferAllocator;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.exception.MemcachedException;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.log4j.Logger;
import org.apache.util.Base64;

import com.cmwebgame.util.preferences.ConfigKeys;
import com.cmwebgame.util.preferences.SystemGlobals; //import com.danga.memcached.Cache;

//import com.danga.memcached.CacheManager;
//import com.danga.memcached.MemCachedClient;
//import com.danga.memcached.SockIOPool;
//import com.danga.MemCached.MemCachedClient;
//import com.danga.MemCached.SockIOPool;

/**
 * The rest of the application seems to make some invalid assumptions about how
 * things are cached.  Those assumptions might be benign, but it is hard to tell
 * without deep testing.  Until this is finishe the JBossCacheEngine should be 
 * configured in a local mode.
 *
 *
 * @author Wilson
 */
public class XMemcachedEngine implements CacheEngine {

	private static final Logger log = Logger.getLogger(XMemcachedEngine.class);
	private MemcachedClient client;
	private int expiryTime = 30;//minutes
	private final long OP_TIME = 5000L;
	

	private Date getExpiryDate(int expiryTime) {
		Calendar calendar = Calendar.getInstance();

		long time = calendar.getTimeInMillis();
		time += expiryTime * 60 * 1000;
		calendar.setTimeInMillis(time);

		return calendar.getTime();
	}

	// 指定memcached服务地址
	final String servers = SystemGlobals.getValue(ConfigKeys.XMEMCACHE_SERVERS);
	final String[] weightString = SystemGlobals.getValue(ConfigKeys.XMEMCACHE_WEIGHTS).split(",");
	// 指定memcached服务器负载量
	final int[] weights = parseToIntegers(weightString);
	final int poolsize = SystemGlobals.getIntValue(ConfigKeys.XMEMCACHE_CONNECTIONPOOLSIZE);
	final int dispatchMessageThreadCount = SystemGlobals.getIntValue(ConfigKeys.XMEMCACHE_DISPATCHMESSAGETHREADCOUNT);
	final int writeThreadCount = SystemGlobals.getIntValue(ConfigKeys.XMEMCACHE_WRITETHREADCOUNT);
	final int readThreadCount = SystemGlobals.getIntValue(ConfigKeys.XMEMCACHE_READTHREADCOUNT);
	final int compressionThreshold = SystemGlobals.getIntValue(ConfigKeys.XMEMCACHE_COMPRESSIONTHRESHOLD);
	final boolean useBinary = SystemGlobals.getBoolValue(ConfigKeys.XMEMCACHE_USEBINARY); 
	
	private int[] parseToIntegers(String[] arg) {
		int[] integers = new int[arg.length];
		for (int i = 0; i < arg.length; i++) {
			integers[i] = Integer.parseInt(arg[i]);
		}
		return integers;
	}

	public MemcachedClient getMemcachedClient() {
		try {
			MemcachedClientBuilder builder = new XMemcachedClientBuilder(
					AddrUtil.getAddresses(servers),weights);
			if (useBinary) {
				builder.setCommandFactory(new BinaryCommandFactory());
			}
			builder.setBufferAllocator(new CachedBufferAllocator());
//			builder.setSocketOption(StandardSocketOption.SO_RCVBUF, 32 * 1024); // set receive buffer as 32K, default is 16K                         
//			builder.setSocketOption(StandardSocketOption.SO_SNDBUF, 16 * 1024); // set send buffer as 16K, default is 8K
//			builder.getConfiguration().setStatisticsServer(false); // disable connection statistics

			builder.setConnectionPoolSize(poolsize);//Nio连接池大小
			return builder.build();
		} catch (IOException e) {
			System.err.println("Create MemcachedClient fail");
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void init() {
		// TODO Auto-generated method stub
		
//		try {
		client = this.getMemcachedClient();
		if(client != null){
			
			//數據壓縮閥值
			client.getTranscoder().setCompressionThreshold(compressionThreshold);
//			((SerializingTranscoder)client.getTranscoder()).setPackZeros(false);
			//			client.setPrimitiveAsString(true);
			client.setOptimizeMergeBuffer(false); // disable the buffer merge
			client.setSanitizeKeys(false);
			client.setOpTimeout(1000*60);
			client.setMergeFactor(50);   //默认是150，缩小到50
//			client.flushAll();
		}else{
			log.warn("memcache init failed : because memcache client is null");
		}
			
//		} 
//		catch (TimeoutException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (MemcachedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		log.info("The Webapp Server is stoped now, but the memcached don't excute stop action, and it will continue to running!");
		/*
		try {
			if(client != null){
			   client.shutdown();
			}else{
				log.warn("memcache stop failed : because memcache client is null");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

	@Override
	public void add(String key, Object value) {
		// TODO Auto-generated method stub
		if (log.isDebugEnabled()) {
			log.debug("Caching " + value + " with key " + key);
		}
		add(DUMMY_FQN, key, value);
	}

	@Override
	public void add(String fullyQualifiedName, String key, Object value) {
		// TODO Auto-generated method stub
		//如果是操作session內容，則每次操作都更新過期時間30分钟，否则为10分钟
		try {
			String finalKey = fullyQualifiedName+key;
			if(finalKey != null){
				finalKey = new String(Base64.encode(finalKey.getBytes()));
				if(client != null){
					if(fullyQualifiedName.indexOf("sessions") != -1){
						client.set(finalKey, 60 * 30, value);
					}else{
						client.set(finalKey, 60 * 10, value);
					}
				}
			}
			
			
			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public boolean replace(String key, Object value) {
		boolean result = false;

		try {
			if(client != null){
				if (key.indexOf("sessions") != -1) {
					result = client.replace(key, 60 * 30, value);
				} else {
					result = client.replace(key, 60 * 10, value);
				}
			}else{
				log.warn("memcache replace failed : because memcache client is null");
			}
			
			return result;
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}

		

	}

	public boolean replace(String key, Object value, int expiry) {
		boolean result = false;

		try {
			if(client != null){
				if (key.indexOf("sessions") != -1) {
					result = client.replace(key, expiry, value);
				} else {
					result = client.replace(key, expiry, value);
				}
			}else{
				log.warn("memcache replace failed : because memcache client is null");
			}
			
			return result;
		} catch (TimeoutException e) {
			//超時
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		} catch (InterruptedException e) {
			//鏈接中斷
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		} catch (MemcachedException e) {
			//內如異常
			// TODO Auto-generated catch block
			e.printStackTrace();
			return result;
		}

		
		
	}

	@Override
	public Object get(String fqn, String key) {
		// TODO Auto-generated method stub
		try {
			String finalKey = fqn+key;
			if(finalKey != null){
				finalKey = new String(Base64.encode(finalKey.getBytes()));
				Object object = client.get(finalKey, OP_TIME);
				return object;
			}else{
				return null;
			}
			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public Object get(String fqn) {
		// TODO Auto-generated method stub
		try {
			Object object = null;
			if(client != null){
				object = client.get(fqn, OP_TIME);
			}else{
				log.warn("memcache get(String fqn) failed : because memcache client is null");
			}
			
			return object;
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection getValues(String fqn) {
		// TODO Auto-generated method stub
		
		try {
			Map map = null;
			if(client != null){
				map = (Map)client.get(fqn,OP_TIME);
			}else{
				log.warn("memcache getValues(String fqn) failed : because memcache client is null");
			}
			
			if (map == null) {
				return new ArrayList();
			}
			return map.values();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList();
		}
		
	}

	@Override
	public void remove(String fqn, String key) {
		// TODO Auto-generated method stub
		//		log.error("key:===" + key);
		try {
			String finalKey = fqn+key;
			if(finalKey != null){
				finalKey = new String(Base64.encode(finalKey.getBytes()));
				if(client != null){
					client.delete(finalKey);
				}
			}
			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void remove(String fqn) {
		// TODO Auto-generated method stub

		try {
			if(client != null){
				client.delete(fqn);
			}else{
				log.warn("memcache remove(String fqn) failed : because memcache client is null");
			}
			
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MemcachedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}

class CASThread extends Thread {
	/**
	 * Increase Operation
	 * 
	 * @author dennis
	 * 
	 */
	static final class IncrmentOperation implements CASOperation<Integer> {

		public int getMaxTries() {
			return Integer.MAX_VALUE; // Max repeat times
		}

		public Integer getNewValue(long currentCAS, Integer currentValue) {
			return currentValue + 1;
		}
	}

	private MemcachedClient mc;
	private CountDownLatch cd;
	private String key;

	public CASThread(MemcachedClient mc, CountDownLatch cdl, String key) {
		super();
		this.mc = mc;
		this.cd = cdl;
		this.key = key;

	}

	@Override
	public void run() {
		try {
			if (this.mc.cas(key, 0, new IncrmentOperation())) {
				this.cd.countDown();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

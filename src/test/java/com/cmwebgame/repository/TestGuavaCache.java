package com.cmwebgame.repository;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.cmwebgame.cache.GuavaCacheEngine;

public class TestGuavaCache {

	public static void main(String[] args) {
		GuavaCacheEngine ge = new GuavaCacheEngine();
		ge.init();
		
		System.out.println(ge.get("wilson"));
//		
//		for (int i = 0; i < 10; i++) {
//			ge.add("wilson" + i, "session" + i);
//		}
//		for (int i = 0; i < 10; i++) {
//			ge.add("Sessions", "wilson" + i, "session" + i);
//		}
//		for (int i = 0; i < 10; i++) {
//			ge.add("article", "title" + i, "content" + i);
//
//		}
//		System.out.println("the cache name like wilson, its value is :");
//		for (int i = 0; i < 10; i++) {
//			System.out.println(ge.get("wilson" + i));
//		}
//		
//		Map m = (Map) ge.get("Sessions");
//		System.out.println("the cache name like sessions, size is "+m.size()+", its value is :");
//		System.out.println(ge.get("Sessions"));
//		
//		m = (Map) ge.get("article");
//		System.out.println("the cache name like article, size is "+m.size()+", its value is :");
//		System.out.println(ge.get("article"));
//		
//		System.out.println("the Session wilson6 is "+ge.get("Sessions","wilson6"));
//		System.out.println("the article title8 is "+ge.get("article","title8"));
//		
//		System.out.println("the Sessions key its values are :" + ge.getValues("Sessions"));
//		
//		ge.remove("Sessions", "wilson4");
//		
//		m = (Map) ge.get("Sessions");
//		System.out.println("the cache name like sessions, size is "+m.size()+", its value is :");
//		System.out.println(ge.get("Sessions"));
//		
//		ge.remove("article");
//		m = (Map) ge.get("article");
//		System.out.println("the cache name like article, size is "+m.size()+", its value is :");
//		System.out.println(ge.get("article"));
//		
//		//一下测试cache 容量回收机制，【当缓存数目接近容量值时，缓存将尝试回收最近没有使用或总体上很少使用的缓存项。
//		//——警告：在缓存项的数目达到限定值之前，缓存就可能进行回收操作——通常来说，这种情况发生在缓存项的数目逼近限定值时。】
//		for (int i = 10; i < 40; i++) {
//			ge.add("wilson" + i, "session" + i);
//		}
//		System.out.println("test max count -- the cache name like wilson, its value is :");
//		for (int i = 0; i < 10; i++) {
//			System.out.println(ge.get("wilson" + i));
//		}
//		
//		//以下测试cache timeout设置
//		try {  
//	        Thread.currentThread();  
//	        Thread.sleep(TimeUnit.SECONDS.toMillis(10));  
//	    } catch (InterruptedException e) {  
//	        // TODO Auto-generated catch block  
//	        e.printStackTrace();  
//	    }  
//
//		System.out.println("final - the cache name like wilson, its value is :");
//		for (int i = 0; i < 10; i++) {
//			System.out.println(ge.get("wilson" + i));
//		}
//		System.out.println("final - the cache name like sessions, size is "+m.size()+", its value is :");
//		System.out.println(ge.get("Sessions"));
//		
//		
		
	}

}

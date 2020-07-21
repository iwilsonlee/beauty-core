package com.cmwebgame.entities;

public class CacheMotion {

	private String cacheName = null;
	private String motionName = null;
	private String[] params = {};

	public CacheMotion(String cacheName, String motionName, String[] params) {
		this.cacheName = cacheName;
		this.motionName = motionName;
		this.params = params;
		System.out.println("event cacheName is "+cacheName+" | motionName is "+motionName);
	}

	public String getMessage() {
		return "event cacheName is "+cacheName+" | motionName is "+motionName;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String getMotionName() {
		return motionName;
	}

	public void setMotionName(String motionName) {
		this.motionName = motionName;
	}

	public String[] getParams() {
		return params;
	}

	public void setParams(String[] params) {
		this.params = params;
	}

	
	
}

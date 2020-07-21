package com.cmwebgame.entities;

import com.cmwebgame.cache.Cacheable;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.Subscribe;

public class EventListener {
	
	public String lastMessage = "";
	
	public Long age = null;

	boolean notDelivered = false;  
	   
    @Subscribe
    public void listen(CacheMotion event) {
        lastMessage = Preconditions.checkNotNull(event.getMessage());
        System.out.println("Message:"+lastMessage);
        
        try {
        	Cacheable cacheable = (Cacheable)Class.forName(event.getCacheName()).newInstance();
//        	cacheable.clearCache();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Subscribe
    public void listenAge(Long event){
    	age = Preconditions.checkNotNull(event);
        System.out.println("Message age :"+age);
    }
    
    /**
     * 如果无合适的监听类型，则执行此监听
     * @param event
     */
    @Subscribe  
    public void listenDeadEvent(DeadEvent event) {
        notDelivered = true;  
    }  
   
    public boolean isNotDelivered() {  
        return notDelivered;  
    }  

    public String getLastMessage() {      
        return lastMessage;
    }
}

package com.cmwebgame.entities;

import org.junit.Test;

import com.google.common.eventbus.EventBus;

/**
 * guava EvenBus 测试
 * @author wilson
 *
 */
public class TestEventBus {

	@Test
    public void testReceiveEvent() throws Exception {

        EventBus eventBus = new EventBus("test");
        EventListener listener = new EventListener();

        eventBus.register(listener);

        String[] param = {"wew","we"};
        eventBus.post(new CacheMotion("member", "add", param));
        eventBus.post(new CacheMotion("member", "remove", param));
        eventBus.post(new CacheMotion("member", "get", param));
        
        eventBus.post(new Long(1000));

        System.out.println("LastMessage:"+listener.getLastMessage());
        ;
    }
}

package com.Kz.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;

public class MyListener implements ApplicationListener {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //ApplicationEvent event就是对应的发布事件，
        //比如ApplicationReadyEvent,ApplicationFailedEvent分别代表应用初始化成功和失败事件
        if (event instanceof ApplicationEvent)
            System.out.println("success监听到事件：" + event);
        if (event instanceof ApplicationFailedEvent)
            System.out.println("failed监听到事件：" + event);

    }

}

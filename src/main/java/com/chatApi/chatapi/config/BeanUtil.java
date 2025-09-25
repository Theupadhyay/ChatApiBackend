package com.chatApi.chatapi.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Small utility to fetch beans from static context.
 * Add this to the context by placing it in a package scanned by Spring.
 */
@Component
public class BeanUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanUtil.ctx = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (ctx == null) throw new IllegalStateException("Spring context not initialized yet");
        return ctx.getBean(clazz);
    }
}

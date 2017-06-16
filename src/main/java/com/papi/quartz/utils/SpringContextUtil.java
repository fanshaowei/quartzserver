package com.papi.quartz.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component("springContextUtil")
public class SpringContextUtil implements ApplicationContextAware {  
    // Spring应用上下文环境  
    private static ApplicationContext applicationContext;  
    
    public SpringContextUtil(){}
    
    /** 
     * 实现ApplicationContextAware接口的回调方法，设置上下文环境   
     * @param applicationContext 
     */ 
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {  
        SpringContextUtil.applicationContext = applicationContext;  
    }  
    /** 
     * @return ApplicationContext 
     */ 
    public  ApplicationContext getApplicationContext() {  
        return applicationContext;  
    }  
    /** 
     * 获取对象 
     *  
     * @param name 
     * @return Object
     * @throws BeansException 
     */ 
    public Object getBean(String name) throws BeansException {  
        return applicationContext.getBean(name);  
    }
	
}

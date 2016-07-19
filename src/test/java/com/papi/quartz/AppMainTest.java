package com.papi.quartz;

import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppMainTest {
	@Test
    public void appMainTest(){
    	AbstractApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-service.xml");
    }
}

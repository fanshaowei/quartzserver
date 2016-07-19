package com.papi.quartz.listener;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class SchdulerLoaderListener implements ServletContextListener
{
	  static Logger logger = Logger.getLogger(SchdulerLoaderListener.class.getName());
	  static StdSchedulerFactory factory;
	  static Scheduler scheduler = null;

	  @Override
	  public void contextInitialized(ServletContextEvent servletContextEvent)
	  {
	    ServletContext servletContext = servletContextEvent.getServletContext();
	    
	    startSchedulerService(servletContext);
	  }

	  @Override
	  public void contextDestroyed(ServletContextEvent servletContextEvent)
	  {
	    try {
	      if (scheduler.isStarted())
	        scheduler.shutdown(true);
	    }
	    catch (SchedulerException e) {
	      logger.error("关闭任务调度失败", e);
	    }
	  }

	  public static void startSchedulerService(ServletContext servletContext)
	  { 
	    try  
	    {
	      factory = (StdSchedulerFactory)servletContext.getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
	      if (factory == null) {
	        factory = new StdSchedulerFactory();
	        factory.initialize(getProperties(servletContext));
	        servletContext.setAttribute("org.quartz.impl.StdSchedulerFactory.KEY", factory);
	      }
	      scheduler = factory.getScheduler();	 
	      
	      ApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
	      scheduler.getContext().put("applicationContextSchedulerContextKey", ac);
	      
	      scheduler.start();
	    } catch (SchedulerException e) {
	      e.printStackTrace();
	    }
	  }

	  private static Properties getProperties(ServletContext servletContext)
	  {
	    Properties properties = new Properties();
	    Properties config = null;
	    String fileName = servletContext.getInitParameter("config-file");//在web容器中配置加载
	    InputStream in = SchdulerLoaderListener.class.getClassLoader().getResourceAsStream(fileName);
	    config = new Properties();
	    try {
	      config.load(in);
	    } catch (IOException e) {
	      e.printStackTrace();
	    }

	    properties.setProperty("org.quartz.scheduler.instanceName", 
	      config.getProperty("org.quartz.scheduler.instanceName") == null ? "SchedulerJob" : config.getProperty("org.quartz.scheduler.instanceName"));
	    
	    properties.setProperty("org.quartz.scheduler.instanceId", 
	      config.getProperty("org.quartz.scheduler.instanceId") == null ? "AUTO" : config.getProperty("org.quartz.scheduler.instanceId"));
	    
	    properties.setProperty("org.quartz.threadPool.class", 
	      config.getProperty("org.quartz.threadPool.class") == null ? "org.quartz.simpl.SimpleThreadPool" : config.getProperty("org.quartz.threadPool.class"));

	    properties.setProperty("org.quartz.threadPool.threadCount", 
	      config.getProperty("org.quartz.threadPool.threadCount") == null ? "30" : config.getProperty("org.quartz.threadPool.threadCount"));

	    properties.setProperty("org.quartz.threadPool.threadPriority", 
	      config.getProperty("org.quartz.threadPool.threadPriority") == null ? "5" : config.getProperty("org.quartz.threadPool.threadPriority"));
	    
	    properties.setProperty("org.quartz.jobStore.misfireThreshold", 
	      config.getProperty("org.quartz.jobStore.misfireThreshold") == null ? "60000" : config.getProperty("org.quartz.jobStore.misfireThreshold"));
	    
	    properties.setProperty("org.quartz.jobStore.class", 
	      config.getProperty("org.quartz.jobStore.class") == null ? "org.quartz.impl.jdbcjobstore.JobStoreTX" : config.getProperty("org.quartz.jobStore.class"));
	    
	    properties.setProperty("org.quartz.jobStore.driverDelegateClass", 
	      config.getProperty("org.quartz.jobStore.driverDelegateClass") == null ? "org.quartz.impl.jdbcjobstore.StdJDBCDelegate" : config.getProperty("org.quartz.jobStore.driverDelegateClass"));
	    
	    properties.setProperty("org.quartz.jobStore.useProperties", 
	      config.getProperty("org.quartz.jobStore.useProperties") == null ? "true" : config.getProperty("org.quartz.jobStore.useProperties"));
	    
	    properties.setProperty("org.quartz.jobStore.dataSource", 
	      config.getProperty("org.quartz.jobStore.dataSource") == null ? "myDS" : config.getProperty("org.quartz.jobStore.dataSource"));
	    
	    properties.setProperty("org.quartz.jobStore.tablePrefix", 
	      config.getProperty("org.quartz.jobStore.tablePrefix") == null ? "QRTZ_" : config.getProperty("org.quartz.jobStore.tablePrefix"));
	    
	    properties.setProperty("org.quartz.jobStore.isClustered", 
	      config.getProperty("org.quartz.jobStore.isClustered") == null ? "false" : config.getProperty("org.quartz.jobStore.isClustered"));
	    
	    properties.setProperty("org.quartz.dataSource.myDS.driver", 
	      config.getProperty("org.quartz.dataSource.myDS.driver") == null ? config.getProperty("jdbc.driver") : config.getProperty("org.quartz.dataSource.myDS.driver"));
	    
	    properties.setProperty("org.quartz.dataSource.myDS.URL", 
	      config.getProperty("org.quartz.dataSource.myDS.URL") == null ? config.getProperty("jdbc.url") : config.getProperty("org.quartz.dataSource.myDS.URL"));
	    
	    properties.setProperty("org.quartz.dataSource.myDS.user", 
	      config.getProperty("org.quartz.dataSource.myDS.user") == null ? config.getProperty("jdbc.username") : config.getProperty("org.quartz.dataSource.myDS.user"));
	    
	    properties.setProperty("org.quartz.dataSource.myDS.password", 
	      config.getProperty("org.quartz.dataSource.myDS.password") == null ? config.getProperty("jdbc.password") : config.getProperty("org.quartz.dataSource.myDS.password"));
	    
	    properties.setProperty("org.quartz.dataSource.myDS.maxConnections", 
	      config.getProperty("org.quartz.dataSource.myDS.maxConnections") == null ? "30" : config.getProperty("org.quartz.dataSource.myDS.maxConnections"));
	 
	   /* properties.setProperty("org.quartz.jobListener.CustomJobListener.class", 
	      config.getProperty("org.quartz.jobListener.CustomJobListener.class") == null ? "com.papi.quartz.listener.CustomJobListener" : config.getProperty("com.papi.quartz.listener.CustomJobListener.class"));
	    
	    properties.setProperty("org.quartz.jobListener.CustomJobListener.name", 
	      config.getProperty("org.quartz.jobListener.CustomJobListener.name") == null ? "CustomJobListener" : config.getProperty("org.quartz.jobListener.CustomJobListener.name"));
*/
	    return properties;
	  }
}


package com.papi.quartz.listener;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.papi.quartz.bean.AppRequestJobInfo;
import com.papi.quartz.commons.config.QuartzFrameConfig;
import com.papi.quartz.service.AppJobService;
import com.papi.quartz.service.RedisUtilService;

/**
 * 
 * @author fanshaowei
 *
 * 定时任务监听器，在web容器启动时调用，启动quartz，并初始化定时任务
 */
public class SchdulerLoaderListener implements ServletContextListener
{
	private static Logger logger = Logger.getLogger(SchdulerLoaderListener.class.getName());
	private static StdSchedulerFactory factory;
	private static Scheduler scheduler = null;
	private static ApplicationContext ac = null;
	  
	private RedisUtilService redisUtilService;
	private AppJobService appJobService;
	private static QuartzFrameConfig quartzFrameConfig;
	
	//获取spring注入的bean
	private void getContextBean(ServletContext servletContext)
	{
		ac = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		this.redisUtilService = (RedisUtilService) ac.getBean("redisUtilService");
		this.appJobService = (AppJobService) ac.getBean("appJobService");
		quartzFrameConfig = (QuartzFrameConfig) ac.getBean("quartzFrameConfig");
	}
	
	//配置quartz
	private static Properties getQuartzConfig(){
		Properties properties = new Properties();
		
		properties.setProperty("org.quartz.scheduler.instanceName", quartzFrameConfig.getInstanceName());	    
	    properties.setProperty("org.quartz.scheduler.instanceId", quartzFrameConfig.getInstanceId());	    
	    properties.setProperty("org.quartz.threadPool.class", quartzFrameConfig.getThreadPoolClass());
	    properties.setProperty("org.quartz.threadPool.makeThreadsDaemons", quartzFrameConfig.getMakeThreadsDaemons());	  	    	    
	    properties.setProperty("org.quartz.threadPool.threadCount", quartzFrameConfig.getThreadCount());
	    properties.setProperty("org.quartz.threadPool.threadPriority", quartzFrameConfig.getThreadPriority());	    
	    properties.setProperty("org.quartz.jobStore.misfireThreshold", quartzFrameConfig.getMisfireThreshold());	    
	    properties.setProperty("org.quartz.jobStore.class", quartzFrameConfig.getJobStoreClass());
	    		
		return properties;
	}
	
	//启动quartz
    public static void startSchedulerService(ServletContext servletContext)
    { 
		try{
			factory = (StdSchedulerFactory)servletContext.getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
			
			if (factory == null) {
				factory = new StdSchedulerFactory();
				factory.initialize(getQuartzConfig());
				servletContext.setAttribute("org.quartz.impl.StdSchedulerFactory.KEY", factory);
			}
			
			scheduler = factory.getScheduler();	 
			//存放一个ServletContext在scheduler上下文容器中	      	     
			scheduler.getContext().put("applicationContextSchedulerContextKey", ac);
			//启动scheduler
			scheduler.start();  
			
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
    }
	  
    //初始化redis任务信息
    public void initRedisQuartzData(ServletContext servletContext)
    {
		Set<String> keySet = this.redisUtilService.getKeys("Quartz-*");
		
		if(!keySet.isEmpty()){
			Iterator<String> itor = keySet.iterator();
			String key;
			Set<Object> memberStr;
			String jobStr;
			AppRequestJobInfo appRequestJobInfo;
			String jobType;
			  
			while(itor.hasNext()){
				key = itor.next();
				memberStr = this.redisUtilService.sMembers(key);
				  
				for(Object obj:memberStr){	    		
					jobStr = obj.toString();
					appRequestJobInfo = (AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(jobStr), AppRequestJobInfo.class);
					jobType = appRequestJobInfo.getJobType();
					  
					if(jobType.equals("SceneRelated")){
						this.appJobService.addSceneRelateJob(servletContext, appRequestJobInfo);
					}else if(jobType.equals("DailySceneControl")){
					    this.appJobService.addDailySceneControlJob(servletContext, appRequestJobInfo);
				    }
			    }
		    }//end while
		}//end if
    }
    
	  @Override
	  public void contextInitialized(ServletContextEvent servletContextEvent)
	  {		  
		  ServletContext servletContext = servletContextEvent.getServletContext();
		  getContextBean(servletContext);
		  startSchedulerService(servletContext);
		  initRedisQuartzData(servletContext);	      
	  }

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		try {
			if (scheduler.isStarted())
				scheduler.shutdown(true);
			
			logger.info("--------------------关闭任务调试容器成功------------------");
		}
		catch (SchedulerException e) {
			logger.error("---------------------关闭任务调度失败---------------------", e);
		}
	}

}


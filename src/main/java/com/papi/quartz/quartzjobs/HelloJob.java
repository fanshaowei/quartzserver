package com.papi.quartz.quartzjobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;

import com.papi.netty.NettyClient;
import com.papi.quartz.commons.config.QuartzServerConfig;
import com.papi.quartz.service.NettyUtilService;

/**
 * 
 * @author fanshaowei
 *定时任务测试类
 */
public class HelloJob implements Job{
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {		
		
		ApplicationContext applicationContex = null;
		NettyUtilService nettyUtilService = null;
		try {
			 applicationContex = 
					(ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContextSchedulerContextKey");
			 QuartzServerConfig quartzServerConfig = (QuartzServerConfig) applicationContex.getBean("quartzServerConfig");
			 nettyUtilService = (NettyUtilService) applicationContex.getBean("nettyUtilService");		
		} catch (SchedulerException e) {		
			e.printStackTrace();
		}		
		
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();		
		JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
		String jobGroup = jobKey.getGroup();
		final String jobName = jobKey.getName();		
		TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();				
		
		JSONObject jsonWrite = new JSONObject();
		jsonWrite.element("type", "test");
		jsonWrite.element("jobName",jobName);
		
		NettyClient nettyClient = nettyUtilService.getNettyClient();
		
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		System.out.println("------" + jobName + "执行时间:" + df.format(new Date()) + "---------->");
		
		nettyClient.writeMesg(jsonWrite.toString());				
	
	}
    
}

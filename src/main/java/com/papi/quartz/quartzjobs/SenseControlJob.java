package com.papi.quartz.quartzjobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import com.papi.netty.NettyClient;
import com.papi.quartz.service.NettyUtilService;
import com.papi.quartz.service.RedisUtilService;

/**
 * 
 * @author fanshaowei
 * 定时情景控制 执行任务
 *
 */
public class SenseControlJob extends BasicJob{
	private Logger logger = Logger.getLogger(SenseControlJob.class.getName());
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		logger.info("-------------------SenseControlJob Begin-------------------------------");
		
		ApplicationContext applicationContex = null;
		RedisUtilService redisUtilService = null;
		NettyUtilService nettyUtilService = null;	
		try {
			 applicationContex = 
					(ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContextSchedulerContextKey");
			 
			//获取spring注入的bean
			 redisUtilService = (RedisUtilService) applicationContex.getBean("redisUtilService");			 
			 nettyUtilService = (NettyUtilService) applicationContex.getBean("nettyUtilService");			 
		} catch (SchedulerException e) {		
			e.printStackTrace();
		}	
		
		final String jobName = jobExecutionContext.getJobDetail().getKey().getName();//获取job名		
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();//获取存放在job map中的情景控制信息					
		
		String username = map.getString("username");//获取用户相关信息和情景ID	
		String doScene = map.getString("doScene");		
		String reqToken = redisUtilService.get("T" + username);//获取redis中的token
		if(reqToken == null){
			reqToken = "";
		}
		
		JSONArray doSceneJSONArray = JSONArray.fromObject(doScene);
		String sceneId = doSceneJSONArray.getJSONObject(0).getString("sceneId");
		
		final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		logger.info("------任务:" + jobName + " 触发时间:" + df.format(new Date()) + "---------->" + System.currentTimeMillis() );		
		
		JSONObject jsonWrite = new JSONObject();
		jsonWrite.element("type", "sceneCtl");
		jsonWrite.element("username",username);
		jsonWrite.element("sceneId",sceneId);
		jsonWrite.element("reqToken",reqToken);
		jsonWrite.element("jobName", jobName);
		
		NettyClient nettyClient = nettyUtilService.getNettyClient();
		nettyClient.writeMesg(jsonWrite.toString());
		
		logger.info("-------------------SenseControlJob End-------------------------------");
	}
    
}

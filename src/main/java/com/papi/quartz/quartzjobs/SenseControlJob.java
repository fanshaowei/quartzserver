package com.papi.quartz.quartzjobs;

import java.io.IOException;
import java.util.Date;

import net.sf.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import com.papi.quartz.commons.QuartzServerConfig;
import com.papi.quartz.enums.JobActionUrl;
import com.papi.quartz.utils.DateUtils;

/**
 * 情景控制 执行任务
 * @author fanshaowei
 *
 */
public class SenseControlJob extends BasicJob{
	private Logger logger = Logger.getLogger(SenseControlJob.class.getName());
	
	@SuppressWarnings("unused")
	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		logger.info("-------------------SenseControlJob Begin-------------------------------");
		
		ApplicationContext applicationContex = null;
		String smarthomeSenseUrl = "";
		try {
			 applicationContex = 
					(ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContextSchedulerContextKey");
			 QuartzServerConfig quartzServerConfig = (QuartzServerConfig) applicationContex.getBean("quartzServerConfig");
			 
			 //获取配置 调用接口的项目路径
			 smarthomeSenseUrl = quartzServerConfig.getSmarthomeSenseUrl();
		} catch (SchedulerException e) {		
			e.printStackTrace();
		}
		
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		
		String date = DateUtils.dateToString(new Date(), DateUtils.TIME_PATTERN_YMDHMS);
		//获取job相关信息
		String jobName = jobExecutionContext.getJobDetail().getKey().getName();
		String jobGroup = jobExecutionContext.getJobDetail().getKey().getGroup();
		JobDataMap map = jobExecutionContext.getJobDetail().getJobDataMap();		
		String triggerName = jobExecutionContext.getTrigger().getKey().getName();
		
		//获取用户相关信息和情景ID
		String username = map.getString("username");
		//String token = map.getString("req_token");		
		String doScene = map.getString("doScene");
		
		JSONArray doSceneJSONArray = JSONArray.fromObject(doScene);
		String sceneId = doSceneJSONArray.getJSONObject(0).getString("sceneId");
		
		//设置执行url地址
		String sceneUrl = smarthomeSenseUrl + JobActionUrl.SCENE_CONTROL.getUrl(); 
   	    sceneUrl = sceneUrl.replace(":username", username)
	       .replace(":idScene", sceneId);		
		System.out.println(sceneUrl);
		
		//创建htt客户端
		CloseableHttpClient httpClient = HttpClients.createDefault();
		//设置请求方式及连接超时时间							
		HttpGet httpGet = new HttpGet(sceneUrl);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(60 * 1000)
				.setConnectTimeout(60 * 1000)
				.build();
		httpGet.setConfig(requestConfig);
		//发送请求
		try {
			System.out.println("QuartzServer调用智能感知系统 情景控制接口....");
			HttpResponse response = httpClient.execute(httpGet);	
			HttpEntity httpEntity = response.getEntity();
			if(httpEntity != null){
	    		   String entityString = EntityUtils.toString(httpEntity);
	    		   
	    		   logger.info("QuartzServer调用智能感知系统 情景控制接口  返回信息....");
	    		   System.out.println(entityString);	    		   
	    	 } 
			
			jobDataMap.put("jobResult", "执行成功");
			logger.info("执行成功....");
		} catch (Exception e) {
			
			jobDataMap.put("jobResult", "执行失败");
			logger.info("QuartzServer调用智能感知系统 情景控制接口执行异常....");
			e.printStackTrace();
			
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		logger.info("-------------------SenseControlJob End-------------------------------");
	}
    
}

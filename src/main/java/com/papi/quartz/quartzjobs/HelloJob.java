package com.papi.quartz.quartzjobs;

import java.io.IOException;

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
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;

import com.papi.quartz.commons.QuartzServerConfig;
import com.papi.quartz.utils.DateUtils;

public class HelloJob extends BasicJob{
    private Logger logger  = Logger.getLogger(HelloJob.class.getName());
	
	@SuppressWarnings("unused")
	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {		
		
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
		JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
		String jobGroup = jobKey.getGroup();
		String jobName = jobKey.getName();		
		TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
		
		System.out.println("------" + jobName + "执行时间:" + DateUtils.sysDate("") + "------");
		
		String testUrl = smarthomeSenseUrl + "/quartzReqTest?jobName="+jobName;		
		//创建htt客户端
		CloseableHttpClient httpClient = HttpClients.createDefault();
		//设置请求方式及连接超时时间							
		HttpGet httpGet = new HttpGet(testUrl);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.build();
		httpGet.setConfig(requestConfig);
		//发送请求
		try {
			HttpResponse response = httpClient.execute(httpGet);	
			HttpEntity httpEntity = response.getEntity();
			if(httpEntity != null){
	    	    String entityString = EntityUtils.toString(httpEntity);
	    	    System.out.println("------" + jobName +"响应时间:" + DateUtils.sysDate("") + ",响应内容:" + entityString +"------");
	    	    System.out.println("");
	    	    //logger.info("QuartzServer测试调用接口  返回信息....");	    		   	    		  
			} 
			
			jobDataMap.put("jobResult", "执行成功");
			//logger.info("执行成功....");
		} catch (Exception e) {			
			jobDataMap.put("jobResult", "执行失败");
			logger.info("QuartzServer测试调用接口 执行异常....");
			e.printStackTrace();
			
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	}
    
}

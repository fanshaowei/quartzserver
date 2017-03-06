package com.papi.quartz.quartzjobs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
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
			 //获取配置 调用接口的项目路径
			 //smarthomeSenseUrl = quartzServerConfig.getSmarthomeSenseUrl();
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
		
		nettyClient.writeMesg(jsonWrite.toString());//
		
		/*String testUrl = smarthomeSenseUrl + "/quartzReqTest?jobName="+jobName;					
		final HttpGet httpGet = new HttpGet(testUrl);
		
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1000)
				.setConnectTimeout(1000)
				.build();
		
	    CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
	    		.setDefaultRequestConfig(requestConfig).build();
		
		//发送请求
		try {
			httpClient.start();
			final CountDownLatch latch = new CountDownLatch(1);
			httpClient.execute(httpGet, new FutureCallback<HttpResponse>(){
				@Override
				public void completed(HttpResponse result) {
					latch.countDown();
					HttpEntity httpEntity = result.getEntity();
					if(httpEntity != null){
			    	    String entityString = null;
						try {
							entityString = EntityUtils.toString(httpEntity);
							System.out.println("------" + jobName +"响应时间:" + df.format(new Date()) + ",响应内容:" + entityString +"------");
				    	    System.out.println("");
						} catch (ParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}			    	    			    	    	    		   	    		 
					} 					
				}//end completed

				@Override
				public void failed(Exception ex) {
					latch.countDown();
					System.out.println("---------------任务:" + jobName +" 失败----------");
				}

				@Override
				public void cancelled() {
					latch.countDown();
					System.out.println("---------------任务:" + jobName +" 被取消");
				}
				
			});
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}	
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
	
	}
    
}

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
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.papi.quartz.enums.JobActionUrl;
import com.papi.quartz.utils.DateUtils;

/**
 * 情景控制 执行任务
 * @author fanshaowei
 *
 */
public class SenseControlJob extends BasicJob{

	@SuppressWarnings("unused")
	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {			
					
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
		String sceneUrl = JobActionUrl.SCENE_CONTROL.getUrl(); 
   	    sceneUrl = sceneUrl.replace(":username", username)
	       .replace(":idScene", sceneId);		
		
		//创建htt客户端
		CloseableHttpClient httpClient = HttpClients.createDefault();
		//设置请求方式及连接超时时间							
		HttpGet httpGet = new HttpGet(sceneUrl);
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(3000)
				.setConnectTimeout(3000)
				.build();
		httpGet.setConfig(requestConfig);
		//发送请求
		try {
			HttpResponse response = httpClient.execute(httpGet);	
			HttpEntity httpEntity = response.getEntity();
			if(httpEntity != null){
	    		   String entityString = EntityUtils.toString(httpEntity);
	    		   System.out.print("--------------------------------------");
	    		   System.out.print(entityString);
	    		   System.out.print("--------------------------------------");	    		   
	    	 } 
		} catch (Exception e) {
			System.out.print("--------------------------------------");
			System.out.print("定时任务执行控制情景失败");
			System.out.print("--------------------------------------");
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

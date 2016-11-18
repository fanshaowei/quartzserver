package com.papi.quartz.service.impl;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import com.papi.quartz.bean.AppRequestJobInfo;
import com.papi.quartz.bean.JobInfo;
import com.papi.quartz.bean.ReturnBean;
import com.papi.quartz.bean.TriggerInfo;
import com.papi.quartz.enums.QuartzJobs;
import com.papi.quartz.quartzjobs.HelloJob;
import com.papi.quartz.service.AppJobService;
import com.papi.quartz.service.QuartzService;

/**
 * 
 * @author fanshaowei
 *
 *处理手机端的相关信息
 */
@Service("appJobService")
public class AppJobServiceImpl implements AppJobService {

	@Resource
    private QuartzService quartzService;
	
	@Override
	public String addSceneRelateJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo){		  	        	     
    	quartzService.quartzServiceImpl(servletContext);
		
    	JobInfo jobInfo = new JobInfo();
    	String idFamily = appRequestJobInfo.getIdFamily();
    	String jobName = appRequestJobInfo.getJobName();
    	JSONObject sourceScene = JSONObject.fromObject(appRequestJobInfo.getSourceScene());
    	JSONArray doScene = JSONArray.fromObject(appRequestJobInfo.getDoScene());
    	String validity = appRequestJobInfo.getValidity();
    	
    	//用家庭id来做任务组名
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	jobInfo.setJobClassName(QuartzJobs.SceneRelateJob.getClazz());
    	
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("jobType", "SceneRelateJob");
    	jobDataMap.put("username",appRequestJobInfo.getUserName());
    	jobDataMap.put("req_token",appRequestJobInfo.getReq_token());
    	jobDataMap.put("idFamily", idFamily);
    	jobDataMap.put("sourceScene",sourceScene.toString());
    	jobDataMap.put("doScene",doScene.toString()); 
    	jobDataMap.put("validity", validity);    	
    	if(validity.equals("everyDay")){
    		jobDataMap.put("sceneRelateJob_everyDayJob_Status", "正常");
    	}
    	jobInfo.setJobDataMap(jobDataMap);
    	
    	//判断任务是否存在
    	boolean isJobExist = quartzService.isJobExsit(jobName, idFamily);
    	if(isJobExist){
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", jobInfo.getJobGroup());
    		map.put("jobName", jobInfo.getJobName());
    		return ReturnBean.ReturnBeanToString("fail", "任务已存在", map);
    	}
    	//添加任务
    	boolean addJobFlag = quartzService.addNewJob(jobInfo);
    	if(!addJobFlag){    	  		    		
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", jobInfo.getJobGroup());
    		map.put("jobName", jobInfo.getJobName());
    		
    		return ReturnBean.ReturnBeanToString("fail", "添加任务失败", map);
    	}
    	//添加触发器(如果任务是在指定时间段内有效，关联任务会增加两个cron触发器，一个是触发使能关联任务，一个是触发关闭关联任务：即使关联任务表的数据状态isValid为1或0)
    	if(validity.equals("custom")){
    		TriggerInfo triggerInfo = new TriggerInfo();        	        	    	
    		triggerInfo.setTriggerGroup(idFamily);
    		triggerInfo.setTriggerState(appRequestJobInfo.getJobState());
    		triggerInfo.setTriggerType("CRON_TRIGGER");
    		
    		for(int i=0 ; i < appRequestJobInfo.getCronExpressionArray().length; i++ ){
    			String cronExpress = appRequestJobInfo.getCronExpressionArray()[i];
    			triggerInfo.setCronExpression(cronExpress);        	
    			
    			jobDataMap.clear();
            	if(i==0){            		
            		jobDataMap.put("operation", "open");
            		triggerInfo.setJobDataMap(jobDataMap);
            	}else{
            		jobDataMap.put("operation", "close");
            		triggerInfo.setJobDataMap(jobDataMap);
            	}
    			
            	triggerInfo.setTriggerName(jobName + "_" + UUID.randomUUID().toString());               	            	            	
            	boolean isTriggerExist = quartzService.isTriggerExist(jobName, triggerInfo.getTriggerName());
            	if(!isTriggerExist){
            		//添加触发器
                	boolean addTriggerInfo = quartzService.addTrigger(jobInfo, triggerInfo);
                	if(!addTriggerInfo){
                		Map<String,String> map = new HashMap<String,String>();
                		map.put("triggerGroup", triggerInfo.getTriggerGroup());
                		map.put("triggerName", triggerInfo.getTriggerName());
                		
                		return ReturnBean.ReturnBeanToString("fail", "添加任务触发器失败", map );
                	}
            	}            	
        	} //end for
    	}
    	
    	if(appRequestJobInfo.getJobState().equals("PAUSE")){
    		quartzService.jobPause(jobInfo);
    	}
    	
		return null;
	}

	@Override
	public String addDailySceneControlJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo){
		quartzService.quartzServiceImpl(servletContext);
		
		JobInfo jobInfo = new JobInfo();    	
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	jobInfo.setJobClassName(QuartzJobs.SenseControlJob.getClazz());
    	
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("jobType", "DailySceneControlJob");
    	jobDataMap.put("username",appRequestJobInfo.getUserName());
    	jobDataMap.put("req_token",appRequestJobInfo.getReq_token());
    	jobDataMap.put("doScene",JSONArray.fromObject(appRequestJobInfo.getDoScene()).toString());
    	
    	jobInfo.setJobDataMap(jobDataMap);
    	
    	boolean isJobExist = quartzService.isJobExsit(jobName, idFamily);
    	if(isJobExist){
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", idFamily);
    		map.put("jobName", jobName);
    		
    		return ReturnBean.ReturnBeanToString("fail", "任务已存在", map);
    	}
    	
    	//添加任务
    	boolean addJobFlag = quartzService.addNewJob(jobInfo);
    	if(!addJobFlag){    	  		    		
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", idFamily);
    		map.put("jobName", jobName);
 
    		return ReturnBean.ReturnBeanToString("fail", "添加任务失败", map);
    	}
    	
    	//添加触发器
    	TriggerInfo triggerInfo = new TriggerInfo();  
    	if(appRequestJobInfo.getTriggerType().equals("DAILY_TRIGGER")){
    		triggerInfo.setTriggerName(jobName + "_" + UUID.randomUUID().toString());
    		triggerInfo.setTriggerGroup(appRequestJobInfo.getIdFamily());
    		triggerInfo.setTriggerState(appRequestJobInfo.getJobState());
    		triggerInfo.setTriggerType("DAILY_TRIGGER");        		
    		triggerInfo.setRepeatTrigger(appRequestJobInfo.getIsRepeatTrigger());
    		triggerInfo.setRepeatCount(appRequestJobInfo.getRepeatCount());
    		triggerInfo.setRepeatInterval(appRequestJobInfo.getRepeatInterval());
    		triggerInfo.setRepeatIntervalUnit(appRequestJobInfo.getRepeatIntervalUnit());
    		triggerInfo.setDayOfWeek(appRequestJobInfo.getDayOfWeek());
    		//触发器的有效日期
    		if(appRequestJobInfo.getDailyTimeArray()!=null && appRequestJobInfo.getDailyTimeArray().length>1){
    			triggerInfo.setDailyStartTime(appRequestJobInfo.getDailyTimeArray()[0]);
    			triggerInfo.setDailyEndTime(appRequestJobInfo.getDailyTimeArray()[1]);
    		}
    		//触发器的执行有效时间点/有效见时间段
    		triggerInfo.setStartTimeOfDay(appRequestJobInfo.getTimeOfDayArray()[0]);
    		if(appRequestJobInfo.getTimeOfDayArray().length>1){
    			triggerInfo.setEndTimeOfDay(appRequestJobInfo.getTimeOfDayArray()[1]);
    		}
    		
    		//添加触发器
        	boolean addTriggerInfo = quartzService.addTrigger(jobInfo, triggerInfo);
        	if(!addTriggerInfo){
        		Map<String,String> map = new HashMap<String,String>();
        		map.put("triggerGroup", triggerInfo.getTriggerGroup());
        		map.put("triggerName", triggerInfo.getTriggerName());
        		
        		return ReturnBean.ReturnBeanToString("fail", "添加任务触发器失败", map );
        	}
        	        	 		        	
    	}//end if
    	
    	return null;
	}
	
	@Override
	public String addSimpleSceneControlJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo){
		quartzService.quartzServiceImpl(servletContext);
		
		JobInfo jobInfo = new JobInfo();    	
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	jobInfo.setJobClassName(QuartzJobs.SenseControlJob.getClazz());
    	
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("jobType", "SimpleSceneControlJob");
    	jobDataMap.put("username",appRequestJobInfo.getUserName());
    	jobDataMap.put("req_token",appRequestJobInfo.getReq_token());
    	jobDataMap.put("doScene",JSONArray.fromObject(appRequestJobInfo.getDoScene()).toString());
    	
    	jobInfo.setJobDataMap(jobDataMap);
    	
    	boolean isJobExist = quartzService.isJobExsit(jobName, idFamily);
    	if(isJobExist){
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", idFamily);
    		map.put("jobName", jobName);
    		
    		return ReturnBean.ReturnBeanToString("fail", "任务已存在", map);
    	}
    	//添加任务
    	boolean addJobFlag = quartzService.addNewJob(jobInfo);
    	if(!addJobFlag){    	  		    		
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", idFamily);
    		map.put("jobName", jobName);
    		
    		return ReturnBean.ReturnBeanToString("fail", "添加任务失败", map);
    	}
    	//添加触发器
    	TriggerInfo triggerInfo = new TriggerInfo();
    	if(appRequestJobInfo.getTriggerType().equals("SIMPLE_TRIGGER")){
    		triggerInfo.setTriggerName(jobName + "_" + UUID.randomUUID().toString());
    		triggerInfo.setTriggerGroup(idFamily);
    		triggerInfo.setTriggerState(appRequestJobInfo.getJobState());
    		triggerInfo.setTriggerType("SIMPLE_TRIGGER");
    		triggerInfo.setRepeatTrigger(appRequestJobInfo.getIsRepeatTrigger());
    		triggerInfo.setRepeatCount(appRequestJobInfo.getRepeatCount());
    		triggerInfo.setRepeatInterval(appRequestJobInfo.getRepeatInterval());
    		triggerInfo.setRepeatIntervalUnit(appRequestJobInfo.getRepeatIntervalUnit());        		        		        		      			        		        		    		
    		triggerInfo.setSimpleStartDateStr(appRequestJobInfo.getSimpletDateArray()[0]);
    		
    		if(appRequestJobInfo.getSimpletDateArray().length == 2)
    		    triggerInfo.setSimpleEndDateStr(appRequestJobInfo.getSimpletDateArray()[1]);
    		
    		//添加触发器
        	boolean addTriggerInfo = quartzService.addTrigger(jobInfo, triggerInfo);
        	if(!addTriggerInfo){
        		Map<String,String> map = new HashMap<String,String>();
        		map.put("triggerGroup", triggerInfo.getTriggerGroup());
        		map.put("triggerName", triggerInfo.getTriggerName());
        		
        		return ReturnBean.ReturnBeanToString("fail", "添加任务触发器失败", map );
        	}
    		
    	}
		
		return null;
	}
	
	@Override
	public String updateSceneRelateJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo){
		quartzService.quartzServiceImpl(servletContext);
		 
		String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	//获取原来job的dataMap    
    	JobDataMap jobDataMap_old = quartzService.getJobDataMap(jobName, idFamily);
    	String validity_old = jobDataMap_old.getString("validity");
    	
    	//设置要更改的job的新信息
    	JSONObject sourceScene = JSONObject.fromObject(appRequestJobInfo.getSourceScene());
    	JSONArray doScene = JSONArray.fromObject(appRequestJobInfo.getDoScene());
    	String validity = appRequestJobInfo.getValidity();
    	
    	JobInfo jobInfo = new JobInfo();
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	jobInfo.setJobClassName(QuartzJobs.SceneRelateJob.getClazz());
    	
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("jobType", "SceneRelateJob");
    	jobDataMap.put("username",appRequestJobInfo.getUserName());
    	jobDataMap.put("req_token",appRequestJobInfo.getReq_token());
    	jobDataMap.put("idFamily", idFamily);
    	jobDataMap.put("sourceScene",sourceScene.toString());
    	jobDataMap.put("doScene",doScene.toString());  
    	jobDataMap.put("validity", validity);
    	if(validity.equals("everyDay")){
    		jobDataMap.put("sceneRelateJob_everyDayJob_Status", "正常");
    	}
    	jobInfo.setJobDataMap(jobDataMap);    	    
    	//更改任务信息
    	boolean addJobFlag = quartzService.addNewJob(jobInfo);

    	if(!addJobFlag){
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", idFamily);
    		map.put("jobName", jobName);
    		
    		return ReturnBean.ReturnBeanToString("fail", "更新情景关联任务信息失败", map);
    	}
    	
    	//更改触发器(validity取值 everyDay时，为关联任务每天都有效，因此不加触发器去控制关联任务的有效时间段)
    	if(validity_old.equals("everyDay")){
    		if(validity.equals("custom")){
    			TriggerInfo triggerInfo = new TriggerInfo();        	        	    	
        		triggerInfo.setTriggerGroup(idFamily);
        		triggerInfo.setTriggerState(appRequestJobInfo.getJobState());
        		triggerInfo.setTriggerType("CRON_TRIGGER");
        		
        		for(int i=0 ; i < appRequestJobInfo.getCronExpressionArray().length; i++ ){
        			String cronExpress = appRequestJobInfo.getCronExpressionArray()[i];
        			triggerInfo.setCronExpression(cronExpress);        	
        			
        			jobDataMap.clear();
                	if(i==0){            		
                		jobDataMap.put("operation", "open");
                		triggerInfo.setJobDataMap(jobDataMap);
                	}else{
                		jobDataMap.put("operation", "close");
                		triggerInfo.setJobDataMap(jobDataMap);
                	}
        			
                	triggerInfo.setTriggerName(jobName + "_" + UUID.randomUUID().toString());               	            	            	
                	boolean isTriggerExist = quartzService.isTriggerExist(jobName, triggerInfo.getTriggerName());
                	if(!isTriggerExist){
                		//添加触发器
                    	boolean addTriggerInfo = quartzService.addTrigger(jobInfo, triggerInfo);
                    	if(!addTriggerInfo){
                    		Map<String,String> map = new HashMap<String,String>();
                    		map.put("triggerGroup", triggerInfo.getTriggerGroup());
                    		map.put("triggerName", triggerInfo.getTriggerName());
                    		
                    		return ReturnBean.ReturnBeanToString("fail", "添加任务触发器失败", map );
                    	}
                	}            	
            	} //end for
        		        		
    		}
    	}//end if
    	
    	if(validity_old.equals("custom")){
    		if(validity.equals("custom")){
    			TriggerInfo triggerInfo = new TriggerInfo();        	        	
    			triggerInfo.setTriggerGroup(idFamily);
    			triggerInfo.setTriggerState(appRequestJobInfo.getJobState());
    			triggerInfo.setTriggerType("CRON_TRIGGER");
    								
    			List<? extends Trigger> triggerList = quartzService.getTriggersOfJob(jobName, idFamily);	
    			String[] cronExpressionArray = appRequestJobInfo.getCronExpressionArray();
    			for(int i = 0; i< appRequestJobInfo.getCronExpressionArray().length; i++){
    				String name = triggerList.get(i).getKey().getName();
    				String cronExpress = cronExpressionArray[i];
    				triggerInfo.setCronExpression(cronExpress);
    				triggerInfo.setTriggerName(name);
    				
    				jobDataMap.clear();
                	if(i==0){            		
                		jobDataMap.put("operation", "open");
                		triggerInfo.setJobDataMap(jobDataMap);
                	}else{
                		jobDataMap.put("operation", "close");
                		triggerInfo.setJobDataMap(jobDataMap);
                	}
    				
    				boolean editTriggerInfo = quartzService.editTrigger(jobInfo, triggerInfo);
    	        	if(!editTriggerInfo){
    	        		Map<String,String> map = new HashMap<String,String>();
    	        		map.put("triggerGroup", triggerInfo.getTriggerGroup());
    	        		map.put("triggerName", triggerInfo.getTriggerName());
    	        		
    	        		return ReturnBean.ReturnBeanToString("fail", "更新情景关联任务触发器失败", map );
    	        	}
    			}//end for

    		}else if(validity.equals("everyDay")){
    			TriggerInfo triggerInfo = new TriggerInfo();        	        	
    			triggerInfo.setTriggerGroup(idFamily);    			
    			
    			List<? extends Trigger> triggerList = quartzService.getTriggersOfJob(jobName, idFamily);
    			for(int i = 0; i< triggerList.size(); i++){
    				String name = triggerList.get(i).getKey().getName();
    				triggerInfo.setTriggerName(name);
    				quartzService.deleteTrigger(triggerInfo);
    			}    			
    		}//end else if
    	}
    	
    	return null;
	}
	
	public String updateDailySceneControlJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo){
		quartzService.quartzServiceImpl(servletContext);
		
		String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	JobInfo jobInfo = new JobInfo();
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	jobInfo.setJobClassName(QuartzJobs.SenseControlJob.getClazz());
    	
    	JobDataMap jobDataMap = new JobDataMap();
    	jobDataMap.put("jobType", "DailySceneControlJob");
    	jobDataMap.put("username",appRequestJobInfo.getUserName());
    	jobDataMap.put("req_token",appRequestJobInfo.getReq_token());    
    	jobDataMap.put("doScene",JSONArray.fromObject(appRequestJobInfo.getDoScene()).toString());    	
    	jobInfo.setJobDataMap(jobDataMap);
    	
    	boolean addJobFlag = quartzService.addNewJob(jobInfo);
    	if(!addJobFlag){
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("jobGroup", idFamily);
    		map.put("jobName", jobName);
    		
    		return ReturnBean.ReturnBeanToString("fail", "更新每日定时情景控制任务信息失败", map);
    	}
    	
    	//更改触发器
    	TriggerInfo triggerInfo = new TriggerInfo(); 
    	
		triggerInfo.setTriggerGroup(appRequestJobInfo.getIdFamily());
		triggerInfo.setTriggerState(appRequestJobInfo.getJobState());
		triggerInfo.setTriggerType("DAILY_TRIGGER");        		
		triggerInfo.setRepeatTrigger(appRequestJobInfo.getIsRepeatTrigger());
		triggerInfo.setRepeatCount(appRequestJobInfo.getRepeatCount());
		triggerInfo.setRepeatInterval(appRequestJobInfo.getRepeatInterval());//***
		triggerInfo.setRepeatIntervalUnit(appRequestJobInfo.getRepeatIntervalUnit());//***
		triggerInfo.setDayOfWeek(appRequestJobInfo.getDayOfWeek());//***

		//触发器的有效日期
		if(appRequestJobInfo.getDailyTimeArray()!=null && appRequestJobInfo.getDailyTimeArray().length>1){
			triggerInfo.setDailyStartTime(appRequestJobInfo.getDailyTimeArray()[0]);
			triggerInfo.setDailyEndTime(appRequestJobInfo.getDailyTimeArray()[1]);
		}

        //触发器的执行有效时间点/有效见时间段
		triggerInfo.setStartTimeOfDay(appRequestJobInfo.getTimeOfDayArray()[0]);//***		
		if(appRequestJobInfo.getTimeOfDayArray().length>1){
			triggerInfo.setEndTimeOfDay(appRequestJobInfo.getTimeOfDayArray()[1]);	
		}		   
		
		List<? extends Trigger> triggerList = quartzService.getTriggersOfJob(jobName, idFamily);
		if(triggerList != null && triggerList.size() >= 0){
			String name = triggerList.get(0).getKey().getName();			
			triggerInfo.setTriggerName(name);	
		}else{
			return ReturnBean.ReturnBeanToString("fail", "更新的任务没有触发器", null);
		}
				
		boolean editTriggerInfo = quartzService.editTrigger(jobInfo, triggerInfo);
    	if(!editTriggerInfo){
    		Map<String,String> map = new HashMap<String,String>();
    		map.put("triggerGroup", triggerInfo.getTriggerGroup());
    		map.put("triggerName", triggerInfo.getTriggerName());
    		
    		return ReturnBean.ReturnBeanToString("fail", "更新每日时情景控制任务触发器失败", map );
    	}
    	
    	return null;
	}
	
	//加入测试数据到任务调度器
	public void importRedisData(ServletContext servletContext,String requestStr){   	        	      
    	quartzService.quartzServiceImpl(servletContext);
		
		JSONObject requestJson = JSONObject.fromObject(requestStr);	
		Integer jobCnt = requestJson.getInt("jobCnt");		
		String batchCronExpress = requestJson.getString("batchCronExpress");
        
		for(int i=0; i<jobCnt; i++){
			JobDetail job = newJob(HelloJob.class).withIdentity("job"+ String.valueOf(i), "group"+String.valueOf(i))
					.build();
			CronTrigger trigger =  newTrigger().withIdentity("trigger"+String.valueOf(i), "group"+String.valueOf(i))
					.withSchedule(cronSchedule(batchCronExpress))
					.build();
			
			try {
				if(quartzService.isJobExsit("job"+ String.valueOf(i), "group"+String.valueOf(i))){
					continue;
				}else{
					quartzService.getTheScheduler().scheduleJob(job, trigger);	
				}				
			} catch (SchedulerException e) {				
				e.printStackTrace();
			}			
		}//end for
				
	}
	
}

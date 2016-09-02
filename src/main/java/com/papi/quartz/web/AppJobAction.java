package com.papi.quartz.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.papi.quartz.bean.AppRequestJobInfo;
import com.papi.quartz.bean.JobInfo;
import com.papi.quartz.bean.ReturnBean;
import com.papi.quartz.bean.SenseDeviceSceneRelate;
import com.papi.quartz.bean.TriggerInfo;
import com.papi.quartz.enums.QuartzJobs;
import com.papi.quartz.service.QuartzService;
import com.papi.quartz.service.SenseDeviceSceneRelateService;
import com.papi.quartz.service.impl.QuartzServiceImpl;
import com.papi.quartz.utils.CommonUtils;

@Controller
@RequestMapping("/appjob")
public class AppJobAction {
	
    private QuartzService quartzService;
    
    @Resource
    SenseDeviceSceneRelateService senseDeviceSceneRelateService;    
    
    /**
     * 添加情景关联任务
     * @param request
     * @return
     */
    @RequestMapping(value="/addSceneRelateJob",method = RequestMethod.POST)
    public @ResponseBody String addSceneRelateJob(HttpServletRequest request){    	
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	        	
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	JobInfo jobInfo = new JobInfo();
    	String idGateway = appRequestJobInfo.getIdGateway();
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
		
    	//保存关联任务到数据库
    	SenseDeviceSceneRelate senseDeviceSceneRelate = new SenseDeviceSceneRelate();
    	senseDeviceSceneRelate.setIdFamily(Integer.parseInt(idFamily));
    	senseDeviceSceneRelate.setIdGateway(idGateway);
    	senseDeviceSceneRelate.setJobName(jobName);
    	senseDeviceSceneRelate.setIdDevice(sourceScene.getString("idDevice"));
    	
    	JSONObject sceneJson = new JSONObject();
    	sceneJson.accumulate("doScene", doScene);
    	sceneJson.accumulate("username", appRequestJobInfo.getUserName());
    	
    	senseDeviceSceneRelate.setSceneJson(sceneJson.toString());
    	senseDeviceSceneRelate.setIsValid("1");
    	
    	try {
			this.senseDeviceSceneRelateService.add(senseDeviceSceneRelate);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","添加关联任务信息到数据库失败",null);
		}
    	return ReturnBean.ReturnBeanToString("succeed","成功添加情景关联任务",null);
    }
    
    /**
     * 添加每天定时情景控制任务
     * @param request
     * @return
     */
    @RequestMapping(value="/addDailySceneControlJob",method = RequestMethod.POST)
    public @ResponseBody String addDailySceneControlJob(HttpServletRequest request){    	
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	        	
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);        	
    	
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
        	        	 		        	
    	}
    	
    	return ReturnBean.ReturnBeanToString("succeed","成功添加每日定时情景任务",null);
    }
    
    
    /**
     * 添加简单情景控制
     * @param request
     * @return
     */
    @RequestMapping(value="/addSimpleSceneControlJob",method = RequestMethod.POST)
    public @ResponseBody String addSimpleSceneControlJob(HttpServletRequest request){
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	        	
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);        	
    	
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
    	
    	return ReturnBean.ReturnBeanToString("succeed","成功添简单情景任务",null);
    }
        
    /**
     * 根据组名（家庭ID）获取任务信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getAllJob",method = RequestMethod.GET)
    public @ResponseBody List<JobInfo> getAllJob(HttpServletRequest request,@RequestParam("idFamily") String idFamily){
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);        
    	
    	return quartzService.getJobsByGroupName(idFamily);
    }
    
    /**
     * 根据任务类型获取相关任务
     * @param request
     */
    @RequestMapping(value = "/getJobByJobType",method = RequestMethod.GET)
    public @ResponseBody List<JobInfo> getJobByJobType(HttpServletRequest request,
    		@RequestParam("jobType") String jobType, @RequestParam("idFamily") String idFamily){
    	
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);    	   
        
    	AppRequestJobInfo appRequestJobInfo =  new AppRequestJobInfo();  
    	appRequestJobInfo.setIdFamily(idFamily);
    	appRequestJobInfo.setJobType(jobType);
    	
    	List<JobInfo> jobInfoList = quartzService.getJobsByGroupName(idFamily);
    	List<JobInfo> returnJobInfoList = new ArrayList<JobInfo>();
    	JobDataMap jobDataMap ;
    	String jobType_temp;
    	for(JobInfo jobInfo : jobInfoList){
    		jobDataMap = jobInfo.getJobDataMap();
    		if(jobDataMap.containsKey("jobType")){//是否存在任务类型    			
    				
    			jobType_temp = jobDataMap.getString("jobType");//获取任务类型
    			
	    		if(jobType_temp.equals(jobType)){//比较任务类型	    				    			
	    			
	    			if(jobType.equals("SceneRelateJob")){//如果是关联任务，加一个标识，用来判断 everyDay 没有触发器的情况
	    				if(!jobDataMap.containsKey("sceneRelateJob_everyDayJob_Status") && jobDataMap.getString("validity").equals("everyDay")){
		    				jobDataMap.put("sceneRelateJob_everyDayJob_Status", "正常");
		    				jobInfo.setStatus(jobDataMap.getString("sceneRelateJob_everyDayJob_Status"));
		    			}else if(jobDataMap.containsKey("sceneRelateJob_everyDayJob_Status") && jobDataMap.getString("validity").equals("everyDay")){
		    				jobInfo.setStatus(jobDataMap.getString("sceneRelateJob_everyDayJob_Status"));
		    			}	    					    				
	    			}
	    			
	    			returnJobInfoList.add(jobInfo);
	    		}
    		}	
    	}    	 	
    	
    	return returnJobInfoList;
    }
    
    /**
     * 更新情景关联任务
     * @param request
     * @return
     * @throws SchedulerException 
     */
    @RequestMapping(value="updateSceneRelateJob",method=RequestMethod.POST)
    public @ResponseBody String updateSceneRelateJob(HttpServletRequest request) throws SchedulerException{
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String idGateway = appRequestJobInfo.getIdGateway();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	//获取原来job的dataMap    
    	JobDataMap jobDataMap_old = quartzService.getJobDataMap(jobName, idFamily);
    	String validity_old = jobDataMap_old.getString("validity");
    	String idDevice_old = JSONObject.fromObject(jobDataMap_old.get("sourceScene")).getString("idDevice");
    	
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
    	
    	int idParam = 0;
    	Map<String,Object> mapParam = new HashMap<String,Object>();
    	mapParam.put("idFamily", idFamily);
    	mapParam.put("idDevice", idDevice_old);
    	mapParam.put("jobName", jobName);
    	try {
			List<SenseDeviceSceneRelate> senseDeviceSceneRelateList = senseDeviceSceneRelateService.find(mapParam);
			if(senseDeviceSceneRelateList != null && senseDeviceSceneRelateList.size()>0){
				idParam = senseDeviceSceneRelateList.get(0).getId();
			}			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    	
		//更改关联任务状态为1，执行关联
		SenseDeviceSceneRelate senseDeviceSceneRelate = new SenseDeviceSceneRelate();
		senseDeviceSceneRelate.setId(idParam);
    	senseDeviceSceneRelate.setIdFamily(Integer.parseInt(idFamily));  
    	senseDeviceSceneRelate.setIdGateway(idGateway);
    	senseDeviceSceneRelate.setIdDevice(sourceScene.getString("idDevice"));    	    	    	    	
    	senseDeviceSceneRelate.setIsValid("1");    	    
    	
    	JSONObject sceneJson = new JSONObject();
    	sceneJson.accumulate("doScene", doScene);
    	sceneJson.accumulate("username", appRequestJobInfo.getUserName());
    	
    	senseDeviceSceneRelate.setSceneJson(sceneJson.toString());
    	try {
			this.senseDeviceSceneRelateService.update(senseDeviceSceneRelate);
		} catch (Exception e) {
			e.printStackTrace();
			return  ReturnBean.ReturnBeanToString("fail", "更新情景关联数据表数据状态失败", null);
		}
    	      		 	
    	return  ReturnBean.ReturnBeanToString("succeed","成功更新情景关联任务",null);
    }
    
    /**
     * 更新每天定时控制情景任务
     * @param request
     * @return
     */
    @RequestMapping(value="updateDailySceneControlJob",method=RequestMethod.POST)
    public @ResponseBody String updateDailySceneControlJob(HttpServletRequest request){
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
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
    	
    	return ReturnBean.ReturnBeanToString("succeed","成功更新每日时情景控制任务",null);
    }
    
   /**
    * 暂停任务
    * @param request
    * @return
    */
    @RequestMapping(value="pauseJob",method=RequestMethod.POST)
    public @ResponseBody String pauseJob(HttpServletRequest request){
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	JobInfo jobInfo = new JobInfo();
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	
    	if(quartzService.isJobExsit(jobName, idFamily)){
    		boolean flag = quartzService.jobPause(jobInfo);
        	if(!flag){
        		return ReturnBean.ReturnBeanToString("fail", "暂停任务失败", null);
        	}
    	}else{
    		return ReturnBean.ReturnBeanToString("fail", "暂停的任务不存在", null);
    	}
    	
    	JobDataMap jobDataMap_temp = quartzService.getJobDataMap(jobName, idFamily);
    	String jobType = jobDataMap_temp.getString("jobType");
    	
    	if(jobType.equals("SceneRelateJob")){    		
    		JSONObject sourceScene = JSONObject.fromObject(jobDataMap_temp.getString("sourceScene"));
    		Map<String,Object> map = new HashMap<String,Object>();    		
    		map.put("idDevice", sourceScene.getString("idDevice"));
    		map.put("idFamily", idFamily);
    		try {
    			List<SenseDeviceSceneRelate> senseDeviceSceneRelates = this.senseDeviceSceneRelateService.find(map);
    			for (SenseDeviceSceneRelate senseDeviceSceneRelate : senseDeviceSceneRelates) {
    				senseDeviceSceneRelate.setIsValid("0");
    				this.senseDeviceSceneRelateService.update(senseDeviceSceneRelate);
				}
			} catch (Exception e) {
				ReturnBean.ReturnBeanToString("fail", "暂停任务失败", null);
			}
    		
    		if(jobDataMap_temp.getString("validity").equals("everyDay")){
    			jobDataMap_temp.put("sceneRelateJob_everyDayJob_Status", "暂停");
        		
        		jobInfo.setJobClassName(QuartzJobs.SceneRelateJob.getClazz());
        		jobInfo.setJobDataMap(jobDataMap_temp);
        		jobInfo.setJobGroup(idFamily);
        		jobInfo.setJobName(jobName);
        		quartzService.addNewJob(jobInfo);
    		}
    		
    	}    	
    	
    	return ReturnBean.ReturnBeanToString("succeed", "暂停任务成功", null);    	
    }
    
    /**
     * 启动任务
     * @param request
     * @return
     */
    @RequestMapping(value="resumeJob",method=RequestMethod.POST)
    public @ResponseBody String resumeJob(HttpServletRequest request){
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	JobInfo jobInfo = new JobInfo();
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	
    	if(quartzService.isJobExsit(jobName, idFamily)){
    		boolean flag = quartzService.jobResume(jobInfo);
        	if(!flag){
        		return ReturnBean.ReturnBeanToString("fail", "启动任务失败", null);
        	}
    	}else{
    		return ReturnBean.ReturnBeanToString("fail", "任务不存在", null);
    	}
    	
    	JobDataMap jobDataMap_temp = quartzService.getJobDataMap(jobName, idFamily);
    	String jobType = jobDataMap_temp.getString("jobType");
    	
    	if(jobType.equals("SceneRelateJob")){    		
    		JSONObject sourceScene = JSONObject.fromObject(jobDataMap_temp.getString("sourceScene"));
    		Map<String,Object> map = new HashMap<String,Object>();    		
    		map.put("idDevice", sourceScene.getString("idDevice"));
    		map.put("idFamily", idFamily);
    		try {
    			List<SenseDeviceSceneRelate> senseDeviceSceneRelates = this.senseDeviceSceneRelateService.find(map);
    			for (SenseDeviceSceneRelate senseDeviceSceneRelate : senseDeviceSceneRelates) {
    				senseDeviceSceneRelate.setIsValid("1");
    				this.senseDeviceSceneRelateService.update(senseDeviceSceneRelate);
				}
			} catch (Exception e) {
				ReturnBean.ReturnBeanToString("fail", "启动任务失败", null);
			}
    		
    		if(jobDataMap_temp.getString("validity").equals("everyDay")){
    			jobDataMap_temp.put("sceneRelateJob_everyDayJob_Status", "正常");
        		
        		jobInfo.setJobClassName(QuartzJobs.SceneRelateJob.getClazz());
        		jobInfo.setJobDataMap(jobDataMap_temp);
        		jobInfo.setJobGroup(idFamily);
        		jobInfo.setJobName(jobName);
        		quartzService.addNewJob(jobInfo);
    		}
    		
    	}
    	
    	return ReturnBean.ReturnBeanToString("succeed", "启动任务成功", null);
    }
    
    
    
    /**
     * 删除任务
     * @param request
     * @return
     */
    @RequestMapping(value="deleteJob",method=RequestMethod.POST)
    public @ResponseBody String deleteJob(HttpServletRequest request){
    	
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService = new QuartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	JobInfo jobInfo = new JobInfo();
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	    	         	
    	//获取原来job的dataMap       	
    	JobDataMap jobDataMap_temp = quartzService.getJobDataMap(jobName, idFamily);
    	String jobType = jobDataMap_temp.getString("jobType");
    	
    	//当删除任务类型为自定义时在关联表中删除
    	if(jobType.equals("SceneRelateJob")){
    		SenseDeviceSceneRelate senseDeviceSceneRelate = new SenseDeviceSceneRelate();
			JSONObject sourceScene = JSONObject.fromObject(jobDataMap_temp.getString("sourceScene"));
			if(sourceScene != null && sourceScene.size() > 0){
				senseDeviceSceneRelate.setIdDevice(sourceScene.getString("idDevice"));	
			}else{
				senseDeviceSceneRelate.setIdDevice("0");
			}			
			senseDeviceSceneRelate.setJobName(jobName);
    		try {	
    			this.senseDeviceSceneRelateService.deleteById(senseDeviceSceneRelate);
    		} catch (Exception e) {
    			e.printStackTrace();
    			return ReturnBean.ReturnBeanToString("fail","删除任务失败",null);
    		}	
    	}
    	
    	if(quartzService.isJobExsit(jobName, idFamily)){
    		boolean flag = quartzService.deleteJob(jobInfo);
        	if(!flag){
        		return ReturnBean.ReturnBeanToString("fail", "删除任务失败", null);
        	}
    	}else{
    		return ReturnBean.ReturnBeanToString("fail", "删除任务不存在", null);
    	}
    
		return ReturnBean.ReturnBeanToString("succeed", "删除任务成功", null);    	
    }
    
}

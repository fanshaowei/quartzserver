package com.papi.quartz.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.papi.quartz.bean.AppRequestJobInfo;
import com.papi.quartz.bean.JobInfo;
import com.papi.quartz.bean.ReturnBean;
import com.papi.quartz.bean.SenseDeviceSceneRelate;
import com.papi.quartz.enums.QuartzJobs;
import com.papi.quartz.service.AppJobService;
import com.papi.quartz.service.QuartzService;
import com.papi.quartz.service.QutzJobFiredDetailsService;
import com.papi.quartz.service.RedisUtilService;
import com.papi.quartz.service.SenseDeviceSceneRelateService;

import com.papi.quartz.utils.CommonUtils;

/**
 * 
 * @author fanshaowei
 *
 *手机端app情景接口
 */
@Controller
@RequestMapping("/appjob")
public class AppJobAction {
	
	@Resource
    private QuartzService quartzService;
	@Resource 
	private AppJobService appJobService;
    @Resource
    private SenseDeviceSceneRelateService senseDeviceSceneRelateService;     
    @Resource
	private QutzJobFiredDetailsService qutzJobFiredDetailsService;
    @Resource
    private RedisUtilService redisUtilService;
    
    /**
     * 添加情景关联任务
     * @param request
     * @return
     */
    @RequestMapping(value="/addSceneRelateJob",method = RequestMethod.POST)
    public @ResponseBody String addSceneRelateJob(HttpServletRequest request){    	
    	ServletContext servletContext = request.getServletContext();     	        	      
    	quartzService.quartzServiceImpl(servletContext);    
    	
    	String repuestStr = CommonUtils.reqtoString(request);           	    	    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	/************************添加job************************/
    	String mess = appJobService.addSceneRelateJob(servletContext, appRequestJobInfo);
    	if(mess != null){
    		return mess;
    	}
    	
    	/************************添加关联信息************************/
    	String idGateway = appRequestJobInfo.getIdGateway();
    	String idFamily = appRequestJobInfo.getIdFamily();
    	String jobName = appRequestJobInfo.getJobName();
    	String userName = appRequestJobInfo.getUserName();
    	JSONObject sourceScene = JSONObject.fromObject(appRequestJobInfo.getSourceScene());
    	JSONArray doScene = JSONArray.fromObject(appRequestJobInfo.getDoScene());
    	boolean isBetweenConfigTime = appRequestJobInfo.getIsBetweenConfigTime();    	  					    	
    	
    	JSONObject sceneJson = new JSONObject();
    	sceneJson.accumulate("doScene", doScene);
    	sceneJson.accumulate("username", userName);
    	
    	//保存关联任务到数据库
    	SenseDeviceSceneRelate senseDeviceSceneRelate = new SenseDeviceSceneRelate();
    	senseDeviceSceneRelate.setIdFamily(Integer.parseInt(idFamily));
    	senseDeviceSceneRelate.setIdGateway(idGateway);
    	senseDeviceSceneRelate.setJobName(jobName);
    	senseDeviceSceneRelate.setIdDevice(sourceScene.getString("idDevice"));
    	senseDeviceSceneRelate.setTriggerSourceJson(sourceScene.toString());
    	senseDeviceSceneRelate.setSceneJson(sceneJson.toString());
    	if(isBetweenConfigTime){
    		senseDeviceSceneRelate.setIsValid("1");
    	} else {
    		senseDeviceSceneRelate.setIsValid("0");
    	}
    		    	
    	try {
			this.senseDeviceSceneRelateService.add(senseDeviceSceneRelate);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","添加关联任务信息到数据库失败",null);
		}
    	
    	/************************保存任务信息到redis************************/
    	try{
    		redisUtilService.sAdd("Quartz-" + idFamily, repuestStr);
    	}catch(Exception e){
    		e.printStackTrace();
    		return ReturnBean.ReturnBeanToString("fail","添加任务信息到Redis失败",null);
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
    	quartzService.quartzServiceImpl(servletContext);
    	        	
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);        	
    	
    	/************************添加job************************/
    	String mess = appJobService.addDailySceneControlJob(servletContext, appRequestJobInfo);
    	if(mess != null){
    		return mess;
    	}
    	
 
    	/************************保存任务信息到redis************************/
    	String idFamily = appRequestJobInfo.getIdFamily();
    	try{
    		redisUtilService.sAdd("Quartz-" + idFamily, repuestStr);
    	}catch(Exception e){
    		e.printStackTrace();
    		return ReturnBean.ReturnBeanToString("fail","添加任务信息到Redis失败",null);
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
    	quartzService.quartzServiceImpl(servletContext);
    	        	
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);        	
    	
    	/************************添加job************************/
    	String mess = appJobService.addSimpleSceneControlJob(servletContext, appRequestJobInfo);
    	if(mess != null){
    		return mess;
    	}
    	
       	/************************保存任务信息到redis************************/
    	String idFamily = appRequestJobInfo.getIdFamily();
    	try{
    		redisUtilService.sAdd("Quartz-" + idFamily, repuestStr);
    	}catch(Exception e){
    		e.printStackTrace();
    		return ReturnBean.ReturnBeanToString("fail","添加任务信息到Redis失败",null);
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
    	quartzService.quartzServiceImpl(servletContext);   
    	    	
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
    	quartzService.quartzServiceImpl(servletContext);	   
        
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
    				
    			jobType_temp = jobDataMap.getString("jobType");//获取原任务类型
    			
	    		if(jobType_temp.equals(jobType)){//比较请求的任务类型 和 原任务的类型	    				    			
	    			
	    			if(jobType.equals("SceneRelateJob")){//如果是关联任务，加一个标识，用来判断 关联任务执行的有效时间是 everyDay ,没有触发器的情况
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
    	quartzService.quartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String idGateway = appRequestJobInfo.getIdGateway();
    	String jobName =  appRequestJobInfo.getJobName();
    	boolean isBetweenConfigTime = appRequestJobInfo.getIsBetweenConfigTime();
    	
    	//获取原来job的dataMap,(注：这个值要在更新job之前先获取)    
    	JobDataMap jobDataMap_old = quartzService.getJobDataMap(jobName, idFamily);
    	String idDevice_old = JSONObject.fromObject(jobDataMap_old.get("sourceScene")).getString("idDevice");
    	
    	//设置要更改的job的新信息
    	JSONObject sourceScene = JSONObject.fromObject(appRequestJobInfo.getSourceScene());
    	JSONArray doScene = JSONArray.fromObject(appRequestJobInfo.getDoScene()); 
    	
    	/************************更新job************************/
    	String mess = appJobService.updateSceneRelateJob(servletContext, appRequestJobInfo);
    	if(mess != null){
    		return mess;
    	}
    	
    	/************************更新关联信息************************/    	 	        	    	
    	int idParam = 0;
    	Map<String,Object> mapParam = new HashMap<String,Object>();
    	mapParam.put("idFamily", idFamily);
    	mapParam.put("idDevice", idDevice_old);
    	mapParam.put("jobName", jobName);
    	try {
			List<SenseDeviceSceneRelate> senseDeviceSceneRelateList = senseDeviceSceneRelateService.find(mapParam);
			if(senseDeviceSceneRelateList != null && senseDeviceSceneRelateList.size()>0){
				idParam = senseDeviceSceneRelateList.get(0).getId();
				
				//更改关联任务状态为1，执行关联
				SenseDeviceSceneRelate senseDeviceSceneRelate = new SenseDeviceSceneRelate();
				senseDeviceSceneRelate.setId(idParam);
		    	//senseDeviceSceneRelate.setIdFamily(Integer.parseInt(idFamily));  
		    	senseDeviceSceneRelate.setIdGateway(idGateway);
		    	senseDeviceSceneRelate.setIdDevice(sourceScene.getString("idDevice")); 
		    	senseDeviceSceneRelate.setTriggerSourceJson(sourceScene.toString());
		    	if(isBetweenConfigTime){
		    		senseDeviceSceneRelate.setIsValid("1");
		    	} else {
		    		senseDeviceSceneRelate.setIsValid("0");
		    	}    	    	        	
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
			}//end if			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
    			
    	
    	/************************更新redis信息,先删除旧的，再添加更新的************************/
    	try {			
			String objStr = null;
			AppRequestJobInfo appRequestJobInfo_temp = null;
			String setKey = "Quartz-" + idFamily;
			Set<Object> jobSet = redisUtilService.sMembers(setKey);
			for(Object obj : jobSet){
				objStr = obj.toString();
				appRequestJobInfo_temp = 
				    (AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(objStr), AppRequestJobInfo.class);
				if(jobName.equals(appRequestJobInfo_temp.getJobName())){
					redisUtilService.remove(setKey, obj);
					break;
				}
			}
			redisUtilService.sAdd(setKey, repuestStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","更新任务信息到Redis失败",null);
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
    	quartzService.quartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	/************************更新job************************/
    	appJobService.updateDailySceneControlJob(servletContext, appRequestJobInfo);    	
    	
    	/************************更新redis信息,先删除旧的，再添加更新的************************/
    	String idFamily = appRequestJobInfo.getIdFamily();
    	String jobName = appRequestJobInfo.getJobName();
    	try {			
			String objStr = null;
			AppRequestJobInfo appRequestJobInfo_temp = null;
			String setKey = "Quartz-" + idFamily;
			Set<Object> jobSet = redisUtilService.sMembers(setKey);
			for(Object obj : jobSet){
				objStr = obj.toString();
				appRequestJobInfo_temp = 
				    (AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(objStr), AppRequestJobInfo.class);
				if(appRequestJobInfo_temp.getJobName().equals(jobName)){
					redisUtilService.remove(setKey, obj);
					break;
				}
			}
			redisUtilService.sAdd(setKey, repuestStr);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","更新任务信息到Redis失败",null);
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
    	quartzService.quartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	/************************暂停job************************/
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
    		map.put("jobName", jobName);
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
    		
    	}//end if 
    	
    	/************************更新redis中job的jobstate************************/    	
    	try {			
			String objStr = null;
			AppRequestJobInfo appRequestJobInfo_temp = null;
			String setKey = "Quartz-" + idFamily;
			Set<Object> jobSet = redisUtilService.sMembers(setKey);
			for(Object obj : jobSet){
				objStr = obj.toString();
				appRequestJobInfo_temp = 
				    (AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(objStr), AppRequestJobInfo.class);				
				
				if(appRequestJobInfo_temp.getJobName().equals(jobName)){
					redisUtilService.remove(setKey, obj);
					
					JSONObject jsonObject = JSONObject.fromObject(obj);
					jsonObject.put("jobState", "PAUSE");
					redisUtilService.sAdd(setKey, jsonObject.toString());
					break;
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","暂停任务时更新信息到Redis失败",null);
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
    	quartzService.quartzServiceImpl(servletContext);
    	 
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
    		map.put("jobName", jobName);
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
    	
    	/************************更新redis中job的jobstate************************/
    	try {			
			String objStr = null;
			AppRequestJobInfo appRequestJobInfo_temp = null;
			String setKey = "Quartz-" + idFamily;
			Set<Object> jobSet = redisUtilService.sMembers(setKey);
			for(Object obj : jobSet){
				objStr = obj.toString();
				appRequestJobInfo_temp = 
				    (AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(objStr), AppRequestJobInfo.class);				
				
				if(appRequestJobInfo_temp.getJobName().equals(jobName)){
					redisUtilService.remove(setKey, obj);
					
					JSONObject jsonObject = JSONObject.fromObject(obj);
					jsonObject.put("jobState", "NORMAL");
					redisUtilService.sAdd(setKey, jsonObject.toString());
					break;
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","恢复任务时更新信息到Redis失败",null);
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
    	quartzService.quartzServiceImpl(servletContext);
    	 
    	String repuestStr = CommonUtils.reqtoString(request);    	
    	AppRequestJobInfo appRequestJobInfo = 
    			(AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(repuestStr), AppRequestJobInfo.class);
    	
    	/************************删除任务及任务关联信息************************/
    	String idFamily= appRequestJobInfo.getIdFamily();
    	String jobName =  appRequestJobInfo.getJobName();
    	
    	JobInfo jobInfo = new JobInfo();
    	jobInfo.setJobGroup(idFamily);
    	jobInfo.setJobName(jobName);
    	    	         	
    	//获取原来job的dataMap       	
    	JobDataMap jobDataMap_temp = quartzService.getJobDataMap(jobName, idFamily);
    	String jobType = jobDataMap_temp.getString("jobType");
    	
    	//当删除任务类型为 情景关联任务 时，在关联表中删除情景关联信息
    	if(jobType.equals("SceneRelateJob")){
    		SenseDeviceSceneRelate senseDeviceSceneRelate = new SenseDeviceSceneRelate();
			JSONObject sourceScene = JSONObject.fromObject(jobDataMap_temp.getString("sourceScene"));
			if(sourceScene != null && sourceScene.size() > 0){
				senseDeviceSceneRelate.setIdDevice(sourceScene.getString("idDevice"));	
			}else{
				senseDeviceSceneRelate.setIdDevice("0");
			}			
			senseDeviceSceneRelate.setJobName(jobName);
			senseDeviceSceneRelate.setIdFamily(Integer.parseInt(idFamily));
    		try {	
    			this.senseDeviceSceneRelateService.deleteSenseDeviceSceneRelate(senseDeviceSceneRelate);
    		} catch (Exception e) {
    			e.printStackTrace();
    			return ReturnBean.ReturnBeanToString("fail","删除情景关联任务 关联信息 失败",null);
    		}	
    	}
    	
    	//删除任务日志
    	Map<String,Object> paramMap = new HashMap<String, Object>();
    	paramMap.put("jobName", jobName);
		paramMap.put("jobGroup", idFamily);
		try {
			qutzJobFiredDetailsService.deleteQutzJobFiredDetails(paramMap);
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","删除任务日志 失败",null);
		}
    	
    	//删除任务记录
    	if(quartzService.isJobExsit(jobName, idFamily)){
    		boolean flag = quartzService.deleteJob(jobInfo);
        	if(!flag){
        		return ReturnBean.ReturnBeanToString("fail", "删除任务失败", null);
        	}
    	}else{
    		return ReturnBean.ReturnBeanToString("fail", "删除任务不存在", null);
    	}
    	
    	/************************删除redis任务信息************************/    	
    	try {			
			String objStr = null;
			AppRequestJobInfo appRequestJobInfo_temp = null;
			String setKey = "Quartz-" + idFamily;
			Set<Object> jobSet = redisUtilService.sMembers(setKey);
			for(Object obj : jobSet){
				objStr = obj.toString();
				appRequestJobInfo_temp = 
				    (AppRequestJobInfo)JSONObject.toBean(JSONObject.fromObject(objStr), AppRequestJobInfo.class);
				if(appRequestJobInfo_temp.getJobName().equals(jobName)){
					redisUtilService.remove(setKey, obj);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ReturnBean.ReturnBeanToString("fail","删除Redis任务信息失败",null);
		}
    
		return ReturnBean.ReturnBeanToString("succeed", "删除任务成功", null);    	
    }
    
}

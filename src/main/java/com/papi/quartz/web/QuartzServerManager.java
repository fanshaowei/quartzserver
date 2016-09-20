package com.papi.quartz.web;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.DateBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;
import org.quartz.examples.example2.SimpleJob;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import sun.nio.cs.UnicodeEncoder;

import com.papi.quartz.bean.JobInfo;
import com.papi.quartz.bean.QutzJobFiredDetails;
import com.papi.quartz.bean.TriggerInfo;
import com.papi.quartz.service.QuartzService;
import com.papi.quartz.service.QutzJobFiredDetailsService;
import com.papi.quartz.service.impl.QuartzServiceImpl;
import com.papi.quartz.utils.CommonUtils;
import com.papi.quartz.utils.DateUtils;

@Controller
@RequestMapping("/quartzServerManager")
public class QuartzServerManager {
	private Logger logger = Logger.getLogger(QuartzServerManager.class.getName());
	
	@Resource
	private QutzJobFiredDetailsService qutzJobFiredDetailsService;
	
	/**
	 * 获取任务列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/getAllJobDetails",method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getAllJobDetails(HttpServletRequest request){		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
		
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		List<JobInfo> jobInfoList =  (List<JobInfo>) quartzServiceImpl.getAllJobs();
		
		ArrayList<TriggerInfo> triggerInfoArr;
		for(JobInfo jobInfo : jobInfoList){
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("jobGroup", jobInfo.getJobGroup());
			map.put("jobName", jobInfo.getJobName());
			map.put("status", jobInfo.getStatus());
			map.put("jobDescription", jobInfo.getJobDescription());
			map.put("jobClassName", jobInfo.getJobClassName());
			
			triggerInfoArr = jobInfo.getTriggerInfoList();
			if(triggerInfoArr!=null && triggerInfoArr.size()>0){
				map.put("triggerType", triggerInfoArr.get(0).getTriggerType());			
				map.put("fireDate", triggerInfoArr.get(0).getPreviousFireTime());
				map.put("nextFireDate", triggerInfoArr.get(0).getNextFireTime());
				map.put("triggerInfoList",triggerInfoArr.get(0));
			}else{
				map.put("triggerType", "");			
				map.put("fireDate", "");
				map.put("nextFireDate", "");
				map.put("triggerInfoList",null);
			}
							
			mapList.add(map);
		}
		if(mapList!=null && mapList.size()>0){
			returnMap.put("total", mapList.size());
			returnMap.put("rows", mapList);
		}else{
			returnMap.put("total", 0);
			returnMap.put("rows", mapList);
		}
		return returnMap;
	}
	
	/**
	 * 编辑任务信息
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/editJobInfo",method = RequestMethod.POST)
	public @ResponseBody boolean editJobInfo(HttpServletRequest request){
		boolean flag = false;
		
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONObject requestJson = JSONObject.fromObject(requestStr);
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobGroup(requestJson.getString("jobGroup"));
		jobInfo.setJobName(requestJson.getString("jobName"));
		jobInfo.setJobDescription(requestJson.getString("jobDescription"));
		jobInfo.setJobClassName(requestJson.getString("jobClassName"));
				
		boolean isJobExsit = quartzServiceImpl.isJobExsit(requestJson.getString("jobName"), requestJson.getString("jobGroup"));
		if(!isJobExsit){
			flag = quartzServiceImpl.addNewJob(jobInfo);	
		}				
		
		TriggerInfo triggerInfo = new TriggerInfo();
		String triggerType = requestJson.getString("triggerType");
		String startDateTime = requestJson.getString("startDateTime");
		String endDateTime = requestJson.getString("endDateTime");
		Boolean isRepeatTrigger = requestJson.getBoolean("isRepeatTrigger");
		Integer repeatCount = requestJson.getInt("repeatCount");
		Integer repeatInterval = requestJson.getInt("repeatInterval");
		String repeatIntervalUnit = requestJson.getString("repeatIntervalUnit");
		String cronExpress = requestJson.getString("cronExpress");
		JSONArray dayOfWeekJsonArray = requestJson.getJSONArray("dayOfWeek");
				
		triggerInfo.setTriggerGroup(requestJson.getString("jobGroup"));
		triggerInfo.setTriggerName(requestJson.getString("jobName"));
		triggerInfo.setTriggerType(requestJson.getString("triggerType"));
		
		if(triggerType.equals("CRON_TRIGGER")){
			triggerInfo.setCronExpression(cronExpress);
		}else if(triggerType.equals("SIMPLE_TRIGGER")){
			triggerInfo.setSimpleStartDateStr(startDateTime);
			//triggerInfo.setSimpleEndDateStr(endDateTime);
			triggerInfo.setRepeatTrigger(isRepeatTrigger);
			triggerInfo.setRepeatCount(repeatCount);
			triggerInfo.setRepeatInterval(repeatInterval);
			triggerInfo.setRepeatIntervalUnit(repeatIntervalUnit);
		}else if(triggerType.equals("DAILY_TRIGGER")){
			String[] dayOfWeekStrArr = new String[dayOfWeekJsonArray.size()] ;
			for(int i=0; i< dayOfWeekJsonArray.size(); i++){
				dayOfWeekStrArr[i] = dayOfWeekJsonArray.getString(i);
			}
						
			triggerInfo.setStartTimeOfDay(startDateTime);
			//triggerInfo.setEndTimeOfDay(endDateTime);
			triggerInfo.setDayOfWeek(dayOfWeekStrArr);
			triggerInfo.setRepeatTrigger(isRepeatTrigger);
			triggerInfo.setRepeatCount(repeatCount);
			triggerInfo.setRepeatInterval(repeatInterval);
			triggerInfo.setRepeatIntervalUnit(repeatIntervalUnit);
		}
		
		
		boolean istriggerExist = quartzServiceImpl.isTriggerExist(requestJson.getString("jobName"), requestJson.getString("jobGroup"));
		if(isJobExsit && istriggerExist){
			flag = quartzServiceImpl.editTrigger(jobInfo, triggerInfo);
		}else{
			flag = quartzServiceImpl.addTrigger(jobInfo, triggerInfo);
		}				
		
		return flag;
	}
	
	/**
	 * 删除任务
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/deleteJobs",method = RequestMethod.POST)
	public @ResponseBody boolean deleteJobs(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONArray requestJsonArray = JSONArray.fromObject(requestStr);
		
		JobInfo jobInfo = new JobInfo();
		Map<String,Object> paramMap = new HashMap<String, Object>();
		for(int i=0; i<requestJsonArray.size(); i++){
			JSONObject jsonObject = requestJsonArray.getJSONObject(i);
						
			jobInfo.setJobGroup(jsonObject.getString("jobGroup"));
			jobInfo.setJobName(jsonObject.getString("jobName"));
			
			boolean flag = quartzServiceImpl.deleteJob(jobInfo);
			if(!flag){
				return false;
			}
			
			paramMap.put("jobName", jsonObject.getString("jobName"));
			paramMap.put("jobGroup", jsonObject.getString("jobGroup"));
			try {
				qutzJobFiredDetailsService.deleteQutzJobFiredDetails(paramMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * 暂停任务
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/pauseJobs",method = RequestMethod.POST)
	public @ResponseBody boolean pauseJobs(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONArray requestJsonArray = JSONArray.fromObject(requestStr);
		
		JobInfo jobInfo = new JobInfo();
		for(int i=0; i<requestJsonArray.size(); i++){
			JSONObject jsonObject = requestJsonArray.getJSONObject(i);
						
			jobInfo.setJobGroup(jsonObject.getString("jobGroup"));
			jobInfo.setJobName(jsonObject.getString("jobName"));
			
			boolean flag = quartzServiceImpl.jobPause(jobInfo);
			if(!flag){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 恢复任务
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/resumeJobs",method = RequestMethod.POST)
	public @ResponseBody boolean resumeJobs(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONArray requestJsonArray = JSONArray.fromObject(requestStr);
		
		JobInfo jobInfo = new JobInfo();
		List<? extends Trigger> triggerList;
		for(int i=0; i<requestJsonArray.size(); i++){
			JSONObject jsonObject = requestJsonArray.getJSONObject(i);
						
			jobInfo.setJobGroup(jsonObject.getString("jobGroup"));
			jobInfo.setJobName(jsonObject.getString("jobName"));
			
			boolean flag = quartzServiceImpl.jobResume(jobInfo);
			if(!flag){
				return false;
			}
						
			
		    triggerList = quartzServiceImpl.getTriggersOfJob(jsonObject.getString("jobName"), jsonObject.getString("jobGroup"));
		    Trigger trigger = triggerList.get(0);
			if((trigger instanceof SimpleTrigger)){
				TriggerState triggerState = null;
				TriggerKey triggerKey = trigger.getKey();
				try {
					triggerState = quartzServiceImpl.getTheScheduler().getTriggerState(triggerKey);
				} catch (SchedulerException e) {
					e.printStackTrace();
				}
				if(triggerState.equals(TriggerState.COMPLETE)){
					try {
						quartzServiceImpl.getTheScheduler().unscheduleJob(triggerKey);
					} catch (SchedulerException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 批量添加任务
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/batchAddJobs",method = RequestMethod.POST)
	public @ResponseBody boolean batchAddJobs(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);			
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONObject requestJson = JSONObject.fromObject(requestStr);	
		Integer jobCnt = requestJson.getInt("jobCnt");		
		String jobClassName = requestJson.getString("jobClassName");
		
		JobInfo jobInfo = new JobInfo();
		
		Date date = new Date();
	    Calendar calendar =  Calendar.getInstance();
	    calendar.setTime(date);
		
	    StringBuilder sb = new StringBuilder();
	    String sec;
	    String min;
	    String hour;
	    boolean flag;
	    String jobuuid;
	    String cronExpression;
		for(int i=0; i<jobCnt; i++){			
			int num  = this.getJobCounts(request)+1;
			
			sb.delete(0, sb.length());
			sb.append(String.valueOf(num));
			sb.append("_");
			sb.append(UUID.randomUUID().toString());
			
		    jobuuid = sb.toString();
		    jobInfo.setJobGroup(jobuuid);
		    jobInfo.setJobName(jobuuid);
		    jobInfo.setJobClassName(jobClassName);
		    
		    flag = quartzServiceImpl.addNewJob(jobInfo);
		    if(!flag){
		    	return false;
		    }		   
		    
		    calendar.add(Calendar.SECOND, 20);		    
		    sec = Integer.toString(calendar.get(Calendar.SECOND));
		    min = Integer.toString(calendar.get(Calendar.MINUTE));
		    hour = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		    //String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));
		    //String month = Integer.toString(calendar.get(Calendar.MONTH + 1));
		    
		    sb.delete(0, sb.length());
		    sb.append(sec);
		    sb.append(" ");
		    sb.append(min);
		    sb.append(" ");
		    sb.append(hour);
		    sb.append(" * * ?");		    		    
		    cronExpression = sb.toString();
		    
		    TriggerInfo triggerInfo = new TriggerInfo();
			triggerInfo.setTriggerGroup(jobuuid);
			triggerInfo.setTriggerName(jobuuid);
			triggerInfo.setTriggerType("CRON_TRIGGER");						
			triggerInfo.setCronExpression(cronExpression);
			
			flag = quartzServiceImpl.addTrigger(jobInfo, triggerInfo);
			
		}				
		
		return true;
	}	
	
	/**
	 * 获取任务执行日志
	 * @param jobName
	 * @param jobGroup
	 * @return
	 */
	@RequestMapping(value="/getJobLogs",method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getJobLogs(@RequestParam("jobName") String jobName, @RequestParam("jobGroup") String jobGroup){						
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try{
			paramMap.put("jobName", jobName);
			paramMap.put("jobGroup", jobGroup);	
		}catch(Exception ex){
			
		}		
		
		List<QutzJobFiredDetails> qutzJobFiredDetailsList = null;
		try {
			qutzJobFiredDetailsList = qutzJobFiredDetailsService.findQutzJobFiredDetails(paramMap);				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(qutzJobFiredDetailsList !=null && qutzJobFiredDetailsList.size()>0){
			returnMap.put("total", qutzJobFiredDetailsList.size());
			returnMap.put("rows", qutzJobFiredDetailsList);
		}else{
			returnMap.put("total", 0);
			returnMap.put("rows", qutzJobFiredDetailsList);
		}
		
		return returnMap;
	}
	
	/**
	 * 获取当前的任务数
	 * @param request
	 * @return
	 */
	public int getJobCounts(HttpServletRequest request){
		int jobCounts = 0;
		
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		try {
			List<String> allJobGroupNamesList = quartzServiceImpl.getTheScheduler().getJobGroupNames();
			Iterator<String> jobGroupIterator = allJobGroupNamesList.iterator();
			while(jobGroupIterator.hasNext()){
				//获取任务组名
				String groupNameStr = jobGroupIterator.next();
				//根据组名获取jobKey集合(jok由jonGroupName和jobName(唯一) 组成)
				Set<JobKey> jobKeySet = quartzServiceImpl.getTheScheduler().getJobKeys(GroupMatcher.jobGroupEndsWith(groupNameStr));
				
				jobCounts += jobKeySet.size();
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		return jobCounts;
	}
	
	
	@RequestMapping(value="/simpleTest",method = RequestMethod.GET)
	public void simpleTest(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		Date startTime = DateBuilder.nextGivenSecondDate(null, 10);
		JobDetail job = newJob(SimpleJob.class).withIdentity("job1", "group1").build();
		SimpleTrigger trigger = (SimpleTrigger) newTrigger().withIdentity("trigger1", "group1").startAt(startTime).build();
		
		try {
			quartzServiceImpl.getTheScheduler()
			.scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
}

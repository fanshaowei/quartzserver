package com.papi.quartz.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
	
	@Resource
	private QutzJobFiredDetailsService qutzJobFiredDetailsService;
	
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
	
	@RequestMapping(value="/deleteJobs",method = RequestMethod.POST)
	public @ResponseBody boolean deleteJobs(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONArray requestJsonArray = JSONArray.fromObject(requestStr);
		
		JobInfo jobInfo = new JobInfo();
		for(int i=0; i<requestJsonArray.size(); i++){
			JSONObject jsonObject = requestJsonArray.getJSONObject(i);
						
			jobInfo.setJobGroup(jsonObject.getString("jobGroup"));
			jobInfo.setJobName(jsonObject.getString("jobName"));
			
			boolean flag = quartzServiceImpl.deleteJob(jobInfo);
			if(!flag){
				return false;
			}
		}
		return true;
	}
	
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
	
	@RequestMapping(value="/resumeJobs",method = RequestMethod.POST)
	public @ResponseBody boolean resumeJobs(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONArray requestJsonArray = JSONArray.fromObject(requestStr);
		
		JobInfo jobInfo = new JobInfo();
		for(int i=0; i<requestJsonArray.size(); i++){
			JSONObject jsonObject = requestJsonArray.getJSONObject(i);
						
			jobInfo.setJobGroup(jsonObject.getString("jobGroup"));
			jobInfo.setJobName(jsonObject.getString("jobName"));
			
			boolean flag = quartzServiceImpl.jobResume(jobInfo);
			if(!flag){
				return false;
			}
		}
		return true;
	}
	
	@RequestMapping(value="/batchAddJobs",method = RequestMethod.POST)
	public @ResponseBody boolean batchAddJobs(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONObject requestJson = JSONObject.fromObject(requestStr);	
		Integer jobCnt = requestJson.getInt("jobCnt");		
		String jobClassName = requestJson.getString("jobClassName");
		
		JobInfo jobInfo = new JobInfo();
		for(int i=0; i<jobCnt; i++){
			String jobuuid = UUID.randomUUID().toString();
		    jobInfo.setJobGroup(jobuuid);
		    jobInfo.setJobName(jobuuid);
		    jobInfo.setJobClassName(jobClassName);
		    
		    boolean flag = quartzServiceImpl.addNewJob(jobInfo);
		    if(!flag){
		    	return false;
		    }
		}
		
		return true;
	}	
	
	@RequestMapping(value="/getJobLogs",method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getJobLogs(@RequestParam("jobName") String jobName, @RequestParam("jobGroup") String jobGroup){						
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		Map<String,Object> paramMap = new HashMap<String,Object>();
		try{
			paramMap.put("jobName", new String(jobName.getBytes("ISO-8859-1"),"UTF-8"));
			paramMap.put("jobGroup", new String(jobGroup.getBytes("ISO-8859-1"),"UTF-8"));	
		}catch(Exception ex){
			
		}		
		
		List<QutzJobFiredDetails> qutzJobFiredDetailsList = null;
		try {
			qutzJobFiredDetailsList = qutzJobFiredDetailsService.findQutzJobFiredDetails(paramMap);
			/*ListIterator<QutzJobFiredDetails> iterator = qutzJobFiredDetailsList.listIterator();
			while(iterator.hasNext()){
				QutzJobFiredDetails qjfd = iterator.next();
				qjfd.setFireDate(DateUtils.dateFormat(qjfd.getFireDate(), DateUtils.TIME_PATTERN_YMDHMS));
				qjfd.setNextFireDate(DateUtils.dateFormat(qjfd.getNextFireDate(), DateUtils.TIME_PATTERN_YMDHMS));
				qjfd.setStartDate(DateUtils.dateFormat(qjfd.getStartDate(), DateUtils.TIME_PATTERN_YMDHMS));
				qjfd.setEndDate(DateUtils.dateFormat(qjfd.getEndDate(), DateUtils.TIME_PATTERN_YMDHMS));
			}*/
			
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
}

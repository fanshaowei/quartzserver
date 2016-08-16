package com.papi.quartz.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.papi.quartz.bean.JobInfo;
import com.papi.quartz.enums.QuartzJobs;
import com.papi.quartz.service.QuartzService;
import com.papi.quartz.service.impl.QuartzServiceImpl;
import com.papi.quartz.utils.CommonUtils;

@Controller
@RequestMapping("quartzServerManager")
public class QuartzServerManager {
	
	@RequestMapping(value="getAllJobDetails",method = RequestMethod.GET)
	public @ResponseBody Map<String,Object> getAllJobDetails(HttpServletRequest request){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		List<JobInfo> jobInfoList =  (List<JobInfo>) quartzServiceImpl.getAllJobDetails();
		if(jobInfoList!=null && jobInfoList.size()>0){
		    returnMap.put("total", jobInfoList.size());
		    returnMap.put("rows", jobInfoList);
		}
		return returnMap;
	}
	
	@RequestMapping(value="editJobInfo",method = RequestMethod.POST)
	public @ResponseBody boolean editJobInfo(HttpServletRequest request){
		ServletContext servletContext = request.getServletContext(); 
		QuartzService quartzServiceImpl = new QuartzServiceImpl(servletContext);
		
		String requestStr = CommonUtils.reqtoString(request);
		JSONObject requestJson = JSONObject.fromObject(requestStr);
		
		JobInfo jobInfo = new JobInfo();
		jobInfo.setJobGroup(requestJson.getString("jobGroup"));
		jobInfo.setJobName(requestJson.getString("jobName"));
		jobInfo.setJobDescription(requestJson.getString("jobDescription"));
		jobInfo.setJobClassName(requestJson.getString("jobClassName"));
		
		boolean flag = quartzServiceImpl.addNewJob(jobInfo);
		
		return flag;
	}
	
	@RequestMapping(value="deleteJobs",method = RequestMethod.POST)
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
	
	@RequestMapping(value="pauseJobs",method = RequestMethod.POST)
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
	
	@RequestMapping(value="resumeJobs",method = RequestMethod.POST)
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
	
	@RequestMapping(value="batchAddJobs",method = RequestMethod.POST)
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
	
}

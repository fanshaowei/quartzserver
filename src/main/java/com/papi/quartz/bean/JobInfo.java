package com.papi.quartz.bean;

import java.io.Serializable;
import java.util.ArrayList;

import org.quartz.JobDataMap;

public class JobInfo implements Serializable{

	private static final long serialVersionUID = 1L;

	private String jobName;
	private String jobGroup;
	private String jobDescription;
	private String jobClassName;
	private JobDataMap jobDataMap;
	
	private String status;
	private ArrayList<TriggerInfo> triggerInfoList;
	  
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getJobClassName() {
		return jobClassName;
	}
	public void setJobClassName(String jobClassName) {
		this.jobClassName = jobClassName;
	}
	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}
	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public ArrayList<TriggerInfo> getTriggerInfoList() {
		return triggerInfoList;
	}
	public void setTriggerInfoList(ArrayList<TriggerInfo> triggerInfoList) {
		this.triggerInfoList = triggerInfoList;
	}	  	  
	 
}

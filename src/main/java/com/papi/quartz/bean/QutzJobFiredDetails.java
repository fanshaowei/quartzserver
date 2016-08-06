package com.papi.quartz.bean;

import java.io.Serializable;
import java.util.Date;

public class QutzJobFiredDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6698748880806642850L;

	private int id;
	private String jobName;
	private String jobGroup;
	private String triggerName;
	private String triggerGroup;
	private String jobType;
	private String jobStatus;
	private Date startDate;
	private Date endDate;
	private Date fireDate;
	private Date nextFireDate;
	private String triggerType;
	private String firedResult;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
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
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	public String getTriggerGroup() {
		return triggerGroup;
	}
	public void setTriggerGroup(String triggerGroup) {
		this.triggerGroup = triggerGroup;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	public Date getFireDate() {
		return fireDate;
	}
	public void setFireDate(Date fireDate) {
		this.fireDate = fireDate;
	}
	public Date getNextFireDate() {
		return nextFireDate;
	}
	public void setNextFireDate(Date nextFireDate) {
		this.nextFireDate = nextFireDate;
	}
	public String getTriggerType() {
		return triggerType;
	}
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	public String getFiredResult() {
		return firedResult;
	}
	public void setFiredResult(String firedResult) {
		this.firedResult = firedResult;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	
}

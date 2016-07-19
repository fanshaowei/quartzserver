package com.papi.quartz.bean;

import java.io.Serializable;
import java.util.Date;

public class SchedPostFiredDetails implements Serializable
{
	  private static final long serialVersionUID = 1L;
	  private Long firedId;
	  private String jobName;
	  private String jobGroup;
	  private String triggerName;
	  private String triggerGroup;
	  private String jobType;
	  private String jobStatus;
	  private Date startDate;
	  private Date endDate;
	  private Date firedDate;
	  private Date nextFiredDate;
	  private String triggerType;
	  private String firedResult;
	  private String linkUrl;
	  private String linkTitle;
	  private String linkType;
	  private String rtnResult;

	  public SchedPostFiredDetails()
	  {
	  }

	  public SchedPostFiredDetails(Long firedId)
	  {
	    this.firedId = firedId;
	  }

	  public SchedPostFiredDetails(Long firedId, String jobName, String jobGroup, String triggerName, String triggerGroup, String jobType, String jobStatus, Date startDate, Date endDate, Date firedDate, Date nextFiredDate, String triggerType, String firedResult, String rtnResult)
	  {
	    this.firedId = firedId;
	    this.jobName = jobName;
	    this.jobGroup = jobGroup;
	    this.triggerName = triggerName;
	    this.triggerGroup = triggerGroup;
	    this.jobType = jobType;
	    this.jobStatus = jobStatus;
	    this.startDate = startDate;
	    this.endDate = endDate;
	    this.firedDate = firedDate;
	    this.nextFiredDate = nextFiredDate;
	    this.triggerType = triggerType;
	    this.firedResult = firedResult;
	    this.rtnResult = rtnResult;
	  }

	  public SchedPostFiredDetails(Long firedId, String jobName, String jobGroup, String triggerName, String triggerGroup, String jobType, String jobStatus, Date startDate, Date endDate, Date firedDate, Date nextFiredDate, String triggerType, String firedResult, String linkUrl, String linkTitle, String linkType, String rtnResult)
	  {
	    this.firedId = firedId;
	    this.jobName = jobName;
	    this.jobGroup = jobGroup;
	    this.triggerName = triggerName;
	    this.triggerGroup = triggerGroup;
	    this.jobType = jobType;
	    this.jobStatus = jobStatus;
	    this.startDate = startDate;
	    this.endDate = endDate;
	    this.firedDate = firedDate;
	    this.nextFiredDate = nextFiredDate;
	    this.triggerType = triggerType;
	    this.firedResult = firedResult;
	    this.linkUrl = linkUrl;
	    this.linkTitle = linkTitle;
	    this.linkType = linkType;
	    this.rtnResult = rtnResult; 
	  } 
	  
	  public Long getFiredId() { return this.firedId; }

	  public void setFiredId(Long firedId)
	  {
	    this.firedId = firedId;
	  }

	  public String getJobName() {
	    return this.jobName;
	  }

	  public void setJobName(String jobName) {
	    this.jobName = jobName;
	  }

	  public String getJobGroup() {
	    return this.jobGroup;
	  }

	  public void setJobGroup(String jobGroup) {
	    this.jobGroup = jobGroup;
	  }

	  public String getTriggerName() {
	    return this.triggerName;
	  }

	  public void setTriggerName(String triggerName) {
	    this.triggerName = triggerName;
	  }

	  public String getTriggerGroup() {
	    return this.triggerGroup;
	  }

	  public void setTriggerGroup(String triggerGroup) {
	    this.triggerGroup = triggerGroup;
	  }

	  public String getJobType() {
	    return this.jobType;
	  }

	  public void setJobType(String jobType) {
	    this.jobType = jobType;
	  }

	  public String getJobStatus() {
	    return this.jobStatus;
	  }

	  public void setJobStatus(String jobStatus) {
	    this.jobStatus = jobStatus;
	  }

	  public Date getStartDate() {
	    return this.startDate;
	  }

	  public void setStartDate(Date startDate) {
	    this.startDate = startDate;
	  }

	  public Date getEndDate() {
	    return this.endDate;
	  }

	  public void setEndDate(Date endDate) {
	    this.endDate = endDate;
	  }

	  public Date getFiredDate() {
	    return this.firedDate;
	  }

	  public void setFiredDate(Date firedDate) {
	    this.firedDate = firedDate;
	  }

	  public Date getNextFiredDate() {
	    return this.nextFiredDate;
	  }

	  public void setNextFiredDate(Date nextFiredDate) {
	    this.nextFiredDate = nextFiredDate;
	  }

	  public String getTriggerType() {
	    return this.triggerType;
	  }

	  public void setTriggerType(String triggerType) {
	    this.triggerType = triggerType;
	  }

	  public String getFiredResult() {
	    return this.firedResult;
	  }

	  public void setFiredResult(String firedResult) {
	    this.firedResult = firedResult;
	  }

	  public String getLinkUrl() {
	    return this.linkUrl;
	  }

	  public void setLinkUrl(String linkUrl) {
	    this.linkUrl = linkUrl;
	  }

	  public String getLinkTitle() {
	    return this.linkTitle;
	  }

	  public void setLinkTitle(String linkTitle) {
	    this.linkTitle = linkTitle;
	  }

	  public String getLinkType() {
	    return this.linkType;
	  }

	  public void setLinkType(String linkType) {
	    this.linkType = linkType;
	  }

	  public String toString()
	  {
	    return "SchedPostFiredDetails [firedId=" + this.firedId + ", jobName=" + 
	      this.jobName + ", jobGroup=" + this.jobGroup + ", triggerName=" + 
	      this.triggerName + ", triggerGroup=" + this.triggerGroup + ", jobType=" + 
	      this.jobType + ", jobStatus=" + this.jobStatus + ", startDate=" + 
	      this.startDate + ", endDate=" + this.endDate + ", firedDate=" + 
	      this.firedDate + ", nextFiredDate=" + this.nextFiredDate + 
	      ", triggerType=" + this.triggerType + ", firedResult=" + 
	      this.firedResult + ", linkUrl=" + this.linkUrl + ", linkTitle=" + 
	      this.linkTitle + ", linkType=" + this.linkType + "]";
	  }

	  public String getRtnResult() {
	    return this.rtnResult;
	  }

	  public void setRtnResult(String rtnResult) {
	    this.rtnResult = rtnResult;
	  }
	}

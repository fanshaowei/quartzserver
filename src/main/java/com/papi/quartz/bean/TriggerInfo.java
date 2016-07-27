package com.papi.quartz.bean;

import java.io.Serializable;
import java.util.Date;

import org.quartz.JobDataMap;

import com.papi.quartz.utils.DateUtils;

public class TriggerInfo implements Serializable{

	  private static final long serialVersionUID = 1L;
      
	  private String triggerName;
	  private String triggerGroup;
	  private String triggerDescription;	  
	  private String triggerType;
	  private String triggerState;
	  private JobDataMap jobDataMap;
	  
	  private Date simpleStartDate;
	  private Date simpleEndDate;
	  private String simpleStartDateStr;
	  private String simpleEndDateStr;
	  private boolean isRepeatTrigger = false;//是否重复执行的触发器

/*	  private String secondIntervalTime = "0";//***	  
	  private String timeTriggerType;//***
	  private String intervalTime = "0:0:0:0";//****
*/	  private int repeatCount = -1;   //重复执行次数
	  private int repeatInterval;//重复执行的间隔
	  private String repeatIntervalUnit="MINUTE"; //重复类型(HOUR,MINUTE,SECOND)
	  
	  private Date cronStartDate;
	  private Date cronEndDate;
	  private String cronStartDateStr;
	  private String cronEndDateStr;
	  private String cronExpression = "0 0 0 * * ?";
	  
/*	  private String dailyTime = "00:00:00";//*****/
	  private String dailyStartTime = "2000-01-01 00:00:00";
	  private String dailyEndTime = "2500-01-01 23:59:59";
	  private String startTimeOfDay = "00:00:00"; //每天开始的时间  （格式  时：分：秒）
	  private String endTimeOfDay = "23:59:59";   //每天结束的时间
/*	  private String dailyIntervalTime;*/
	  private String[] dayOfWeek = {"everyDay"};	  	
	  
	 	  
	public String getTriggerType() {
		return triggerType;
	}
	
	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}
	
	public Date getSimpleStartDate() {
		return simpleStartDate;
	}
	
	public void setSimpleStartDate(Date simpleStartDate) {
		if(simpleStartDate != null){
			this.simpleStartDateStr = DateUtils.dateToString(simpleStartDate, DateUtils.TIME_PATTERN_YMDHMS);
		}
		this.simpleStartDate = simpleStartDate;
	}
	
	public Date getSimpleEndDate() {
		return simpleEndDate;
	}
	
	public void setSimpleEndDate(Date simpleEndDate) {
		if (simpleEndDate != null) {
		      this.simpleEndDateStr = DateUtils.dateToString(simpleEndDate, 
		        DateUtils.TIME_PATTERN_YMDHMS);
		}
		this.simpleEndDate = simpleEndDate;
	}
	
	public String getSimpleStartDateStr() {
		return simpleStartDateStr;
	}
	
	public void setSimpleStartDateStr(String simpleStartDateStr) {
		this.simpleStartDateStr = simpleStartDateStr;
	}
	
	public String getSimpleEndDateStr() {
		return simpleEndDateStr;
	}
	
	public void setSimpleEndDateStr(String simpleEndDateStr) {
		this.simpleEndDateStr = simpleEndDateStr;
	}
	
	
	
	public boolean getIsRepeatTrigger() {
		return isRepeatTrigger;
	}

	public void setRepeatTrigger(boolean isRepeatTrigger) {
		this.isRepeatTrigger = isRepeatTrigger;
	}
	
	public int getRepeatCount() {
		return repeatCount;
	}
	
	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}
	
	public Date getCronStartDate() {
		return cronStartDate;
	}
	
	public void setCronStartDate(Date cronStartDate) {
		if (cronStartDate != null) {
		      this.cronStartDateStr = DateUtils.dateToString(cronStartDate, 
		        DateUtils.TIME_PATTERN_YMDHMS);
		}
		this.cronStartDate = cronStartDate;
	}
	
	public Date getCronEndDate() {
		return cronEndDate;
	}
	public void setCronEndDate(Date cronEndDate) {
		if (cronEndDate != null) {
		      this.cronEndDateStr = DateUtils.dateToString(cronEndDate, 
		        DateUtils.TIME_PATTERN_YMDHMS);
		}
		this.cronEndDate = cronEndDate;
	}
	public String getCronStartDateStr() {
		return cronStartDateStr;
	}
	public void setCronStartDateStr(String cronStartDateStr) {
		this.cronStartDateStr = cronStartDateStr;
	}
	public String getCronEndDateStr() {
		return cronEndDateStr;
	}
	public void setCronEndDateStr(String cronEndDateStr) {
		this.cronEndDateStr = cronEndDateStr;
	}
	public String getDailyStartTime() {
		return dailyStartTime;
	}
	public void setDailyStartTime(String dailyStartTime) {
		this.dailyStartTime = dailyStartTime;
	}
	public String getDailyEndTime() {
		return dailyEndTime;
	}
	public void setDailyEndTime(String dailyEndTime) {
		this.dailyEndTime = dailyEndTime;
	}
	
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
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
	public String getTriggerDescription() {
		return triggerDescription;
	}
	public void setTriggerDescription(String triggerDescription) {
		this.triggerDescription = triggerDescription;
	}

	public int getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public String getTriggerState() {
		return triggerState;
	}

	public void setTriggerState(String triggerState) {
		this.triggerState = triggerState;
	}

	public String getRepeatIntervalUnit() {
		return repeatIntervalUnit;
	}

	public void setRepeatIntervalUnit(String repeatIntervalUnit) {
		this.repeatIntervalUnit = repeatIntervalUnit;
	}

	public String getStartTimeOfDay() {
		return startTimeOfDay;
	}

	public void setStartTimeOfDay(String startTimeOfDay) {
		this.startTimeOfDay = startTimeOfDay;
	}

	public String getEndTimeOfDay() {
		return endTimeOfDay;
	}

	public void setEndTimeOfDay(String endTimeOfDay) {
		this.endTimeOfDay = endTimeOfDay;
	}

	public String[] getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String[] dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	} 
	  	
}


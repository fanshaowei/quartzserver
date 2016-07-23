package com.papi.quartz.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * @author fanshaowei
 *
 */
public class AppRequestJobInfo implements Serializable {
	/**
	 * STATE_BLOCKED 4 STATE_COMPLETE 2 STATE_ERROR 3 STATE_NONE -1 STATE_NORMAL
	 * 0 STATE_PAUSED 1
	 */
	private static final long serialVersionUID = -7092479545958787696L;

	private String idFamily;
	private String req_token;
	private String userName;

	private String jobName; // 任务名
	private String jobState; // 任务状态
	private String jobType; // senceRelatied,senceDelay
	private String triggerType; // cronTrigger,simpleTrigger

	private String cronTriggerType; // workDay,weekEnd,daily,customer
	private String[] cronExpressionArray; // 触发器表达式
	private Map<String, String> sourceScene; // 情景触发源
	private ArrayList<Map<String, String>> doScene; // 情景关联
	private String validity; // 关联情景的有效时间段标识，everyDay或custom

	// simpleTrigger属性
	private String[] simpletDateArray;
	private boolean isRepeatTrigger;
	private int repeatCount = -1;
	private int repeatInterval;
	private String repeatIntervalUnit;

	private String dailyStartTime = "2000-01-01 00:00:00";// 可加时间也可不加：2000-01-01 00:00:00
	private String dailyEndTime = "2500-01-01 23:59:59";
	private String dailyTimeArray[];
	private String[] timeOfDayArray;
	private String[] dayOfWeek;

	public String getIdFamily() {
		return idFamily;
	}

	public void setIdFamily(String idFamily) {
		this.idFamily = idFamily;
	}

	public String getReq_token() {
		return req_token;
	}

	public void setReq_token(String req_token) {
		this.req_token = req_token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobState() {
		return jobState;
	}

	public void setJobState(String jobState) {
		this.jobState = jobState;
	}

	public String getJobType() {
		return jobType;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public String getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(String triggerType) {
		this.triggerType = triggerType;
	}

	public String getCronTriggerType() {
		return cronTriggerType;
	}

	public void setCronTriggerType(String cronTriggerType) {
		this.cronTriggerType = cronTriggerType;
	}

	public String[] getCronExpressionArray() {
		return cronExpressionArray;
	}

	public void setCronExpressionArray(String[] cronExpressionArray) {
		this.cronExpressionArray = cronExpressionArray;
	}

	public Map<String, String> getSourceScene() {
		return sourceScene;
	}

	public void setSourceScene(Map<String, String> sourceScene) {
		this.sourceScene = sourceScene;
	}

	public ArrayList<Map<String, String>> getDoScene() {
		return doScene;
	}

	public void setDoScene(ArrayList<Map<String, String>> doScene) {
		this.doScene = doScene;
	}

	public String[] getSimpletDateArray() {
		return simpletDateArray;
	}

	public void setSimpletDateArray(String[] simpletDateArray) {
		this.simpletDateArray = simpletDateArray;
	}

	public boolean getIsRepeatTrigger() {
		return isRepeatTrigger;
	}

	public void setIsRepeatTrigger(boolean isRepeatTrigger) {
		this.isRepeatTrigger = isRepeatTrigger;
	}

	public int getRepeatCount() {
		return repeatCount;
	}

	public void setRepeatCount(int repeatCount) {
		this.repeatCount = repeatCount;
	}

	public int getRepeatInterval() {
		return repeatInterval;
	}

	public void setRepeatInterval(int repeatInterval) {
		this.repeatInterval = repeatInterval;
	}

	public String getRepeatIntervalUnit() {
		return repeatIntervalUnit;
	}

	public void setRepeatIntervalUnit(String repeatIntervalUnit) {
		this.repeatIntervalUnit = repeatIntervalUnit;
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

	public String[] getTimeOfDayArray() {
		return timeOfDayArray;
	}

	public void setTimeOfDayArray(String[] timeOfDayArray) {
		this.timeOfDayArray = timeOfDayArray;
	}

	public String[] getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(String[] dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public String[] getDailyTimeArray() {
		return dailyTimeArray;
	}

	public void setDailyTimeArray(String[] dailyTimeArray) {
		this.dailyTimeArray = dailyTimeArray;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

}

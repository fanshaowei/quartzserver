package com.papi.quartz.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.DateBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.DailyTimeIntervalTriggerImpl;

import com.papi.quartz.bean.JobInfo;
import com.papi.quartz.bean.TriggerInfo;
import com.papi.quartz.enums.WeekOfNum;
import com.papi.quartz.service.QuartzService;
import com.papi.quartz.utils.DateUtils;
import com.papi.quartz.utils.QuartzUtils;


public class QuartzServiceImpl implements QuartzService,Serializable {    

	private static final long serialVersionUID = 1L;
    
	static Logger logger = Logger.getLogger(QuartzServiceImpl.class.getName());
	
	private Scheduler scheduler;
	private JobDetail jobDetail;
	private JobDataMap jobDataMap;	
	
	public QuartzServiceImpl(){}
	
	public QuartzServiceImpl(Scheduler scheduler){
		this.scheduler = scheduler;
	}
		
	public QuartzServiceImpl(ServletContext servletContext){
		StdSchedulerFactory factory = 
				(StdSchedulerFactory)servletContext.getAttribute("org.quartz.impl.StdSchedulerFactory.KEY");
		try {
			this.scheduler = factory.getScheduler();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	
	public Scheduler getTheScheduler(){
		return this.scheduler;
	}
	/**
	 * 添加新任务
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean addNewJob(JobInfo jobInfo) {
		String jobName = jobInfo.getJobName();
		String jobGroup = jobInfo.getJobGroup();
		String jobClass = jobInfo.getJobClassName();
		String description = jobInfo.getJobDescription();
		this.jobDataMap = jobInfo.getJobDataMap();
		
		try {
			//创建任务详情
			this.jobDetail = JobBuilder
					.newJob((Class<? extends Job>) Class.forName(jobClass))
					.withIdentity(jobName , jobGroup )
					.withDescription(description)
					.storeDurably(true)//新加的任务没有指定触发器，需要设置该属性为ture，不然会被销毁
					.build();
			//将任务需要交互的信息放到jobDataMap,在job执行executor时，可获取相关的信息
			if(this.jobDataMap != null){
				this.jobDetail.getJobDataMap().putAll(this.jobDataMap);	
			}
			
			//将任务加到调度器
			this.scheduler.addJob(this.jobDetail, true);
			
			return true;
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
				
		return false;
	}
	
	/**
	 * 获取所有任务
	 * @return
	 */
	@Override
	public List<JobInfo> getAllJobs(){
		List<JobInfo> jobInfoList= new ArrayList<JobInfo>();
		
		try {
			//获取所有任务组名
			List<String> allJobGroupNamesList = this.scheduler.getJobGroupNames();
			Iterator<String> jobGroupIterator = allJobGroupNamesList.iterator();
			while(jobGroupIterator.hasNext()){
				//获取任务组名
				String groupNameStr = jobGroupIterator.next();
				//根据组名获取jobKey集合(jok由jonGroupName和jobName(唯一) 组成)
				Set<JobKey> jobKeySet = this.scheduler.getJobKeys(GroupMatcher.jobGroupEndsWith(groupNameStr));
				Iterator<JobKey> jobKeyIterator = jobKeySet.iterator();
				while(jobKeyIterator.hasNext()){
					//获取jobKey
					JobKey jobKey = (JobKey) jobKeyIterator.next();
					//设置jobInfo的信息
					getJobInfoValue(jobKey,jobInfoList);
				}
			}
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return jobInfoList;
	}
	
	//获取所有任务的详细信息
	@Override
	public List<JobInfo> getAllJobDetails(){
		List<JobInfo> allJobInfoList = new ArrayList<JobInfo>();		
		List<? extends Trigger> triggerList = null;
		
		try {
			List<String> allJobGroupNamesList = this.scheduler.getJobGroupNames();
			Iterator<String> jobGroupIterator = allJobGroupNamesList.iterator();
			while(jobGroupIterator.hasNext()){
				String jobGroupName = jobGroupIterator.next();
				Set<JobKey> jobKeySet = this.scheduler.getJobKeys(GroupMatcher.jobGroupContains(jobGroupName));
				Iterator<JobKey> jobKeyIterator = jobKeySet.iterator();
				while(jobKeyIterator.hasNext()){
					//获取jobKey
					JobKey jobKey = jobKeyIterator.next();
					
					String jobGroup = jobKey.getGroup();
					String jobName = jobKey.getName();										
					
					JobDetail jobDetail = this.scheduler.getJobDetail(jobKey);
					String jobClassName =jobDetail.getJobClass().getName();
					String jobDescription = jobDetail.getDescription();
					
					JobInfo jobInfo = new JobInfo();
					jobInfo.setJobGroup(jobGroup);
					jobInfo.setJobName(jobName);
					jobInfo.setJobClassName(jobClassName);
					jobInfo.setJobDescription(jobDescription);
										
					triggerList = this.scheduler.getTriggersOfJob(jobKey);					
					String state = QuartzUtils.getJobStatus(triggerList, jobKey, this.scheduler);
					jobInfo.setStatus(state);
					
					allJobInfoList.add(jobInfo);
				}
			}
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return allJobInfoList;
	}
	
	/**
	 * 根据任务组名获取任务
	 * @param groupName
	 * @return
	 */
	@Override
	public List<JobInfo> getJobsByGroupName(String groupName){
		List<JobInfo> jobInfoList = new ArrayList<JobInfo>();
				
		Set<JobKey> jobKeySet = null;
		try {
			//获取所有任务组名
			List<String> allJobGroupNamesList = this.scheduler.getJobGroupNames();			
			Iterator<String> jobGroupIterator = allJobGroupNamesList.iterator();
			while(jobGroupIterator.hasNext()){
				//获取任务组名
				String groupNameStr = jobGroupIterator.next();
				//根据组名获取jobKey集合(jok由jonGroupName和jobName(唯一) 组成)
				if(groupNameStr.endsWith(groupName)){
					jobKeySet = this.scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName));
					break;
				}							
			}
			
			if(jobKeySet != null){
				Iterator<JobKey> jobKeySetIterator = jobKeySet.iterator();
				while(jobKeySetIterator.hasNext()){
					//获取jobKey
					JobKey jobKey = jobKeySetIterator.next();
					//设置jobInfo的信息
					getJobInfoValue(jobKey, jobInfoList);							
				}
			}		
										
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return jobInfoList;
	}	
	
	/**
	 * 获取查询要返回的jobInfo的信息
	 * @param jobKey
	 * @param jobInfoList
	 */
	public void getJobInfoValue(JobKey jobKey,List<JobInfo> jobInfoList){
		JobDetail jobDetail = null ;
		try {
			//获取jobDetail(jobDetail包含信息有jobClass,jobDataMap,description等信息)
			jobDetail = this.scheduler.getJobDetail(jobKey);
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		if(jobDetail != null){
			JobInfo jobInfo = new JobInfo();
			jobInfo.setJobName(jobKey.getName());
			jobInfo.setJobGroup(jobKey.getGroup());
			jobInfo.setJobDescription(jobDetail.getDescription());
			jobInfo.setJobDataMap(jobDetail.getJobDataMap());
			
			jobInfo.getJobDataMap().put("doScene", JSONArray.fromObject(jobInfo.getJobDataMap().getString("doScene")));
			jobInfo.getJobDataMap().put("sourceScene", JSONObject.fromObject(jobInfo.getJobDataMap().getString("sourceScene")));
						
			jobInfo.setJobClassName(jobDetail.getJobClass().getName());
			
			List<? extends Trigger> triggerList = null;
			try {
				triggerList = this.scheduler.getTriggersOfJob(jobKey);
			} catch (SchedulerException e) {				
				e.printStackTrace();
			}
			String state = QuartzUtils.getJobStatus(triggerList, jobKey, this.scheduler);
			jobInfo.setStatus(state);
			
			ArrayList<TriggerInfo> triggerInfoList = new ArrayList<TriggerInfo>();
			for(Trigger trigger : triggerList){
				TriggerInfo triggerInfo = getTriggerInfo(trigger);
				triggerInfoList.add(triggerInfo);
			}
			jobInfo.setTriggerInfoList(triggerInfoList);
			
			jobInfoList.add(jobInfo);
		}
	}
		
	/**
	 * 获取触发器信息triggerInfo
	 * @param trigger
	 * @return
	 */
	public TriggerInfo getTriggerInfo(Trigger trigger){
		TriggerInfo triggerInfo = new TriggerInfo();
		String triggerName = trigger.getKey().getName();
		String triggerGroup = trigger.getKey().getGroup();
		String triggerDescription = trigger.getDescription();
		JobDataMap jobDataMap = trigger.getJobDataMap();
		
		Trigger.TriggerState triggerState = null;
		try {
		     triggerState = this.scheduler.getTriggerState(trigger.getKey());
		     triggerInfo.setTriggerState(triggerState.toString());
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}			
		
		//简单触发器
		if((trigger instanceof SimpleTrigger)){
			SimpleTrigger simpleTrigger = (SimpleTrigger)trigger;			
			Date startTime = simpleTrigger.getStartTime();
			Date endTime = simpleTrigger.getEndTime();
			//触发器执行任务间隔(毫秒)
			Long repeatInterval = simpleTrigger.getRepeatInterval();
			//触发器执行任务的次数
			Integer repeatCount = simpleTrigger.getRepeatCount();					
			
			if(repeatCount>0)
				triggerInfo.setRepeatCount(repeatCount);
			//是否有重复间隔  
			if(repeatInterval > 0L){					
				String repeatIntervalUnit = simpleTrigger.getJobDataMap().getString("repeatIntervalUnit");
				if(repeatIntervalUnit.equals("HOUR")){
					triggerInfo.setRepeatIntervalUnit("HOUR");
					triggerInfo.setRepeatInterval(((repeatInterval.intValue())/1000/3600));
				}else if(repeatIntervalUnit.equals("MINUTE")){
					triggerInfo.setRepeatIntervalUnit("MINUTE");
					triggerInfo.setRepeatInterval(((repeatInterval.intValue())/1000/60));
				}else if(repeatIntervalUnit.equals("SECOND")){
					triggerInfo.setRepeatIntervalUnit("SECOND");
					triggerInfo.setRepeatInterval(((repeatInterval.intValue())/1000));
				}
				triggerInfo.setRepeatTrigger(true);				
			}else{
				triggerInfo.setRepeatTrigger(false);
				triggerInfo.setRepeatCount(0);			
			}
			
			triggerInfo.setTriggerName(triggerName);
			triggerInfo.setTriggerGroup(triggerGroup);
			triggerInfo.setTriggerDescription(triggerDescription);
			triggerInfo.setSimpleStartDate(startTime);
			triggerInfo.setSimpleEndDate(endTime);
			//triggerInfo.setTimeTriggerType("interval");//触发方式  间隔触发
			triggerInfo.setTriggerType("SIMPLE_TRIGGER");//简单触发器
		}
		//每天触发器
		else if(trigger instanceof DailyTimeIntervalTrigger){
			DailyTimeIntervalTrigger dailyTimeIntervalTrigger = (DailyTimeIntervalTrigger) trigger;			
			Date startTime = dailyTimeIntervalTrigger.getStartTime();
			Date endTime = dailyTimeIntervalTrigger.getEndTime();
			TimeOfDay startTimeOfDay = dailyTimeIntervalTrigger.getStartTimeOfDay();
			TimeOfDay endTimeOfDay = dailyTimeIntervalTrigger.getEndTimeOfDay();
			Set<Integer> set = dailyTimeIntervalTrigger.getDaysOfWeek();										
			
			if(startTime != null){
				triggerInfo.setDailyStartTime(DateUtils.dateToString(startTime, DateUtils.TIME_PATTERN_YMDHMS));
			}
			if(endTime != null){
				triggerInfo.setDailyEndTime(DateUtils.dateToString(endTime, DateUtils.TIME_PATTERN_YMDHMS));
			}
			
			int repeatCount = dailyTimeIntervalTrigger.getRepeatCount();
			if(repeatCount>0){
				triggerInfo.setRepeatCount(repeatCount);
			}
			
			int repeatInterval =  dailyTimeIntervalTrigger.getRepeatInterval();
			if(repeatInterval > 0){				
				String repeatIntervalUnit = dailyTimeIntervalTrigger.getRepeatIntervalUnit().toString();
				
				triggerInfo.setRepeatInterval(repeatInterval);
				triggerInfo.setRepeatIntervalUnit(repeatIntervalUnit);
				triggerInfo.setRepeatTrigger(true);					
			}else{
				triggerInfo.setRepeatInterval(0);
				triggerInfo.setRepeatIntervalUnit(null);
				triggerInfo.setRepeatTrigger(false);
				triggerInfo.setRepeatCount(0);
			}
			
			triggerInfo.setTriggerName(triggerName);
			triggerInfo.setTriggerGroup(triggerGroup);
			triggerInfo.setTriggerDescription(triggerDescription);
			
			String hour="";
			String minute="";
			String second="";
			
			hour = startTimeOfDay.getHour() < 10 ? "0" + String.valueOf(startTimeOfDay.getHour()) : String.valueOf(startTimeOfDay.getHour());
			minute = startTimeOfDay.getMinute() < 10 ? "0" + String.valueOf(startTimeOfDay.getMinute()) : String.valueOf(startTimeOfDay.getMinute());
			second = startTimeOfDay.getSecond() < 10 ? "0" + String.valueOf(startTimeOfDay.getSecond()) : String.valueOf(startTimeOfDay.getSecond());			
			triggerInfo.setStartTimeOfDay(hour + ":" + minute + ":" + second);			
			
			hour = endTimeOfDay.getHour() < 10 ? "0" + String.valueOf(endTimeOfDay.getHour()) : String.valueOf(endTimeOfDay.getHour());
			minute = endTimeOfDay.getMinute() < 10 ? "0" + String.valueOf(endTimeOfDay.getMinute()) : String.valueOf(endTimeOfDay.getMinute());
			second = endTimeOfDay.getSecond() < 10 ? "0" + String.valueOf(endTimeOfDay.getSecond()) : String.valueOf(endTimeOfDay.getSecond());
			triggerInfo.setEndTimeOfDay(hour+ ":" + minute + ":" + second);
			
			String[] dayOfWeekArr = new String[set.size()];
			Iterator<Integer> dayOfWeekIterator = set.iterator();
			int dayOfWeekArrSize = 0;
			while(dayOfWeekIterator.hasNext()){				
				String weekName = WeekOfNum.getWeekName(dayOfWeekIterator.next());
				dayOfWeekArr[dayOfWeekArrSize] = weekName;
				dayOfWeekArrSize ++;
			}
			
			triggerInfo.setDayOfWeek(dayOfWeekArr);
			triggerInfo.setTriggerType("DAILY_TRIGGER");
		}		
		//cron触发器
		else if((trigger instanceof CronTrigger)){
			CronTrigger cronTrigger = (CronTrigger) trigger;			
			String cronExpression = cronTrigger.getCronExpression();
			
			triggerInfo.setTriggerName(triggerName);
			triggerInfo.setTriggerGroup(triggerGroup);
			triggerInfo.setTriggerDescription(triggerDescription);
			triggerInfo.setCronExpression(cronExpression);						
			triggerInfo.setTriggerType("CRON_TRIGGER");	
			triggerInfo.setJobDataMap(jobDataMap);
		}
				
		return triggerInfo;
	}
	
	/**
	 * 删除任务(与任务关联的触发器也会一起被删除)
	 * @param jobInfo
	 * @return
	 */
	@Override
	public boolean deleteJob(JobInfo jobInfo){
		boolean flag = false;
		
		String jobName = jobInfo.getJobName();
		String jobGroup = jobInfo.getJobGroup();
		
		JobKey jobKey = new JobKey(jobName,jobGroup);
		
		try {
			flag = this.scheduler.deleteJob(jobKey);		   
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 立即执行任务
	 * @param jobInfo
	 * @return
	 */
	@Override
	public boolean jobFire(JobInfo jobInfo){
		boolean flag = false;
		
		String jobName = jobInfo.getJobName();
		String jobGroup = jobInfo.getJobGroup();
		
		JobKey jobKey = new JobKey(jobName,jobGroup);
		try {
			 this.scheduler.triggerJob(jobKey);
			 flag = true;
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 暂停任务（会暂停和任务所有相关的触发器）
	 * @param jobInfo
	 * @return
	 */
	@Override
	public boolean jobPause(JobInfo jobInfo){
		boolean flag = false;
		String jobName = jobInfo.getJobName();
		String jobGroup = jobInfo.getJobGroup();
		
		JobKey jobKey = new JobKey(jobName,jobGroup);
		try {
			 this.scheduler.pauseJob(jobKey);
			 flag = true;
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * 恢复暂停任务
	 * @param jobInfo
	 * @return
	 */
	@Override
	public boolean jobResume(JobInfo jobInfo){
		boolean flag = false;
		String jobName = jobInfo.getJobName();
		String jobGroup = jobInfo.getJobGroup();
		
		JobKey jobKey = new JobKey(jobName,jobGroup);
		try {
			 this.scheduler.resumeJob(jobKey);
			 flag = true;
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return flag;
	}
	
	/**
	 * job是否存在
	 * @param jobName
	 * @param group
	 * @return
	 */
	@Override
	public boolean isJobExsit(String jobName,String group){
		boolean exsit = false;
		 try {
		      exsit = this.scheduler.checkExists(JobKey.jobKey(jobName, group));
		    } catch (SchedulerException e) {
		      e.printStackTrace();
		    }
		 return exsit;
	}
	
	/***********************************触发器***********************************************/
	/**
	 * 添加触发器
	 * @param jobInfo
	 * @param triggerInfo
	 */
	@Override
	public boolean addTrigger(JobInfo jobInfo, TriggerInfo triggerInfo){
		String triggerType = triggerInfo.getTriggerType();
		
		try{
			if(triggerType.equals("SIMPLE_TRIGGER")){			
	            SimpleTrigger simpleTrigger = buildSimpleTrigger(jobInfo,triggerInfo);	            
			    this.scheduler.scheduleJob(simpleTrigger);			
			}else if(triggerType.equals("DAILY_TRIGGER")){
				DailyTimeIntervalTrigger dailyTimeIntervalTrigger = buildDailyTimeIntervalTrigger(jobInfo, triggerInfo);
				this.scheduler.scheduleJob(dailyTimeIntervalTrigger);
			}else if(triggerType.equals("CRON_TRIGGER")){
				CronTrigger cronTrigger = buildCronTrigger(jobInfo, triggerInfo);
				this.scheduler.scheduleJob(cronTrigger);
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 创建简单触发器，SimpleTrigger（指定开始时间/结束时间，间隔多少小时/多少分/多少秒  触发多少次）
	 * SimpleTrigger如果不会再次触发了，会从数据库中自己删除trigger信息
	 * @param jobInfo
	 * @param triggerInfo
	 * @return
	 */
	private static SimpleTrigger buildSimpleTrigger(JobInfo jobInfo,TriggerInfo triggerInfo){
		TriggerBuilder<Trigger> triggerBuilder = getTriggerBuilder(jobInfo,triggerInfo);
		//创建一个SimpleScheduleBuilder(定义了 严格/文字 基于间隔时间表的触发器) 
		SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
				.withMisfireHandlingInstructionIgnoreMisfires();//错过执行时间的就不理
				 						
		
		String startDate = triggerInfo.getSimpleStartDateStr();
		String endDate = triggerInfo.getSimpleEndDateStr();				
		boolean isRepeatTrigger = triggerInfo.getIsRepeatTrigger();		
		JobDataMap jobDataMap = triggerInfo.getJobDataMap();
		
		//如果不指定开始触发器触发的时间，就会立刻触发
		if(startDate != null){
			triggerBuilder.startAt(DateUtils.stringToDate(startDate, DateUtils.TIME_PATTERN_YMDHMS));
		}
		
		if(jobDataMap != null){
			triggerBuilder.usingJobData(jobDataMap);
		}
		
		//是否重复执行的触发器,如果不是,就是指定开始时间执行一次
		if(isRepeatTrigger){					
			String repeatIntervalUnit = triggerInfo.getRepeatIntervalUnit();
			int repeatInterval = new Integer(triggerInfo.getRepeatInterval()).intValue();
						
			if(repeatIntervalUnit.equals("HOUR")){
				triggerBuilder.usingJobData("repeatIntervalUnit", "HOUR");
				simpleScheduleBuilder.withIntervalInHours(repeatInterval);				
			}else if(repeatIntervalUnit.equals("MINUTE")){
				triggerBuilder.usingJobData("repeatIntervalUnit", "MINUTE");
				simpleScheduleBuilder.withIntervalInMinutes(repeatInterval);
			}else if(repeatIntervalUnit.equals("SECOND")){
				triggerBuilder.usingJobData("repeatIntervalUnit", "SECOND");
				simpleScheduleBuilder.withIntervalInSeconds(repeatInterval);
			}
						
			simpleScheduleBuilder.repeatForever();
			
			if(endDate != null){
				triggerBuilder.withSchedule(simpleScheduleBuilder)
				    .endAt(DateUtils.stringToDate(endDate, DateUtils.TIME_PATTERN_YMDHMS));
			}
			//如果触发器有指定repeatCount,则次数执行完后会自动删除触发器
			if(triggerInfo.getRepeatCount() > 0){
				int repeatCount = triggerInfo.getRepeatCount();
				triggerBuilder.withSchedule(simpleScheduleBuilder.withRepeatCount(repeatCount));
			}
			
		}
				
		
		return (SimpleTrigger)triggerBuilder.build();
	}	
	
	/**
	 * 每天触发器（day-of-week 1-7：星期天~星期一， 指定星期一到星期天的 几点、几时、几秒触发）
	 * @param jobInfo
	 * @param triggerInfo
	 * @return
	 */
	private static DailyTimeIntervalTrigger buildDailyTimeIntervalTrigger(JobInfo jobInfo, TriggerInfo triggerInfo){
		TriggerBuilder<Trigger> triggerBuilder = getTriggerBuilder(jobInfo,triggerInfo);
		
		JobDataMap jobDataMap = triggerInfo.getJobDataMap();
		if(jobDataMap != null){
			triggerBuilder.usingJobData(jobDataMap);
		}
		
		DailyTimeIntervalScheduleBuilder dailyTimeIntervalScheduleBuilder = DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule();
		
		//每天的开始时间、结束时间		
		String startTimeOfDay = triggerInfo.getStartTimeOfDay();
		String endTimeOfDay = triggerInfo.getEndTimeOfDay();
		
		String[] startTimeOfDayArr = startTimeOfDay.split(":");
		int starHour = Integer.parseInt(startTimeOfDayArr[0]);
		int starMin = Integer.parseInt(startTimeOfDayArr[1]);
		int starSec = Integer.parseInt(startTimeOfDayArr[2]);
		
		String[] endTimeOfDayArr = endTimeOfDay.split(":");
		int endHour = Integer.parseInt(endTimeOfDayArr[0]);
		int endMin = Integer.parseInt(endTimeOfDayArr[1]);
		int endSec = Integer.parseInt(endTimeOfDayArr[2]);	
		
		//触发器有效的时间段
		/*String dailyStartTime = triggerInfo.getDailyStartTime();
		String dailyEndTime = triggerInfo.getDailyEndTime();
		Date startDate = null;
		Date endDate = null;
		if(dailyStartTime != null && dailyStartTime != ""){
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH), 
					calendar.get(Calendar.DAY_OF_MONTH), 
					starHour, starMin, starSec);
			
			startDate = DateUtils.dateFormat(calendar.getTime(), DateUtils.TIME_PATTERN_YMDHMS);
		}
		if(dailyEndTime != null && dailyEndTime != ""){
			endDate = DateUtils.stringToDate(dailyEndTime,DateUtils.TIME_PATTERN_YMDHMS);
		}*/									
	
		//设置在哪天执行
		//dayOfWeek取值：everyDay 或者 SUNDAY、MONDAY、TUESDAY、WEDNESDAY、THURSDAY...（1-7 代表星期天到星期六）
		String dayOfWeek = triggerInfo.getDayOfWeek()[0];
		if(dayOfWeek.equals("everyDay")){
			//每天都重复触发
			dailyTimeIntervalScheduleBuilder = dailyTimeIntervalScheduleBuilder.onEveryDay();
		}else{
			//指定的哪一天触发
			Set<Integer> dayOfWeekSet = new HashSet<Integer>();
			String[] dayOfWeekArr = dayOfWeek.split(",");			
			for(String i : dayOfWeekArr){ 
				dayOfWeekSet.add(WeekOfNum.getWeekOfNum(i));
			}			
			dailyTimeIntervalScheduleBuilder = dailyTimeIntervalScheduleBuilder.onDaysOfTheWeek(dayOfWeekSet);
        }
				
		//设置是否重复执行，重复执行的次数和间隔
		boolean isRepeatTrigger = triggerInfo.getIsRepeatTrigger();
		//间隔时间大小和间隔的单位
		int repeatInterval = triggerInfo.getRepeatInterval();
		String repeatIntervalUnit = triggerInfo.getRepeatIntervalUnit();
		
		if(isRepeatTrigger){																					
			//如果触发器有指定repeatCount,则次数执行完后会自动删除触发器,改用endingDailyAfterCount
			if(triggerInfo.getRepeatCount() > 1){
				int repeatCount = triggerInfo.getRepeatCount();
				
				dailyTimeIntervalScheduleBuilder = dailyTimeIntervalScheduleBuilder
						.startingDailyAt(new TimeOfDay(starHour,starMin,starSec))
						.withInterval(repeatInterval, DateBuilder.IntervalUnit.valueOf(repeatIntervalUnit))
						.endingDailyAfterCount(repeatCount);	
			}else{				
				dailyTimeIntervalScheduleBuilder = dailyTimeIntervalScheduleBuilder
						.startingDailyAt(new TimeOfDay(starHour,starMin,starSec))
						.withInterval(repeatInterval, DateBuilder.IntervalUnit.valueOf(repeatIntervalUnit));	
			}
		}else{
			//如果没有重复，则设置隔24小时执行一次
			dailyTimeIntervalScheduleBuilder = dailyTimeIntervalScheduleBuilder
					.startingDailyAt(new TimeOfDay(starHour,starMin,starSec))
			        .endingDailyAt(new TimeOfDay(endHour,endMin,endSec))
			        .withInterval(repeatInterval, DateBuilder.IntervalUnit.valueOf(repeatIntervalUnit));
		}				
						
		triggerBuilder.withSchedule(dailyTimeIntervalScheduleBuilder);
		
		return (DailyTimeIntervalTrigger) triggerBuilder.build();
	}
	
	//创建每天间隔触发器另一种方法
	@SuppressWarnings("unused")
	private static DailyTimeIntervalTrigger buildDailyTimeIntervalTrigger2(JobInfo jobInfo, TriggerInfo triggerInfo){				
		String triggerName =  triggerInfo.getTriggerName();
		String triggerGroup = triggerInfo.getTriggerGroup();
		String triggerDescription = triggerInfo.getTriggerDescription();
		JobDataMap jobDataMap = triggerInfo.getJobDataMap();
		
		String jobName =  jobInfo.getJobName();
		String jobGroup = jobInfo.getJobGroup();
		
		//每天的开始时间、结束时间		
		String startTimeOfDayStr = triggerInfo.getStartTimeOfDay();
		String endTimeOfDayStr = triggerInfo.getEndTimeOfDay();
		
		String[] startTimeOfDayArr = startTimeOfDayStr.split(":");
		int starHour = Integer.parseInt(startTimeOfDayArr[0]);
		int starMin = Integer.parseInt(startTimeOfDayArr[1]);
		int starSec = Integer.parseInt(startTimeOfDayArr[2]);
		
		String[] endTimeOfDayArr = endTimeOfDayStr.split(":");
		int endHour = Integer.parseInt(endTimeOfDayArr[0]);
		int endMin = Integer.parseInt(endTimeOfDayArr[1]);
		int endSec = Integer.parseInt(endTimeOfDayArr[2]);
		
		//有效时日期
		String dailyStartTime = triggerInfo.getDailyStartTime();
		String dailyEndTime = triggerInfo.getDailyEndTime();
		Date startDate = null;
		Date endDate = null;
		if(dailyStartTime != null && dailyStartTime != ""){
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), 
					calendar.get(Calendar.MONTH), 
					calendar.get(Calendar.DAY_OF_MONTH), 
					starHour, starMin, starSec);
			
			startDate = DateUtils.dateFormat(calendar.getTime(), DateUtils.TIME_PATTERN_YMDHMS);	
		}
		if(dailyEndTime != null && dailyEndTime != ""){
			endDate = DateUtils.stringToDate(dailyEndTime,DateUtils.TIME_PATTERN_YMDHMS);
		}	
						
		//重复间隔，重复单位
		int repeatInterval = triggerInfo.getRepeatInterval();
		String intervalUnit = triggerInfo.getRepeatIntervalUnit();
		
		//设置week
		String dayOfWeek = triggerInfo.getDayOfWeek()[0];
		Set<Integer> dayOfWeekSet = new HashSet<Integer>();
		if(dayOfWeek.equals("everyDay")){
			//每天都重复触发
		    dayOfWeek = "1,2,3,4,5,6,7";
		}
				
		String[] dayOfWeekArr = dayOfWeek.split(",");			
		for(String i : dayOfWeekArr){ 
			dayOfWeekSet.add(WeekOfNum.getWeekOfNum(i));
		}        
		
		//创建触发器
		DailyTimeIntervalTriggerImpl dailyTimeIntervalTriggerImpl = 
				new DailyTimeIntervalTriggerImpl(triggerName, triggerGroup, jobName, jobGroup, 
						startDate, endDate, 
						new TimeOfDay(starHour,starMin,starSec), new TimeOfDay(endHour,endMin,endSec), 
						DateBuilder.IntervalUnit.valueOf(intervalUnit), repeatInterval);
		
		dailyTimeIntervalTriggerImpl.setDaysOfWeek(dayOfWeekSet);
		dailyTimeIntervalTriggerImpl.setJobDataMap(jobDataMap);
		dailyTimeIntervalTriggerImpl.setDescription(triggerDescription);
		
		return dailyTimeIntervalTriggerImpl;
	}
	
	/**
	 * 创建cron触发器
	 * @param jobInfo
	 * @param triggerInfo
	 * @return
	 */
	private static CronTrigger buildCronTrigger(JobInfo jobInfo, TriggerInfo triggerInfo){
		TriggerBuilder<Trigger> triggerBuilder = getTriggerBuilder(jobInfo,triggerInfo);			
		
		String cronExpression = triggerInfo.getCronExpression();
	    JobDataMap jobDataMap = triggerInfo.getJobDataMap();		
		
		boolean b = CronExpression.isValidExpression(cronExpression);
		if(!b){
			logger.info("CRON表达式 = " + cronExpression + "不正确，过不了校验");
			return null;
		}
		
		if(jobDataMap != null){
			triggerBuilder.usingJobData(jobDataMap);
		}
		
		//withMisfireHandlingInstructionDoNothing:任务错过，不做什么。配合properties参数org.quartz.jobStore.misfireThreshold，
		//如果任务错过的时间长度大于参数配置，则任务错过后就不会执行。如果错过的时间长度在参数配置内，则会执行错过的任务
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing());
		return (CronTrigger)triggerBuilder.build();		
	}
	
	/**
	 * 创建和job相关的TriggerBuilder,用于创建一个Trigger实例，设定Trigger的自定属性，例如名字，组，关联触发的任务
	 * @param jobInfo
	 * @param triggerInfo
	 * @return
	 */
	private static TriggerBuilder<Trigger>  getTriggerBuilder(JobInfo jobInfo, TriggerInfo triggerInfo){
		String triggerName =  triggerInfo.getTriggerName();
		String triggerGroup = triggerInfo.getTriggerGroup();
		String triggerDescription = triggerInfo.getTriggerDescription();
		
		String jobName =  jobInfo.getJobName();
		String jobGroup = jobInfo.getJobGroup();						
		
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger()
			.withIdentity(triggerName, triggerGroup)
			.forJob(jobName, jobGroup)
			.withDescription(triggerDescription);
		
		return triggerBuilder;
	}
	
	/**
	 * 编辑触发器
	 * @param jobInfo
	 * @param triggerInfo
	 * @return
	 */
	@Override
	public boolean editTrigger(JobInfo jobInfo, TriggerInfo triggerInfo){
		String triggerType = triggerInfo.getTriggerType();
				
		TriggerKey tk = TriggerKey.triggerKey(triggerInfo.getTriggerName(), triggerInfo.getTriggerGroup());		
		    
		if(triggerType.equals("SIMPLE_TRIGGER")){
			SimpleTrigger simpleTrigger = buildSimpleTrigger(jobInfo, triggerInfo);
			try {
				this.scheduler.rescheduleJob(tk,simpleTrigger);
				
				return true;
			} catch (SchedulerException e) {
				e.printStackTrace();			
			}
		}else if (triggerType.equals("DAILY_TRIGGER")){
			DailyTimeIntervalTrigger dailyTimeIntervalTrigger= buildDailyTimeIntervalTrigger(jobInfo, triggerInfo);
			try {
				this.scheduler.rescheduleJob(tk, dailyTimeIntervalTrigger);
				
				return true;
			} catch (SchedulerException e) {
				e.printStackTrace();				
			}
		}else if (triggerType.equals("CRON_TRIGGER")){
			CronTrigger cronTrigger = buildCronTrigger(jobInfo, triggerInfo);
			try {
				this.scheduler.rescheduleJob(tk,cronTrigger);
				
				return true;
			} catch (SchedulerException e) {
				e.printStackTrace();				
			}
		}		    					
		
		return false;
	}
	/**
	 * 删除触发器
	 * @param triggerInfo
	 * @return
	 */
	@Override
	public boolean deleteTrigger(TriggerInfo triggerInfo){
		boolean b = false;
		
		String triggerName = triggerInfo.getTriggerName();
		String triggerGroup =  triggerInfo.getTriggerGroup();
				
		if((triggerName == "")||(triggerGroup == ""))
			return false;
		
		try {
			b = this.scheduler.unscheduleJob(new TriggerKey(triggerName,triggerGroup));
		} catch (SchedulerException e) {			
			e.printStackTrace();
			return false;
		}
		
		return b;
	}
	
	/**
	 * 获取触发器信息
	 * @param triggerName
	 * @param triggerGroup
	 * @return
	 */
	@Override
	public TriggerInfo getTrigger(String triggerName, String triggerGroup){
		TriggerInfo triggerInfo = new TriggerInfo();
		Trigger trigger= null;
		try {
			trigger = this.scheduler.getTrigger(new TriggerKey(triggerName, triggerGroup));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		if(trigger != null){
			triggerInfo = getTriggerInfo(trigger);
		}
		
		return triggerInfo;
	}
	
	/**
	 * 
	 * @param triggerInfo
	 * @return
	 */
	@Override
	public boolean isTriggerExist(String triggerName, String triggerGroup){
		boolean exist = false;
		
		try {
			exist = this.scheduler.checkExists(TriggerKey.triggerKey(triggerName,triggerGroup));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		
		return exist;
	}
	
	
	@Override
	public List<? extends Trigger> getTriggersOfJob(String jobName,String jobGroup){
		JobKey jobKey = new JobKey(jobName, jobGroup);
		List<? extends Trigger> list = null;
		try {
			list =  this.scheduler.getTriggersOfJob(jobKey);
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		
		return list;
	}
	
	@Override
	public JobDataMap getJobDataMap(String jobName, String jobGroup){
		JobKey jobKey = new JobKey(jobName,jobGroup);
		JobDetail jobDetail = null;
		try {
			 jobDetail = this.scheduler.getJobDetail(jobKey);
		} catch (SchedulerException e) {			
			e.printStackTrace();
		}
		JobDataMap jobDataMap = jobDetail.getJobDataMap();
		
		return jobDataMap;
	}
	/*************************************************************************/
	public Scheduler getScheduler() {
		return scheduler;
	}

	public void setScheduler(Scheduler scheduler) {
		this.scheduler = scheduler;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}
		
}

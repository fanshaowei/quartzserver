package com.papi.quartz.listener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import com.papi.quartz.bean.QutzJobFiredDetails;
import com.papi.quartz.enums.QuartzJobs;
import com.papi.quartz.service.QutzJobFiredDetailsService;
import com.papi.quartz.utils.DateUtils;

/**
 * job监听器，在触发器触发执行job之前和之后调用的类，并记录相关的日志到表qrtz_job_fired_details
 * @author fanshaowei
 *
 */
public class CustomJobListener implements JobListener {
	static Logger logger = Logger.getLogger(CustomJobListener.class.getName());	
	
	public ApplicationContext springContext = null;
	public QutzJobFiredDetailsService qutzJobFiredDetailsService ; 
	
	/**
	 * 获取应用上下文对象，用来获取注入的 service
	 * @param context
	 * @return
	 */
	public static final ApplicationContext getApplicationContext(JobExecutionContext context){
		ApplicationContext applicationContex = null;
		
		try {
			 applicationContex = 
					(ApplicationContext) context.getScheduler().getContext().get("applicationContextSchedulerContextKey");
			 
			 if (applicationContex == null)
				 applicationContex = ContextLoader.getCurrentWebApplicationContext();
		} catch (SchedulerException e) {		
			e.printStackTrace();
		}
		
		return applicationContex;
	}
	
	@SuppressWarnings("unused")
	private String name;
	
	public  CustomJobListener() {	
	}
	
	public CustomJobListener (String name){
		this.name = name;
	}

	//@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 任务执行前监听
	 */
	@Override
	public void jobToBeExecuted(JobExecutionContext context) {					
		this.springContext= getApplicationContext(context);		
		qutzJobFiredDetailsService = (QutzJobFiredDetailsService)this.springContext.getBean("qutzJobFiredDetailsService");
		
		JobDetail jobDetail = context.getJobDetail();
		String jobName = jobDetail.getKey().getName();
		String jobGroup = jobDetail.getKey().getGroup();
		String jobClassName = jobDetail.getJobClass().getName();
		String jobType = QuartzJobs.getJobType(jobClassName);
		String jobStatus = "执行中";
		
		Trigger trigger = context.getTrigger();
		String triggerName = trigger.getKey().getName();
		String triggerGroup =  trigger.getKey().getGroup();
		Date fireDate = trigger.getPreviousFireTime();
		fireDate = DateUtils.dateFormat(fireDate, DateUtils.TIME_PATTERN_YMDHMS);
		String triggerType = "";
		Date nextFireDate = null;
		
		if ((trigger instanceof SimpleTrigger)) {
	        SimpleTrigger simpleTrigger = (SimpleTrigger)trigger;
	        triggerType = "SIMPLE_TRIGGER";
	        nextFireDate = simpleTrigger.getNextFireTime();
	    }else if(trigger instanceof DailyTimeIntervalTrigger){
	    	DailyTimeIntervalTrigger dailyTimeIntervalTrigger = (DailyTimeIntervalTrigger)trigger;
	    	triggerType = "DAILY_TRIGGER";
	    	nextFireDate = dailyTimeIntervalTrigger.getNextFireTime();
	    } else if ((trigger instanceof CronTrigger)) {
	        CronTrigger cronTrigger = (CronTrigger)trigger;
	        triggerType = "CRON_TRIGGER";
	        nextFireDate = cronTrigger.getNextFireTime();
	    } else {
	      logger.error("trigger type error,not Simple,not Cron,no Daily");
	    }
		
		logger.info("[jobName:" + jobName + ",jobGroup:" + jobGroup +
				",triggerName:" + triggerName + ",triggerGroup" + triggerGroup
				+ "] 将被执行。");
		
		Date startDate = new Date();
		startDate = DateUtils.dateFormat(startDate, DateUtils.TIME_PATTERN_YMDHMS);
		
		QutzJobFiredDetails qutzJobFiredDetails = new QutzJobFiredDetails();
		qutzJobFiredDetails.setJobName(jobName);
		qutzJobFiredDetails.setJobGroup(jobGroup);
		qutzJobFiredDetails.setTriggerName(triggerName);
		qutzJobFiredDetails.setTriggerGroup(triggerGroup);
		qutzJobFiredDetails.setJobType(jobType);
		qutzJobFiredDetails.setJobStatus(jobStatus);
		qutzJobFiredDetails.setTriggerType(triggerType);
		qutzJobFiredDetails.setStartDate(startDate);
		qutzJobFiredDetails.setFireDate(fireDate);
		qutzJobFiredDetails.setNextFireDate(nextFireDate);
		
		try {
			qutzJobFiredDetailsService.saveQutzJobFiredDetails(qutzJobFiredDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		logger.info("[jobName:" + context.getJobDetail().getKey().getName()
				+ ",jobGroup:" + context.getJobDetail().getKey().getGroup()
				+ "] 已经被否决而且没有执行。");

	}

	/**
	 * 任务执行完后监听
	 */
	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		this.springContext= getApplicationContext(context);		
		qutzJobFiredDetailsService = (QutzJobFiredDetailsService)this.springContext.getBean("qutzJobFiredDetailsService");
		
		JobDetail jobDetail = context.getJobDetail();
		String jobName = jobDetail.getKey().getName();
		String jobGroup = jobDetail.getKey().getGroup();		
		String jobStatus = "执行完毕";
		String firedResult = jobDetail.getJobDataMap().getString("jobResult");
		
		Trigger trigger = context.getTrigger();
		String triggerName = trigger.getKey().getName();
		String triggerGroup =  trigger.getKey().getGroup();
		Date fireDate = trigger.getPreviousFireTime();
		fireDate = DateUtils.dateFormat(fireDate, DateUtils.TIME_PATTERN_YMDHMS);
		
		Date endDate = new Date();
		endDate = DateUtils.dateFormat(endDate, DateUtils.TIME_PATTERN_YMDHMS);
		
		logger.info("[jobName:" + context.getJobDetail().getKey().getName()
				+ ",jobGroup:" + context.getJobDetail().getKey().getGroup()
				+ "] 已经被执行。");
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("jobName",jobName);
		paramMap.put("jobGroup",jobGroup);
		paramMap.put("triggerName",triggerName);
		paramMap.put("triggerGroup",triggerGroup);
		paramMap.put("fireDate",fireDate);
		paramMap.put("jobStatus","执行中");				
		
		try {
			List<QutzJobFiredDetails> qutzJobFiredDetailsList = qutzJobFiredDetailsService.findQutzJobFiredDetails(paramMap);
			if(qutzJobFiredDetailsList != null && qutzJobFiredDetailsList.size() > 0 ){
				ListIterator<QutzJobFiredDetails> listIterator = qutzJobFiredDetailsList.listIterator();
				QutzJobFiredDetails qutzJobFiredDetails = new QutzJobFiredDetails();
				while(listIterator.hasNext()){
					int id = listIterator.next().getId();										
									
					qutzJobFiredDetails.setId(id);
					qutzJobFiredDetails.setEndDate(endDate);
					qutzJobFiredDetails.setJobStatus(jobStatus);
					qutzJobFiredDetails.setFiredResult(firedResult);
					
					qutzJobFiredDetailsService.updateQutzJobFiredDetails(qutzJobFiredDetails);
				}
			}
		} catch (Exception e) {			
			e.printStackTrace();
		}
	}
}

package com.papi.quartz.quartzjobs;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.TriggerKey;

public class HelloJob extends BasicJob{

	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		
		JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
		String jobGroup = jobKey.getGroup();
		String jobName = jobKey.getName();		
		TriggerKey triggerKey = jobExecutionContext.getTrigger().getKey();
		
		jobDataMap.put("jobResult", "执行成功");
	}
    
}

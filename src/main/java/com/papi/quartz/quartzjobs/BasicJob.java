package com.papi.quartz.quartzjobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

@PersistJobDataAfterExecution//告诉Quartz在成功执行了job类的execute方法后（没有发生任何异常），更新JobDetail中JobDataMap的数据
@DisallowConcurrentExecution//不要并发执行同一个jobDetail,
public abstract class BasicJob implements Job {
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		String jobName = jobExecutionContext.getJobDetail().getKey().getName();
		String jobGroup = jobExecutionContext.getJobDetail().getKey().getGroup();
		System.out.println("执行任务" + "任务组："+ jobGroup + ",任务名："+ jobName );		
	}
}

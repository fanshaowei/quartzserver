package com.papi.quartz.listener;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

public class CustomJobListener implements JobListener {
	static Logger logger = Logger.getLogger(CustomJobListener.class.getName());

	@SuppressWarnings("unused")
	private String name;

	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void jobToBeExecuted(JobExecutionContext context) {
		logger.info("[jobName:" + context.getJobDetail().getKey().getName()
				+ ",jobGroup:" + context.getJobDetail().getKey().getGroup()
				+ "] 将被执行。");
	}

	@Override
	public void jobExecutionVetoed(JobExecutionContext context) {
		logger.info("[jobName:" + context.getJobDetail().getKey().getName()
				+ ",jobGroup:" + context.getJobDetail().getKey().getGroup()
				+ "] 已经被否决而且没有执行。");

	}

	@Override
	public void jobWasExecuted(JobExecutionContext context,
			JobExecutionException jobException) {
		logger.info("[jobName:" + context.getJobDetail().getKey().getName()
				+ ",jobGroup:" + context.getJobDetail().getKey().getGroup()
				+ "] 已经被执行。");
	}
}

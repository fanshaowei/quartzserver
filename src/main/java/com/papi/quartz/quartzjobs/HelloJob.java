package com.papi.quartz.quartzjobs;

import java.util.Date;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class HelloJob extends BasicJob
{
	  public void execute(JobExecutionContext context)
	    throws JobExecutionException
	  {
	    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
	    System.out.println("Hello World ! " + new Date());

	    if (jobDataMap.getString("result") != null) {
	      if (jobDataMap.getString("result").equals("WOW"))
	      {
	        context.getJobDetail().getJobDataMap()
	          .put("JOB_RESULT", "成功");
	      }
	      else {
	        context.getJobDetail().getJobDataMap()
	          .put("JOB_RESULT", "失败");
	      }
	    }
	    else
	      context.getJobDetail().getJobDataMap()
	        .put("JOB_RESULT", "失败");
	  }
}

package com.papi.quartz.utils;

import java.util.List;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerKey;

public class QuartzUtils {
   
	/**
	 * 获取作务的触发器的状态
	 * BLOCKED、COMPLETE、ERROR、NONE、NORMAL、PAUSED
	 * @param triggerList
	 * @param jobKey
	 * @param scheduler
	 * @return
	 */
	public static String getJobStatus(List<? extends Trigger> triggerList,JobKey jobKey,Scheduler scheduler){
		String state = "正常";
		
		if(triggerList.size() == 0)
			return "无触发器";
		
		for(Trigger trigger: triggerList){
			 TriggerKey triggerKey = trigger.getKey();
			 try {
				TriggerState triggerState = scheduler.getTriggerState(triggerKey);
				if(triggerState.equals(TriggerState.PAUSED)){
					state = "暂停";
					
					if(state.endsWith("暂停"))
						scheduler.pauseJob(jobKey);
					
					return state;
				}
			} catch (SchedulerException e) {				
				e.printStackTrace();
			}
		}
		
		return state;
	}
	
	/**
	 * 时间间隔
	 * 
	 * @param l
	 * @return
	 */
	public static String bulidRepeatInteval(long l)
	  {
	    String lastString = "";
	    long s = l / 1000L;
	    if ((0L < s) && (s <= 60L)) {
	      lastString = "0:0:0:" + new Long(s).toString();
	    } else if ((60L < s) && (s <= 3600L)) {
	      long m1 = s / 60L;
	      long m2 = s % 60L;
	      String s1 = new Long(m1).toString();
	      String s2 = new Long(m2).toString();
	      lastString = "0:0:" + s1 + ":" + s2;
	    } else if ((3600L < s) && (s <= 86400L)) {
	      long hour = s / 3600L;
	      long min = s % 3600L / 60L;
	      long sec = s % 3600L % 60L;
	      String s1 = new Long(hour).toString();
	      String s2 = new Long(min).toString();
	      String s3 = new Long(sec).toString();
	      lastString = "0:" + s1 + ":" + s2 + ":" + s3;
	    } else {
	      long day = s / 86400L;
	      long last = s % 86400L;
	      long hour = last / 3600L;
	      long min = s % 3600L / 60L;
	      long sec = s % 3600L % 60L;
	      String s1 = new Long(hour).toString();
	      String s2 = new Long(min).toString();
	      String s3 = new Long(sec).toString();
	      String s4 = new Long(day).toString();
	      lastString = s4 + ":" + s1 + ":" + s2 + ":" + s3;
	    }
	    return lastString;
	  }

}

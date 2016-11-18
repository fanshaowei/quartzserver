package com.papi.quartz.service;

import javax.servlet.ServletContext;

import com.papi.quartz.bean.AppRequestJobInfo;

public interface AppJobService {
	public String addSceneRelateJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo);
	
	public String addDailySceneControlJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo);
	
	public String addSimpleSceneControlJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo);
	
	public String updateSceneRelateJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo);
	
	public String updateDailySceneControlJob(ServletContext servletContext, AppRequestJobInfo appRequestJobInfo);
	
	public void importRedisData(ServletContext servletContext,String requestStr);
}

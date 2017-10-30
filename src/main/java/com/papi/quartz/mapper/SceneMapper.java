package com.papi.quartz.mapper;

import java.util.Map;

import com.papi.quartz.bean.SceneBean;


public interface SceneMapper{
	public SceneBean listByParams(Map<String,Object> paramMap);
	
	public SceneBean selectById(int idScene);
}

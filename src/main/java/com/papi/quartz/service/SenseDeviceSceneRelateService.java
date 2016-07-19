package com.papi.quartz.service;

import java.util.List;
import java.util.Map;

import com.papi.quartz.bean.SenseDeviceSceneRelate;

public interface SenseDeviceSceneRelateService {
	public List<SenseDeviceSceneRelate> find(Map<String,Object> map) throws Exception;
	
    public Integer add(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception;
    
    public Integer deleteById(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception;
    
    public Integer update(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception;
    
}

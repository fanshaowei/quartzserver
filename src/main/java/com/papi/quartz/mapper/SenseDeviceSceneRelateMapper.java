package com.papi.quartz.mapper;

import java.util.List;
import java.util.Map;

import com.papi.quartz.bean.SenseDeviceSceneRelate;

public interface SenseDeviceSceneRelateMapper {
	public List<SenseDeviceSceneRelate> listByBean(Map<String,Object> map) throws Exception;
	
    public Integer insert(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception;
    
    public Integer delete(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception;
    
    public Integer update(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception;
}

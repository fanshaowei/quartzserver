package com.papi.quartz.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.papi.quartz.bean.SenseDeviceSceneRelate;
import com.papi.quartz.mapper.SenseDeviceSceneRelateMapper;
import com.papi.quartz.service.SenseDeviceSceneRelateService;

/**
 * 
 * @author fanshaowei
 *
 *用来对情景关联 信息的保存，更改，删除
 */
@Service("senseDeviceSceneRelateService")
public class SenseDeviceSceneRelateServiceImpl implements SenseDeviceSceneRelateService{
    @Resource
    SenseDeviceSceneRelateMapper senseDeviceSceneRelateMapper;
	        
	@Override
	public List<SenseDeviceSceneRelate> find(Map<String,Object> map) throws Exception {
		
		return senseDeviceSceneRelateMapper.listByBean(map);
	}

	@Override
	public Integer add(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception {
				
		return senseDeviceSceneRelateMapper.insert(senseDeviceSceneRelate);
	}
	
	@Override
	public Integer update(SenseDeviceSceneRelate senseDeviceSceneRelate) throws Exception {
		
		return senseDeviceSceneRelateMapper.update(senseDeviceSceneRelate);
	}

	@Override
	public Integer deleteSenseDeviceSceneRelate(SenseDeviceSceneRelate senseDeviceSceneRelate)
			throws Exception {
		
		return senseDeviceSceneRelateMapper.delete(senseDeviceSceneRelate);
	}
	
}

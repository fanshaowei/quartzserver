package com.papi.quartz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.papi.quartz.bean.SceneBean;
import com.papi.quartz.mapper.SceneMapper;
import com.papi.quartz.service.SceneService;

@Service("sceneService")
public class SceneServiceImpl implements SceneService{
	@Resource
	SceneMapper sceneMapper;
	
	@Override
	public SceneBean selectById(int idScene) {
		return sceneMapper.selectById(idScene);
	}
}

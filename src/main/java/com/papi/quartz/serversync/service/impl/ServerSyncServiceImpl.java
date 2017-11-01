package com.papi.quartz.serversync.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.papi.quartz.bean.SenseDeviceSceneRelate;
import com.papi.quartz.serversync.service.ServerSyncService;
import com.papi.quartz.service.AppJobService;
import com.papi.quartz.service.QuartzService;
import com.papi.quartz.service.SenseDeviceSceneRelateService;

/**
 * 该类用于在安居家园app删除网关或家庭时，同步删除该网关或家庭下的智能任务(删除的信息：任务调试器中的任务，redis中的存储任务，智能设备情景关联信息)
 * @author fanshaowei
 *
 */
@Service("serverSyncService")
public class ServerSyncServiceImpl implements ServerSyncService{
	private Logger logger = LoggerFactory.getLogger(ServerSyncServiceImpl.class);
	
	@Resource
	SenseDeviceSceneRelateService senseDeviceSceneRelateService;
	@Resource
    private QuartzService quartzService;    
    @Resource
    private AppJobService appJobService;
	
	@Override
	public void deleSceneRelateJobByGateway(String idFamily, String idGateway) {
		Map<String,Object> paramMap = new HashMap<String,Object>();
		paramMap.put("idFamily", idFamily);
		paramMap.put("idGateway", idGateway);
				 
		/*********************************************/
		//删除redis和调度任务器中的任务信息
		appJobService.deleteRedisAndScheduleJob(idFamily, idGateway);
		/*********************************************/ 
				
		List<SenseDeviceSceneRelate> resultList = null;
		Iterator<SenseDeviceSceneRelate> itor = null;
		try {
			resultList = this.senseDeviceSceneRelateService.find(paramMap);//获取智能设备情景关联中的关联记录
			if(resultList != null){
			    itor = resultList.iterator();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}		   
		
		SenseDeviceSceneRelate senseDeviceSceneRelate = null;
		int delCnt = 0;
		while(itor.hasNext()){
			senseDeviceSceneRelate = itor.next();						    				
			/*********************************************/
			//删除智能设备情景记录
			try {
				delCnt = this.senseDeviceSceneRelateService.deleteSenseDeviceSceneRelate(senseDeviceSceneRelate);
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("----删除情景关联记录条数:" + delCnt);
			/*********************************************/						
		}
		
	}
}

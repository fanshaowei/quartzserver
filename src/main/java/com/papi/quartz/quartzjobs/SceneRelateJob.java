package com.papi.quartz.quartzjobs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import com.papi.quartz.bean.SenseDeviceSceneRelate;
import com.papi.quartz.service.SenseDeviceSceneRelateService;

public class SceneRelateJob extends BasicJob{
    private Logger logger = Logger.getLogger(SceneRelateJob.class.getName());
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		logger.info("-----------------------begin SceneRelateJob---------------------------");
		ApplicationContext applicationContex = null;
		try {
			 applicationContex = 
					(ApplicationContext) jobExecutionContext.getScheduler().getContext().get("applicationContextSchedulerContextKey");
		} catch (SchedulerException e) {		
			e.printStackTrace();
		}		
				
		
		JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
		String sourceScene = jobDataMap.getString("sourceScene");
		String idFamily = jobDataMap.getString("idFamily");
		String idDevice = JSONObject.fromObject(sourceScene).getString("idDevice");
		
		String operation = jobExecutionContext.getTrigger().getJobDataMap().getString("operation");
		
		SenseDeviceSceneRelateService senseDeviceSceneRelateService = 
				(SenseDeviceSceneRelateService) applicationContex.getBean("senseDeviceSceneRelateService");
		
		Map<String,Object> mapParam = new HashMap<String,Object>();
		mapParam.put("idFamily", idFamily);
		mapParam.put("idDevice", idDevice);
		
		try {
			List<SenseDeviceSceneRelate> senseDeviceSceneRelateList = senseDeviceSceneRelateService.find(mapParam);
			System.out.println(senseDeviceSceneRelateList.toString());
			if(senseDeviceSceneRelateList != null && senseDeviceSceneRelateList.size()>0){
				Iterator<SenseDeviceSceneRelate> iterator = senseDeviceSceneRelateList.listIterator();
				while(iterator.hasNext()){
					SenseDeviceSceneRelate senseDeviceSceneRelate = iterator.next();
					String isValid = senseDeviceSceneRelate.getIsValid();
					if(operation.equals("open")){
						if(isValid.equals("0")){
							senseDeviceSceneRelate.setIsValid("1");
							int num = senseDeviceSceneRelateService.update(senseDeviceSceneRelate);
							System.out.println("更新情景关联"+"open" + num);
						}
					}else if(operation.equals("close")){
						if(isValid.equals("1")){
							senseDeviceSceneRelate.setIsValid("0");
							int num = senseDeviceSceneRelateService.update(senseDeviceSceneRelate);
							System.out.println("更新情景关联"+"close" + num);
						}
					}
				}//end while
			}
			
			jobDataMap.put("jobResult", "执行成功");
			logger.info("执行成功。。。");
		} catch (Exception e) {
			jobDataMap.put("jobResult", "执行失败");
			logger.info("执行失败。。。");
			e.printStackTrace();
		}   		
	
		logger.info("-----------------------end SceneRelateJob-----------------------------");
	}
}


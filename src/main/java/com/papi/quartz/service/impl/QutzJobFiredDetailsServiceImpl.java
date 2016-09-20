package com.papi.quartz.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.papi.quartz.bean.QutzJobFiredDetails;
import com.papi.quartz.mapper.QutzJobFiredDetailsMapper;
import com.papi.quartz.service.QutzJobFiredDetailsService;

@Service("qutzJobFiredDetailsService")
public class QutzJobFiredDetailsServiceImpl implements QutzJobFiredDetailsService{
	
	@Resource
	QutzJobFiredDetailsMapper qutzJobFiredDetailsMapper;

	//查找记录
	@Override
	public List<QutzJobFiredDetails> findQutzJobFiredDetails(
			Map<String, Object> paramMap) throws Exception {
		
		return qutzJobFiredDetailsMapper.find(paramMap);
	}
		
	//保存记录
	@Override
	public int saveQutzJobFiredDetails(QutzJobFiredDetails qutzJobFiredDetails) throws Exception {
		
		return qutzJobFiredDetailsMapper.insert(qutzJobFiredDetails);
	}

	//删除记录
	@Override
	public int updateQutzJobFiredDetails(QutzJobFiredDetails qutzJobFiredDetails)
			throws Exception {
				
		return qutzJobFiredDetailsMapper.update(qutzJobFiredDetails);
	}

	@Override
	public int deleteQutzJobFiredDetails(Map<String, Object> paramMap)
			throws Exception {		
		
		return qutzJobFiredDetailsMapper.delete(paramMap);
	}
			
	
}

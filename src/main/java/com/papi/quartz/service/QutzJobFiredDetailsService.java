package com.papi.quartz.service;

import java.util.List;
import java.util.Map;

import com.papi.quartz.bean.QutzJobFiredDetails;

public interface QutzJobFiredDetailsService {
    
	public List<QutzJobFiredDetails> findQutzJobFiredDetails(Map<String, Object> paramMap) throws Exception;
	
	public int saveQutzJobFiredDetails(QutzJobFiredDetails qutzJobFiredDetails) throws Exception;
	
	public int updateQutzJobFiredDetails(QutzJobFiredDetails qutzJobFiredDetails) throws Exception;
	
	public int deleteQutzJobFiredDetails(Map<String, Object> paramMap) throws Exception;
    
}
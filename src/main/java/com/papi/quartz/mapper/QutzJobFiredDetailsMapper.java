package com.papi.quartz.mapper;

import java.util.List;
import java.util.Map;

import com.papi.quartz.bean.QutzJobFiredDetails;

public interface QutzJobFiredDetailsMapper {
    public List<QutzJobFiredDetails> find(Map<String,Object> paramMap) throws Exception;
    
    public int insert(QutzJobFiredDetails qutzJobFiredDetails) throws Exception;
    
    public int delete(Map<String,Object> paraMap) throws Exception;
    
    public int update(QutzJobFiredDetails qutzJobFiredDetails) throws Exception;
}

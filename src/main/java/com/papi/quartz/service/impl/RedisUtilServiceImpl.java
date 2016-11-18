package com.papi.quartz.service.impl;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.papi.quartz.service.RedisUtilService;

/**
 * 
 * @author fanshaowei
 *redis 通用类
 *
 */
@Service("redisUtilService")
public class RedisUtilServiceImpl implements RedisUtilService {
    Logger log = Logger.getLogger(RedisUtilServiceImpl.class);
	
	@Resource
	RedisTemplate<String, Object> redisTemplate;
	
	/**
	 * 获取key集合
	 */
	@Override
	public Set<String> getKeys(String pattern){
		return redisTemplate.keys(pattern);
	}
	
	/**
	 * 删除key
	 */
	@Override
	public void delKey(String key){
		redisTemplate.delete(key);
	}
	/*******Set*******************************************************************/
	/**
	 * 往Set集合里添加元素
	 * @param key
	 * @param obj
	 * @return
	 */
	@Override
	public Long sAdd(String key, Object obj){
		try{
			return redisTemplate.opsForSet().add(key, obj);
		}catch(Exception e){
			log.error("----------Fail add Set------------");
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 根据key取出Set的所有元素
	 * @param key
	 * @return
	 */
	@Override
	public Set<Object> sMembers(String key){
		try{
			return redisTemplate.opsForSet().members(key);
		}catch(Exception e){
			log.error("----------Fail get set members by key ------------");
			e.printStackTrace();
		}
	    
		return null;
	}
	
	/**
	 * 判断元素是否在Set集合内
	 * @param key
	 * @param obj
	 * @return
	 */
	@Override
	public Boolean isMember(String key,Object obj){
		try {
			return redisTemplate.opsForSet().isMember(key, obj);
		} catch (Exception e) {
		    log.error("----------Fail judge set member ------------");	
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 删除Set集合内的某个元素
	 * @param key
	 * @param obj
	 * @return
	 */
	@Override
	public Long remove(String key, Object obj){
		try {
			return redisTemplate.opsForSet().remove(key, obj);
		} catch (Exception e) {
			log.error("----------Fail remove set member ------------");
			e.printStackTrace();
		}
		
		return null;
	}

	/******key value********************************************************************/
	public String get(String key){
		return (String) redisTemplate.opsForValue().get(key);
	}
}

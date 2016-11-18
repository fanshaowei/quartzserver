package com.papi.quartz.service;

import java.util.Set;

public interface RedisUtilService {
	public Set<String> getKeys(String pattern);
	
	public void delKey(String key);
	
	public Long sAdd(String key, Object obj);
	
	public Set<Object> sMembers(String key);
	
	public Boolean isMember(String key,Object obj);
	
	public Long remove(String key, Object obj);
	
	public String get(String key);
	
}

package com.papi.quartz.commons.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

//暂时没用到
public class GenericServiceImpl<T, PK extends Serializable> implements GenericService<T, PK> {	
	
	@SuppressWarnings("unused")
	private Class<T> entity;
		
    public GenericServiceImpl(Class<T> entity) {
		this.entity = entity;
	}
	
	@Override
	public List<T> getAll() {
		return null;
	}

	@Override
	public List<T> getAll(Map<String, Object> paramMap) {
		return null;
	}

}

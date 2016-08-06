package com.papi.quartz.commons.generic;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

//暂时没用到
public abstract interface GenericService<T , PK extends Serializable> {
    public abstract List<T> getAll();
    
    public abstract List<T> getAll(Map<String, Object> paramMap);
}



package com.papi.quartz.bean;

import net.sf.json.JSONObject;

public class ReturnBean<T> {
    private String status;
    private String message;
    private T data;
     
    public static String ReturnBeanToString(String status,String message, Object data){
    	ReturnBean<Object> returnBean = new ReturnBean<Object>(status,message,data);
    	    	
    	return JSONObject.fromObject(returnBean).toString();
    }
    
	public ReturnBean(String status, String message, T data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
    
    
}

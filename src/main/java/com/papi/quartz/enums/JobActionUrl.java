package com.papi.quartz.enums;

public enum JobActionUrl {
    //SCENE_CONTROL("/dailyTriggerScene?username=:username&idScene=:idScene");
    SCENE_CONTROL("/sceneControl?username=:username&reqToken=:reqToken&idScene=:idScene");
    private String url;
    private JobActionUrl(String url){
    	this.url = url;
    }
        
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}   
}

package com.papi.quartz.commons;

import java.io.Serializable;

public class QuartzServerConfig implements Serializable{
	
	private static final long serialVersionUID = 1013293050850281467L;
	private String smarthomeSenseUrl;
    
	public QuartzServerConfig() {	
	}
	
	public QuartzServerConfig(String smarthomeSenseUrl) {
		this.smarthomeSenseUrl = smarthomeSenseUrl;
	}

	public String getSmarthomeSenseUrl() {
		return smarthomeSenseUrl;
	}

	public void setSmarthomeSenseUrl(String smarthomeSenseUrl) {
		this.smarthomeSenseUrl = smarthomeSenseUrl;
	}
    
    
}

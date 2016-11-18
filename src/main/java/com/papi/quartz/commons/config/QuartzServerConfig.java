package com.papi.quartz.commons.config;

/**
 * 
 * @author fanshaowei
 *
 **该类通过spring注入，读取quartzServerConfig.properties的配置
 */
public class QuartzServerConfig {
	
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

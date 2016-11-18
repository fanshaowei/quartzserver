package com.papi.quartz.commons.config;

/**
 * 
 * @author fanshaowei
 *
 *该类通过spring注入，读取netty.properties的配置
 */
public class NettyConfig {
	private String host;
	private int port;
	
	public NettyConfig(){
		System.out.println("--------------获取nettyClient配置--------------------------");
	}
		
	public NettyConfig(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}	
	
}

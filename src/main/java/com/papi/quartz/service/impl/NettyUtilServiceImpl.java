package com.papi.quartz.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.papi.netty.NettyClient;
import com.papi.quartz.commons.config.NettyConfig;
import com.papi.quartz.service.NettyUtilService;

/**
 * 
 * @author fanshaowei
 *在spring 容器中注入该实例时，创建一个netty客户端长链接，用来给定时任务定时触发器，发送情景控制信息到
 *netty服务端
 */

@Service("nettyUtilService")
public class NettyUtilServiceImpl implements NettyUtilService {
	
	@Autowired 
	private NettyConfig nettyConfig;
	
	private NettyClient nettyClient;
	private String host;
	private int port;
	private Thread nettyClientThread;
	
	public NettyUtilServiceImpl(){
		System.out.println("-----------------初始化nettyClient连接--------------------------");
	}
			
	@Override
	public NettyClient getNettyClient() {						
		return nettyClient;
	}
    
	@PostConstruct
	private void connectNetty(){
		this.setNettyProperties();
		
		nettyClient = new NettyClient();
		nettyClientThread = new Thread(){
			@Override
			public void run() {
				super.run();
				nettyClient.connect(host,port);
			}
		};	
		nettyClientThread.setDaemon(true);
		nettyClientThread.setName("nettyClient");
		nettyClientThread.start();
	}
		  
	@PreDestroy
	private void disconnectNetty(){
		nettyClient.disconnect();
		nettyClientThread = null;
	}
	
	private void setNettyProperties(){
		host = nettyConfig.getHost(); 
	    port = nettyConfig.getPort();
	}
		
}

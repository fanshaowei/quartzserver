package com.papi.quartz.web;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.papi.netty.NettyClient;
import com.papi.quartz.service.AppJobService;
import com.papi.quartz.service.NettyUtilService;

@Controller
@RequestMapping("test")
public class Test {

	@Resource
	NettyUtilService nettyUtilService;
	@Resource
	AppJobService appJobService;
	
	@RequestMapping("nettyReconnect")
	public @ResponseBody String nettyReconnectTest(){
		JSONObject jsonWrite = new JSONObject();
		jsonWrite.element("type", "sceneCtl");
		jsonWrite.element("username","13580387033");
		jsonWrite.element("sceneId",3);
		jsonWrite.element("reqToken","13580387033fb3430d267094e09aad63a135851b3c51508484784261");
		jsonWrite.element("jobName", "test");
		
		NettyClient nettyClient = nettyUtilService.getNettyClient();
		nettyClient.writeMesg(jsonWrite.toString());
		
		return "haah";
	}
	
	@RequestMapping("test")
	public @ResponseBody String test(){
		appJobService.deleteRedisJob("1", "010611000000382C", "hh");
		return "111";
	}
}

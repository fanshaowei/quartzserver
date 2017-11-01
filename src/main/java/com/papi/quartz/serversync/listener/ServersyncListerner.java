package com.papi.quartz.serversync.listener;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.papi.quartz.serversync.bean.RedisSyncDataBean;
import com.papi.quartz.serversync.service.ServerSyncService;


/**
 * 该监听器为redis消息订阅通道，用来接收安居家园Smarthome服务器删除家庭、网关 的信息
 * 订阅通道名为 SmarthomeDataSync
 * @author fanshaowei
 *
 */
@Component("messageListener")
public class ServersyncListerner implements MessageListener{
	@Resource
	RedisTemplate<String,Object> redisTemplate;	
	@Resource
	ServerSyncService serverSyncService;
	
	@Override
	public void onMessage(Message message, byte[] pattern) {		
		String msgStr = (String) redisTemplate.getValueSerializer().deserialize(message.getBody());
		
		RedisSyncDataBean msgJson = (RedisSyncDataBean) JSONObject.toBean(JSONObject.fromObject(msgStr), RedisSyncDataBean.class);		
		String msgType = msgJson.getMsgType();
				
		switch (msgType) {
		case "del-gateway":
			String idGateway = msgJson.getMsgMap().get("gatewayId").toString();
			String idFamily = 	msgJson.getMsgMap().get("idFamily").toString();															
			serverSyncService.deleSceneRelateJobByGateway(idFamily, idGateway);
			
			break;
		case "del-family":	
			String idFamily2 = msgJson.getMsgMap().get("familyId").toString();
			serverSyncService.deleSceneRelateJobByGateway(idFamily2, null);
			
			break;		
		default:
			break;
		}
	}

}

package com.papi.quartz.serversync.bean;

import java.util.Map;

/**
 * msgType:famaily、gateway、equitment
 * msgMap:{"famailyId":""}/{"gatewayId":""}/{"equitmentId":""}
 * operation:delete
 * @author fanshaowei
 *
 */
public class RedisSyncDataBean {
	private String msgType;
	private Map<String,?> msgMap;
	private String operation;
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public Map<String, ?> getMsgMap() {
		return msgMap;
	}
	public void setMsgMap(Map<String, ?> msgMap) {
		this.msgMap = msgMap;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
}

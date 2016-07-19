package com.papi.quartz.bean;

import java.io.Serializable;

/**
 * 情景关联信息表
 * @author fanshaowei
 *
 */
public class SenseDeviceSceneRelate implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -9183592486596846278L;
	private int id;
    private int idFamily;
    private String idGateway;
    private String idDevice;
    private String sceneJson;
    private String isValid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getIdFamily() {
		return idFamily;
	}
	public void setIdFamily(int idFamily) {
		this.idFamily = idFamily;
	}
	public String getIdGateway() {
		return idGateway;
	}
	public void setIdGateway(String idGateway) {
		this.idGateway = idGateway;
	}
	public String getIdDevice() {
		return idDevice;
	}
	public void setIdDevice(String idDevice) {
		this.idDevice = idDevice;
	}
	public String getSceneJson() {
		return sceneJson;
	}
	public void setSceneJson(String sceneJson) {
		this.sceneJson = sceneJson;
	}
	public String getIsValid() {
		return isValid;
	}
	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}
    
    
}


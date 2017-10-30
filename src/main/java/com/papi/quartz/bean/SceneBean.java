package com.papi.quartz.bean;

import java.io.Serializable;

public class SceneBean implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private int idScene;//情景表id
	private String sceneName;//情景名称
	private int idFamily;//家庭id
	private int scenePicture;//情景图标
	private int idGroup;//分组表id
	private String scenecontrolJson;//情景控制json指令（添加情景控制时添加）
	public int getIdScene() {
		return idScene;
	}
	public void setIdScene(int idScene) {
		this.idScene = idScene;
	}

	public String getSceneName() {
		return sceneName;
	}
	public void setSceneName(String sceneName) {
		this.sceneName = sceneName;
	}
	public int getIdFamily() {
		return idFamily;
	}
	public void setIdFamily(int idFamily) {
		this.idFamily = idFamily;
	}
	public int getScenePicture() {
		return scenePicture;
	}
	public void setScenePicture(int scenePicture) {
		this.scenePicture = scenePicture;
	}
	public int getIdGroup() {
		return idGroup;
	}
	public void setIdGroup(int idGroup) {
		this.idGroup = idGroup;
	}
	public String getScenecontrolJson() {
		return scenecontrolJson;
	}
	public void setScenecontrolJson(String scenecontrolJson) {
		this.scenecontrolJson = scenecontrolJson;
	}
	
	
	
}

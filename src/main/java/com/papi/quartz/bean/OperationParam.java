package com.papi.quartz.bean;

import java.io.Serializable;

public class OperationParam  implements Serializable{
	private static final long serialVersionUID = 1L;
	  private String name;
	  private String type;
	  private String value;
	  private String maxOccurs;
	  private String minOccurs;
	  private String isRequired;
	  private String isNillable;

	  public OperationParam()
	  {
	  }

	  public OperationParam(String name, String value, String type, String maxOccurs, String isNillable)
	  {
	    this.name = name;
	    this.value = value;
	    this.type = type;
	    this.maxOccurs = maxOccurs;
	    this.isNillable = isNillable;
	  }

	  public String getName() {
	    return this.name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }

	  public String getType() {
	    return this.type;
	  }

	  public void setType(String type) {
	    this.type = type;
	  }

	  public String getValue() {
	    return this.value;
	  }

	  public void setValue(String value) {
	    this.value = value;
	  }

	  public String getMaxOccurs() {
	    return this.maxOccurs;
	  }

	  public void setMaxOccurs(String maxOccurs) {
	    this.maxOccurs = maxOccurs;
	  }

	  public String getMinOccurs() {
	    return this.minOccurs;
	  }

	  public void setMinOccurs(String minOccurs) {
	    this.minOccurs = minOccurs;
	  }

	  public String getIsRequired() {
	    return this.isRequired;
	  }

	  public void setIsRequired(String isRequired) {
	    this.isRequired = isRequired;
	  }

	  public String getIsNillable() {
	    return this.isNillable;
	  }

	  public void setIsNillable(String isNillable) {
	    this.isNillable = isNillable;
	  }
}

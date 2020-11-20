package com.csgroup.auxip.model.jpa;

import javax.persistence.Entity;

import com.csgroup.auxip.model.jpa.AttributeJPA;

@Entity(name = "StringAttributes")
public class StringAttributeJPA extends AttributeJPA {
	
	private String value;

	public StringAttributeJPA(String name, String valueType, String value) {
		super(name, valueType);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	

}

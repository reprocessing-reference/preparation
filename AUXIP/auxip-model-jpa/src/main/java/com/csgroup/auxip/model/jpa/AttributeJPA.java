package com.csgroup.auxip.model.jpa;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AttributeJPA {	
	
	private String Name;
		
	private String ValueType;
	
	public AttributeJPA() {		
	}
	
	public AttributeJPA(String name, String valueType) {
		Name = name;
		ValueType = valueType;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getValueType() {
		return ValueType;
	}

	public void setValueType(String valueType) {
		ValueType = valueType;
	}	
}

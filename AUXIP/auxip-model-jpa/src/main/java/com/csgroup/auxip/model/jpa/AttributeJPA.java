package com.csgroup.auxip.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name ="Attributes")
public abstract class AttributeJPA {
	
	@Id
	private String Name;
		
	private String ValueType;
	
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

package com.csgroup.auxip.model.jpa;

import javax.persistence.Entity;

@Entity(name = "IntegerAttributes")
public class IntegerAttributeJPA extends AttributeJPA {
	
	private long value;

	public IntegerAttributeJPA(String name, String valueType, long value) {
		super(name, valueType);
		this.value = value;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
	
	
}

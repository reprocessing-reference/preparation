package com.csgroup.auxip.model.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class StringAttributeJPA extends AttributeJPA {
	
	private String Value;

	public StringAttributeJPA() {				
	}
	
	public StringAttributeJPA(String name, String valueType, String value) {
		super(name, valueType);
		this.Value = value;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		this.Value = value;
	}
	
	

}

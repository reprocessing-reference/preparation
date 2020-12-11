package com.csgroup.auxip.model.jpa;

import javax.persistence.Embeddable;

@Embeddable
public class DoubleAttributeJPA extends AttributeJPA {

	private double value;
	
	public DoubleAttributeJPA() {
		
	}

	public DoubleAttributeJPA(String name, String valueType, double value) {
		super(name, valueType);
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
}

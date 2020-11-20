package com.csgroup.auxip;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.CSC", key = { "Name" },containerName = "Container")
@EdmEntitySet("DoubleAttributes")
public class DoubleAttribute extends Attribute {


	@EdmProperty(name = "Value", nullable = false)
	private double value;
	
	public DoubleAttribute()
	{		
	}
	
	public DoubleAttribute(String name, String valueType, double value) {
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

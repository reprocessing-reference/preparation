package com.csgroup.auxip;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.DoubleAttributeJPA")
public class DoubleAttribute extends Attribute {


	@EdmProperty(name = "Value", nullable = false)
	@ODataJPAProperty
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

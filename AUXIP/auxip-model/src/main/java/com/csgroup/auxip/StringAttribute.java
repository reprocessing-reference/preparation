package com.csgroup.auxip;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.csgroup.auxip.Attribute;
import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;

@EdmComplex(namespace = "OData.CSC")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.StringAttributeJPA")
public class StringAttribute extends Attribute {
	
	@EdmProperty(name = "Value", nullable = false)
	@ODataJPAProperty
	private String Value;

	public StringAttribute() {		
		this.Value = "";
	}

	
	public StringAttribute(String name, String valueType, String value) {
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

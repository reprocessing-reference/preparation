package com.csgroup.auxip;

import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;
import com.csgroup.auxip.Attribute;
import com.csgroup.jpadatasource.annotations.ODataJPAEntity;

@EdmEntity(namespace = "OData.CSC", key = { "Name" },containerName = "Container")
@EdmEntitySet("StringAttributes")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.StringAttributeJPA")
public class StringAttribute extends Attribute {
	
	@EdmProperty(name = "Value", nullable = false)
	private String value;

	public StringAttribute(String name, String valueType, String value) {
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

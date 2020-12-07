package com.csgroup.auxip;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.CSC", key = "Name", containerName = "Container")
@EdmEntitySet("Attributes")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.AttributeJPA")
public abstract class Attribute {
	
	@EdmProperty(name = "Name", nullable = false)
	@ODataJPAProperty
	private String Name;
	
	@EdmProperty(name = "ValueType", nullable = false)
	@ODataJPAProperty
	private String ValueType;
	
	public Attribute() {		
	}
	
	public Attribute(String name, String valueType) {
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

package com.csgroup.auxip;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.PropertyJPA")
public class Property {

	@EdmProperty(name = "Name")
	@ODataJPAProperty
	private String Name;
	
	@EdmProperty(name = "Value")
	@ODataJPAProperty
	private String Value;	
	
}

package com.csgroup.auxip;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
public class Property {

	@EdmProperty(name = "Name")
	private String Name;
	
	@EdmProperty(name = "Value")
	private String Value;	
	
}

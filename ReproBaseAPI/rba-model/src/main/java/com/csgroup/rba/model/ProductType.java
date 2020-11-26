package com.csgroup.rba.model;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.RBA", key = "Level", containerName = "Container")
@EdmEntitySet("ProductLevels")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.ProductTypeJPA")
public class ProductType {

	@ODataJPAProperty
	@EdmProperty(name = "Type", nullable = false)
    private String Type;
	
	public String getType() {
		return Type;
	}

	public void setType(String name) {
		Type = name;
	}	
}

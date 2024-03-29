package com.csgroup.rba.model;


import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.RBA", key = "Type", containerName = "Container")
@EdmEntitySet("ProductTypes")
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

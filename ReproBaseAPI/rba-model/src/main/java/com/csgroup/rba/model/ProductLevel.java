package com.csgroup.rba.model;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.RBA", key = "Level", containerName = "Container")
@EdmEntitySet("ProductLevels")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.ProductLevelJPA")
public class ProductLevel {

	@ODataJPAProperty
	@EdmProperty(name = "Level", nullable = false)
    private String Level;
	
	public String getLevel() {
		return Level;
	}

	public void setLevel(String name) {
		Level = name;
	}	
}

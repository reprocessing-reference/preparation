package com.csgroup.rba.model;

import java.util.UUID;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.RBA", key = "Id", containerName = "Container")
@EdmEntitySet("Bands")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.BandJPA")
public class Band {

	
	@ODataJPAProperty(value = "identifier")
	@EdmProperty(name = "Id", nullable = false)
    private UUID Id;
	
	@ODataJPAProperty
	@EdmProperty(name = "Name", nullable = false)
    private String Name;

	public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
	
}

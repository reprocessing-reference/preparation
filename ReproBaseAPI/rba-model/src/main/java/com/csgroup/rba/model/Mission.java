package com.csgroup.rba.model;

import java.util.List;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.RBA", key = "Name", containerName = "Container")
@EdmEntitySet("Missions")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.MissionJPA")
public class Mission {

	@ODataJPAProperty
	@EdmProperty(name = "Name", nullable = false)
    private String Name;
	
	@ODataJPAProperty
    @EdmNavigationProperty(name = "AuxFiles", nullable = false)
    private List<AuxFile> AuxFiles;
        
    public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}	
}

package com.csgroup.rba.model;

import java.time.ZonedDateTime;
import java.util.UUID;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.RBA", key = "Name", containerName = "Container")
@EdmEntitySet("Baselines")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.BaselineJPA")
public class Baseline {

	@ODataJPAProperty
	@EdmProperty(name = "Name", nullable = false)
    private String Name;    
        
	@ODataJPAProperty
    @EdmProperty(name = "Date", precision = 3, nullable = false)
    private ZonedDateTime Date;
    
        
    public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	
	public ZonedDateTime getDate() {
		return Date;
	}

	public void setDate(ZonedDateTime date) {
		this.Date = date;
	}
	
}

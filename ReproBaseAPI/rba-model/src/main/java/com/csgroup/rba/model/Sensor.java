package com.csgroup.rba.model;

import java.util.List;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(namespace = "OData.RBA", key = "FullName", containerName = "Container")
@EdmEntitySet("Sensors")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.SensorJPA")
public class Sensor {

	@ODataJPAProperty
	@EdmProperty(name = "FullName", nullable = false)
    private String FullName;
	
	@ODataJPAProperty
	@EdmProperty(name = "ShortName", nullable = false)
    private String ShortName;
	
	@ODataJPAProperty
	@EdmProperty(name = "Unit", nullable = false)
    private String Unit;
	
	@ODataJPAProperty
	@EdmProperty(name = "Mission", nullable = false)
    private String Mission;	
	    
    public String getFullName() {
		return FullName;
	}

	public void setFullName(String name) {
		FullName = name;
	}
	
	public String getShortName() {
		return ShortName;
	}

	public void setShortName(String name) {
		ShortName = name;
	}
	
	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getMission() {
		return Mission;
	}

	public void setMission(String name) {
		Mission = name;
	}

}

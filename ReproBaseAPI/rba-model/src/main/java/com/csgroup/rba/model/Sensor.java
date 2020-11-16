package com.csgroup.rba.model;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
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
	@EdmProperty(name = "Satellite", nullable = false)
    private String Satellite;
        
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
	
	public String getSatellite() {
		return Satellite;
	}

	public void setSatellite(String name) {
		Satellite = name;
	}

}

package com.csgroup.rba.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Sensors")
public class SensorJPA {

	@Id
    private String FullName;
	private String ShortName;
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

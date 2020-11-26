package com.csgroup.rba.model.jpa;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "Sensors")
public class SensorJPA {

	@Id
    private String FullName;
	private String ShortName;
	private String Satellite;
	private String Mission;
	
    @ManyToMany
    private List<AuxFileJPA> AuxFiles;
        
            
    public List<AuxFileJPA> getAuxFiles() {
		return AuxFiles;
	}

	public void setAuxFiles(List<AuxFileJPA> auxFiles) {
		AuxFiles = auxFiles;
	}

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

	public String getMission() {
		return Mission;
	}

	public void setMission(String mission) {
		Mission = mission;
	}

}

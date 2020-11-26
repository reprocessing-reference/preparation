package com.csgroup.rba.model.jpa;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity(name = "Missions")
public class MissionJPA {

	@Id
    private String Name;
	
    @ManyToMany
    private List<AuxFileJPA> AuxFiles;
            
    public List<AuxFileJPA> getAuxFiles() {
		return AuxFiles;
	}

	public void setAuxFiles(List<AuxFileJPA> auxFiles) {
		AuxFiles = auxFiles;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

}

package com.csgroup.rba.model.jpa;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Bands")
public class BandJPA {

	@Id
    private UUID identifier;
	
	private String Name;

	public UUID getId() {
		return identifier;
	}

	public void setId(UUID id) {
		identifier = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	
	
}

package com.csgroup.rba.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "ProductLevels")
public class ProductLevelJPA {

	@Id
	private String Level;
	
	public String getLevel() {
		return Level;
	}

	public void setLevel(String name) {
		Level = name;
	}	
}

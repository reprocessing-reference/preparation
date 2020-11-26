package com.csgroup.rba.model.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "ProductTypes")
public class ProductTypeJPA {

	@Id
	private String Type;
	
	public String getType() {
		return Type;
	}

	public void setType(String name) {
		Type = name;
	}	
}

package com.csgroup.rba.model.jpa;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "Baselines")
public class BaselineJPA {

	@Id
    private String Name;    
    
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

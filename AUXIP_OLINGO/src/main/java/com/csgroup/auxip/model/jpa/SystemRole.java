package com.csgroup.auxip.model.jpa;


import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;


/**
 * @author Naceur MESKINI
 */
public class SystemRole {

	 private RoleType name;
     private String description;

     public RoleType getName() {
         return name;
     }
     public void setName(RoleType name) {
         this.name = name;
     }
     public String getDescription() {
         return description;
     }

     public void setDescription(String description) {
         this.description = description;
     }
	 
}

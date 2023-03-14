package com.csgroup.auxip.model.jpa;

/**
 * @author Naceur MESKINI
 */
public class SystemRole {


    private RoleType name;
    private String description;

    public SystemRole(){}
    public SystemRole(String name){
        if( name.equals(Globals.DOWNLOAD_ROLE))
        {
            this.name = RoleType.download;
        }
        if( name.equals(Globals.REPORTING_ROLE))
        {
            this.name = RoleType.download;
        }
        
    }

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

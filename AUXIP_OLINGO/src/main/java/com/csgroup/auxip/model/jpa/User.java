package com.csgroup.auxip.model.jpa;


import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.keycloak.representations.AccessToken;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Transient;


/**
 * @author Naceur MESKINI
 */
@Entity(name = "Users")
public class User {

     @Id
	 private String name;
	 private ZonedDateTime created;
     private String email;
     
    //  Volume over current Duration 
    @Embedded
    private DurationVolume downloadedVolume;
    //  Cumulative volumes of downloaded files for a given productType 
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
     private List<ProductTypedCounter> downloadedVolumes;
    //  Cumulative number of completed downloads
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
     private List<ProductTypedCounter> numberOfCompletedDownloads;
    //  Cumulative number of failed downloads
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ProductTypedCounter> numberOfFailedDownloads;

    //Read the following fields from JWT 
    @Transient
    private List<SystemRole> roles;
    // Maximum volume (GB) transfer within a defined period
    @Transient
    private Integer totalDownloadsQuota ;
    // Maximum number of separate downloads which can be performed in parallel 
    @Transient
    private Integer parallelDownloadsQuota ;

    public User(){}
    
    // define a user by parsing JWT
    public User(AccessToken token)
    {
        
    }

    public ZonedDateTime getCreated() {
        return created;
    }
    public String getEmail() {
        return email;
    }
    public String getName() {
        return name;
    }
    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setName(String name) {
        this.name = name;
    }

    public DurationVolume getDownloadedVolume() {
        return downloadedVolume;
    }
	 
    public List<ProductTypedCounter> getNumberOfCompletedDownloads() {
        return numberOfCompletedDownloads;
    }

    public List<ProductTypedCounter> getDownloadedVolumes() {
        return downloadedVolumes;
    }
	 
    public List<ProductTypedCounter> getNumberOfFailedDownloads() {
        return numberOfFailedDownloads;
    }

    public Integer getParallelDownloadsQuota() {
        return parallelDownloadsQuota;
    }

    public List<SystemRole> getRoles() {
        return roles;
    }
    public Integer getTotalDownloadsQuota() {
        return totalDownloadsQuota;
    }
    
}

package com.csgroup.auxip.model.jpa;

import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.keycloak.TokenVerifier;
import org.keycloak.representations.AccessToken;

import ch.qos.logback.core.joran.conditional.ElseAction;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;

import com.csgroup.auxip.config.QuotasConfiguration;
import com.csgroup.auxip.controller.AuxipBeanUtil;

/**
 * @author Naceur MESKINI
 */
@Entity(name = "Users")
public class User {

    @Id
    private String name;
    private LocalDateTime created;
    private String email;
    private LocalDateTime lastDownloadDateTime;
    private int numberOfParallelDownloads;

    // Volume over current Duration
    @Embedded
    private DurationVolume downloadedVolume;
    @ElementCollection(fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SELECT)
    private List<ProductTypedCounter> downloadsCounters;

    // Read the following fields from JWT
    @Transient
    private List<SystemRole> roles;


    public User() {

        this.numberOfParallelDownloads = 0;
        // init lastDownloadDateTime with the current dateTime even if there is no download yet
        this.lastDownloadDateTime = LocalDateTime.now();
        this.downloadedVolume = new DurationVolume();
        this.downloadedVolume.setPeriodStart(LocalDateTime.now());
        this.downloadedVolume.setVolume(0);
        this.created = LocalDateTime.now();
    }

    // define a user by parsing JWT
    public User(AccessToken token) {
        this.name = token.getPreferredUsername();
        this.email = token.getEmail();

        // get roles
        Set<String> keycloakRoles = token.getRealmAccess().getRoles();
        this.roles = new ArrayList<>();

        for (String role : keycloakRoles) {
            // roles
            if (!role.contains("Quota")) {
                SystemRole systemRole = new SystemRole();
                if (role.equals(Globals.DOWNLOAD_ROLE)) {
                    systemRole.setName(RoleType.download);
                }
                if (role.equals(Globals.REPORTING_ROLE)) {
                    systemRole.setName(RoleType.reporting);
                }

                this.roles.add(systemRole);
            }
        }
        this.downloadedVolume = new DurationVolume();
        this.downloadedVolume.setPeriodStart(LocalDateTime.now());
        this.downloadedVolume.setVolume(0);
        this.created = LocalDateTime.now();
        this.numberOfParallelDownloads = 0;
        // init lastDownloadDateTime with the current dateTime even there is no download yet
        this.lastDownloadDateTime = LocalDateTime.now();
    }

    public int getNumberOfParallelDownloads() {
        return numberOfParallelDownloads;
    }

    public void setNumberOfParallelDownloads(int numberOfParallelDownloads) {
        this.numberOfParallelDownloads = numberOfParallelDownloads;
    }

    public void setLastDownloadDateTime(LocalDateTime lastDownloadDateTime) {
        this.lastDownloadDateTime = lastDownloadDateTime;
    }
    public LocalDateTime getLastDownloadDateTime() {
        return lastDownloadDateTime;
    }
    public LocalDateTime getCreated() {
        return created;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setCreated(LocalDateTime created) {
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
        return this.downloadsCounters.stream()
                .filter(counter -> CounterType.completed.equals(counter.getCounterType()))
                .collect(Collectors.toList());
    }

    public List<ProductTypedCounter> getDownloadedVolumes() {

        return this.downloadsCounters.stream().filter(counter -> CounterType.volume.equals(counter.getCounterType()))
                .collect(Collectors.toList());

    }

    public List<ProductTypedCounter> getNumberOfFailedDownloads() {

        return this.downloadsCounters.stream().filter(counter -> CounterType.failed.equals(counter.getCounterType()))
                .collect(Collectors.toList());
    }
    public List<SystemRole> getRoles() {
        return roles;
    }

    /**
     * increment the downloaded volume
     * and update numberOfParallelDownloads 
     * @param volume
     */
    public void updateDownloadedVolume(long volume) {

        QuotasConfiguration quotasConfiguration = AuxipBeanUtil.getBean(QuotasConfiguration.class);

        // update the total volume
        this.downloadedVolume.addVolume(volume);
        
        // update numberOfParallelDownloads
        LocalDateTime limitDateTime =  this.lastDownloadDateTime.plusSeconds(quotasConfiguration.getParallelDownloadsDeltaTime().getSeconds());
        LocalDateTime now = LocalDateTime.now();
                
        if (now.isAfter(limitDateTime) )
        {
            // start a new counting 
            this.numberOfParallelDownloads = 1;
        }else
        {
            this.numberOfParallelDownloads ++;
        }
        // update the lastDownloadDateTime
        this.lastDownloadDateTime = now ;

    }

    /**
     * Increment the corresponding counter 
     * @param productName : product Name where to get productType , plateform Short Name and platform Serial Id
     * @param counterType : one of the following : counterType.volume, counterType.completed, counterType.failed
     * @param increment : depending on counterType , this can be a volume or an increment unit.
     */
    public void incrementDownloadsCounter(String productName,CounterType counterType,long increment)
    {
        Map<String,String> shortNamesMapping = new HashMap<>();
        shortNamesMapping.put("S1",Globals.SENTINEL_1);
        shortNamesMapping.put("S2",Globals.SENTINEL_2);
        shortNamesMapping.put("S3",Globals.SENTINEL_3);
        
        // First get ProductType/platformShortName and platformSerialIdentifier from the productName
        String produtType;
        String platformShortName = shortNamesMapping.get(productName.substring(0, 2));
        String platformSerialIdentifier = productName.substring(2, 3); //"A" or "B" or "_"
        // Sentinel-1
        if( platformShortName.equals(Globals.SENTINEL_1) )
        {     
            // .SAFE => S1__AUX_WND_V20201008T120000_G20201005T061654.SAFE.zip
            // .EOF => S1B_OPER_AUX_RESORB_OPOD_20201004T194235_V20201004T152329_20201004T184100.EOF.zip
            produtType = productName.contains(".SAFE") ? productName.substring(4, 11) : productName.substring(9, 19) ;
        }else if ( platformShortName.equals(Globals.SENTINEL_2) )
        { // Sentinel-2
            // S2__OPER_AUX_ECMWFD_PDMC_20180501T000000_V20180502T060000_20180502T180000.TGZ
            produtType = productName.substring(9, 19) ;

        }else{// Sentinel-3
            // S3B_OL_1_CAL_AX_20190317T203033_20991231T235959_20190320T120000___________________MPC_O_AL_006.SEN3.zip
            produtType = productName.substring(4, 15) ;
        }   
        // Get the right counter by applying filters  

        ProductTypedCounter productTypedCounter = this.downloadsCounters.stream()
        .filter(counter -> counterType.equals(counter.getCounterType()))
        .filter(counter -> produtType.equals(counter.getProductType()))
        .filter(counter -> platformShortName.equals(counter.getPlateForm()))
        .filter(counter -> platformSerialIdentifier.equals(counter.getUnit()))
        .findFirst() 
        .orElse(null);

        if( productTypedCounter == null )
        {
            // add a new counter to downloadsCounters
            productTypedCounter = new ProductTypedCounter();
            productTypedCounter.setValue(increment);
            productTypedCounter.setCounterType(counterType);
            productTypedCounter.setProductType(produtType);
            productTypedCounter.setPlateForm(platformShortName);
            productTypedCounter.setUnit(platformSerialIdentifier);
            
            this.downloadsCounters.add(productTypedCounter);
        }else
        {
            // increment the value of this counter
            productTypedCounter.setValue( productTypedCounter.getValue() + increment );
        }
    }    

    /**
     * Increment the corresponding counter 
     * @param productName : product Name where to get productType , plateform ShortName and platform Serial Id
     * @param counterType : one of the following : counterType.volume, counterType.completed, counterType.failed
     */
    public void incrementDownloadsCounter(String productName,CounterType counterType)
    {
        this.incrementDownloadsCounter(productName, counterType,1);
    }


    public void setRoles(Set<String> keycloakRoles) 
    {
        this.roles = new ArrayList<>();
        for (String role : keycloakRoles) {
            // roles
            if (!role.contains("Quota")) {
                SystemRole systemRole = new SystemRole();
                if (role.equals(Globals.DOWNLOAD_ROLE)) {
                    systemRole.setName(RoleType.download);
                }
                if (role.equals(Globals.REPORTING_ROLE)) {
                    systemRole.setName(RoleType.reporting);
                }

                this.roles.add(systemRole);
            }
        }

    }

}

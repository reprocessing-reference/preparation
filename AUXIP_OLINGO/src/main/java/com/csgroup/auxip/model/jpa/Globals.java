package com.csgroup.auxip.model.jpa;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Globals {
    
    public static final String SENTINEL_1 = "Sentinel-1"; // Sentinel-1 Short name
    public static final String SENTINEL_2 = "Sentinel-2"; // Sentinel-1 Short name
    public static final String SENTINEL_3 = "Sentinel-3"; // Sentinel-1 Short name

    public static final String NAMESPACE = "OData.CSC"; // the namespace of odata entities
    public static final Integer OK = 0; // Ok return code status
    public static final Integer NOT_OK = 1; // not Ok 
    public static final String DOWNLOAD_ROLE = "download"; //tobe fixed to the right value 
    public static final String REPORTING_ROLE = "reporting"; //tobe fixed to the right value 
    public static final Integer TOO_MANY_REQUESTS = 429; //Too Many Requests: if the download quota is exceeded 
    
    // public static final Duration DOWNLOAD_DURATION = Duration.ofDays(8); //tobe fixed to the right value 
    // public static final Integer TOTAL_DOWNLOADS_QUOTA = 1000; // Maximum volume (GB) transfer within a defined period 
    // public static final Integer PARALLEL_DOWNLOADS_QUOTA = 100; // Maximum number of separate downloads which can be performed in parallel
    // public static final Duration PARALLEL_DOWNLOADS_DELTA_TIME = Duration.ofSeconds(10); //Delta time between two consecutive downloads 

}

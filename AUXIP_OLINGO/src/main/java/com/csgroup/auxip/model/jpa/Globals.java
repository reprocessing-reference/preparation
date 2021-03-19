package com.csgroup.auxip.model.jpa;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class Globals {
    
    public static final String NAMESPACE = "OData.CSC"; // the namespace of odata entities
    public static final Integer OK = 0; // Ok return code status
    public static final Integer NOT_OK = 1; // not Ok 
    public static final Duration DOWNLOAD_DURATION = Duration.ofDays(8); //tobe fixed to the right value 
    
}

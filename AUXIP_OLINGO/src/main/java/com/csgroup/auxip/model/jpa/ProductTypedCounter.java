package com.csgroup.auxip.model.jpa;


import javax.persistence.Embeddable;


/**
 * @author Naceur MESKINI
 */
@Embeddable
public class ProductTypedCounter {
    // product Type
     private String productType;
    //  can be one of the following : volume,completed,failed
     private CounterType counterType;
    //  value of the counter 
     private long value;


}

package com.csgroup.auxip.model.jpa;


import javax.persistence.Embeddable;


/**
 * @author Naceur MESKINI
 */
@Embeddable
public class ProductTypedCounter {
    // product Type
     private String productType;
    // product Type
     private String plateForm;
    // product Type
     private String unit;
    //  can be one of the following : volume,completed,failed
     private CounterType counterType;
    //  value of the counter 
     private long value;
     
	public String getProductType() {
		return productType;
	}
	public String getPlateForm() {
		return plateForm;
	}
	public String getUnit() {
		return unit;
	}
	public CounterType getCounterType() {
		return counterType;
	}
	public long getValue() {
		return value;
	}
     
     


}

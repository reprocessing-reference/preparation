package com.csgroup.auxip.model.jpa;


import javax.persistence.Embeddable;


/**
 * @author Naceur MESKINI
 */
@Embeddable
public class ProductTypedCounter {
    // product Type
     private String productType;
    //  plateForm
     private String plateForm;
    // product unit
     private String unit;
    //  can be one of the following : volume,completed,failed
     private CounterType counterType;
    //  value of the counter 
     private long value;


     public CounterType getCounterType() {
         return counterType;
     }

     public String getProductType() {
         return productType;
     }

     public long getValue() {
         return value;
     }

     public void setProductType(String productType) {
         this.productType = productType;
     }

     public void setCounterType(CounterType counterType) {
         this.counterType = counterType;
     }

     public void setValue(long value) {
         this.value = value;
     }

     public String getPlateForm() {
         return plateForm;
     }

     public String getUnit() {
         return unit;
     }

     public void setPlateForm(String plateForm) {
         this.plateForm = plateForm;
     }
     public void setUnit(String unit) {
         this.unit = unit;
     }
    
}

package com.csgroup.rba.model;

import java.time.ZonedDateTime;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.ChecksumJPA")
@EdmComplex(namespace = "OData.RBA")
public class Checksum {

	public Checksum(String algorithm, String value, ZonedDateTime checksumDate) {
		super();
		Algorithm = algorithm;
		Value = value;
		ChecksumDate = checksumDate;
	}

	public Checksum() {		
	}

	@ODataJPAProperty
	@EdmProperty(name = "Algorithm", nullable = false)
	private String Algorithm;
	
	@ODataJPAProperty
	@EdmProperty(name = "Value", nullable = false)
	private String Value;	
	
	@ODataJPAProperty
	@EdmProperty(name = "ChecksumDate", nullable = false)
	private ZonedDateTime ChecksumDate;
	
	public String getAlgorithm() {
		return Algorithm;
	}

	public void setAlgorithm(String algorithm) {
		Algorithm = algorithm;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		Value = value;
	}

	public ZonedDateTime getChecksumDate() {
		return ChecksumDate;
	}

	public void setChecksumDate(ZonedDateTime checksumDate) {
		ChecksumDate = checksumDate;
	}

}

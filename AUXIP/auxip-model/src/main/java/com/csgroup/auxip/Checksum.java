package com.csgroup.auxip;

import java.time.ZonedDateTime;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.ChecksumJPA")
public class Checksum {

	public Checksum(String algorithm, String value, ZonedDateTime checksumDate) {
		super();
		Algorithm = algorithm;
		Value = value;
		ChecksumDate = checksumDate;
	}

	public Checksum() {		
	}

	@EdmProperty(name = "Algorithm")
	@ODataJPAProperty
	private String Algorithm;
	
	@EdmProperty(name = "Value")
	@ODataJPAProperty
	private String Value;	
	
	@EdmProperty(name = "ChecksumDate")
	@ODataJPAProperty
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

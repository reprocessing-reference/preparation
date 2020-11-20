package com.csgroup.auxip;

import java.time.ZonedDateTime;

import com.sdl.odata.api.edm.annotations.EdmComplex;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmComplex(namespace = "OData.CSC")
public class Checksum {

	@EdmProperty(name = "Algorithm")
	private String Algorithm;
	
	@EdmProperty(name = "Value")
	private String Value;	
	
	@EdmProperty(name = "ChecksumDate")
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

package com.csgroup.rba.model.jpa;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;

@Embeddable
public class ChecksumJPA {

	private UUID Id;
	private String Algorithm;
	private String Value;	
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

	public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}

}

/**
 * Copyright (c) 2020 CS Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.csgroup.rba.model.jpa;

import java.time.ZonedDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;


import java.util.List;

/**
 * @author besquis
 */
@Entity(name = "AuxFiles")
public class AuxFileJPA {
	
	@Id
    private UUID identifier;    
	
	@ManyToOne(optional = false)
	private AuxTypeJPA AuxType;
	
	private String FullName;
    
	private String Baseline;
	
	@Column(nullable = true)
	private String IpfVersion;
	
	private String        Unit;
	private ZonedDateTime ValidityStart;
	private ZonedDateTime ValidityStop;
	
	private ZonedDateTime SensingTimeApplicationStart;
	
	private ZonedDateTime SensingTimeApplicationStop;
   
	private ZonedDateTime CreationDate;
		
	private String Band;    
    
	public AuxFileJPA() {
		// TODO Auto-generated constructor stub
	}

	public UUID getIdentifier() {
		return identifier;
	}

	public void setIdentifier(UUID id_) {
		identifier = id_;
	}

	public AuxTypeJPA getAuxType() {
		return AuxType;
	}

	public void setAuxType(AuxTypeJPA fileType) {
		AuxType = fileType;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}	
	
	public ZonedDateTime getValidityStart() {
		return ValidityStart;
	}

	public void setValidityStart(ZonedDateTime validityStart) {
		ValidityStart = validityStart;
	}

	public ZonedDateTime getValidityStop() {
		return ValidityStop;
	}

	public void setValidityStop(ZonedDateTime validityStop) {
		ValidityStop = validityStop;
	}

	public ZonedDateTime getSensingTimeApplicationStart() {
		return SensingTimeApplicationStart;
	}

	public void setSensingTimeApplicationStart(ZonedDateTime sensingTimeApplicationStart) {
		SensingTimeApplicationStart = sensingTimeApplicationStart;
	}

	public ZonedDateTime getSensingTimeApplicationStop() {
		return SensingTimeApplicationStop;
	}

	public void setSensingTimeApplicationStop(ZonedDateTime sensingTimeApplicationStop) {
		SensingTimeApplicationStop = sensingTimeApplicationStop;
	}

	public ZonedDateTime getCreationDate() {
		return CreationDate;
	}

	public void setCreationDate(ZonedDateTime creationDate) {
		CreationDate = creationDate;
	}

	public String getBand() {
		return Band;
	}

	public void setBand(String bands) {
		Band = bands;
	}

	public String getBaseline() {
		return Baseline;
	}

	public void setBaseline(String baseline) {
		Baseline = baseline;
	}

	public String getUnit() {
		return Unit;
	}

	public void setUnit(String unit) {
		Unit = unit;
	}

	public String getIpfVersion() {
		return IpfVersion;
	}

	public void setIpfVersion(String ipfVersion) {
		IpfVersion = ipfVersion;
	}

	
}

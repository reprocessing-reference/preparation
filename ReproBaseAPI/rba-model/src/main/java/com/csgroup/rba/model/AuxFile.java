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
package com.csgroup.rba.model;

import com.csgroup.rba.model.annotations.ODataJPAEntity;
import com.csgroup.rba.model.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.List;

/**
 * @author besquis
 */
@EdmEntity(namespace = "OData.RBA", key = "Id", containerName = "Container")
@EdmEntitySet("AuxFiles")
@ODataJPAEntity(value = "com.csgroup.rba.model.jpa.AuxFileJPA")
public class AuxFile {
	
	@ODataJPAProperty(value = "identifier")
	@EdmProperty(name = "Id", nullable = false)
    private UUID Id;    
	
	@ODataJPAProperty
	@EdmNavigationProperty(name = "AuxType", nullable = false)
	private AuxType AuxType;
	
	@ODataJPAProperty
	@EdmProperty(name = "FullName", nullable = false)
    private String FullName;
    
	@ODataJPAProperty
    @EdmNavigationProperty(name = "Baseline", nullable = false)
    private Baseline Baseline;
    
	@ODataJPAProperty
    @EdmProperty(name = "TimeDependency", nullable = false)
    private TimeDependency TimeDependency;
	
	@ODataJPAProperty
    @EdmProperty(name = "ValidityStart", nullable = false)
    private ZonedDateTime ValidityStart;
	
	@ODataJPAProperty
    @EdmProperty(name = "ValidityStop", nullable = false)
    private ZonedDateTime ValidityStop;
	
	@ODataJPAProperty
    @EdmProperty(name = "SensingTimeApplicationStart", nullable = false)
    private ZonedDateTime SensingTimeApplicationStart;
	
	@ODataJPAProperty
    @EdmProperty(name = "SensingTimeApplicationStop", nullable = false)
    private ZonedDateTime SensingTimeApplicationStop;
   
	@ODataJPAProperty
    @EdmProperty(name = "CreationDate", precision = 3, nullable = false)
    private ZonedDateTime CreationDate;
    
	@ODataJPAProperty
    @EdmNavigationProperty(name = "Band", nullable = false)
    private Band Band;
    
	@ODataJPAProperty
    @EdmProperty(name = "Checksum", nullable = false)
    private Checksum Checksum;
    
	public AuxFile() {
		// TODO Auto-generated constructor stub
	}

	public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}

	public AuxType getAuxType() {
		return AuxType;
	}

	public void setAuxType(AuxType fileType) {
		AuxType = fileType;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public Baseline getBaseline() {
		return Baseline;
	}

	public void setBaseline(Baseline baseline) {
		Baseline = baseline;
	}
	
	public TimeDependency getTimeDependency() {
		return TimeDependency;
	}

	public void setTimeDependency(TimeDependency timeDependency) {
		TimeDependency = timeDependency;
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

	public Band getBand() {
		return Band;
	}

	public void setBand(Band band) {
		Band = band;
	}

	public Checksum getChecksum() {
		return Checksum;
	}

	public void setChecksum(Checksum checksum) {
		Checksum = checksum;
	}
	
   
}

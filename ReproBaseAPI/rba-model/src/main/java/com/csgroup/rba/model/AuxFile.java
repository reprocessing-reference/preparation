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
	@EdmNavigationProperty(name = "FileType", nullable = false)
	private AuxFileType FileType;
	
	@ODataJPAProperty
	@EdmProperty(name = "FullName", nullable = false)
    private String FullName;
    
	@ODataJPAProperty
    @EdmNavigationProperty(name = "Baseline", nullable = false)
    private Baseline Baseline;
    
	@ODataJPAProperty
    @EdmProperty(name = "Validity", nullable = false)
    private TimeRange Validity;
   
	@ODataJPAProperty
    @EdmProperty(name = "CreationDate", precision = 3, nullable = false)
    private ZonedDateTime CreationDate;
    
	@ODataJPAProperty
    @EdmNavigationProperty(name = "Bands", nullable = false)
    private List<Band> Bands;
        
	@ODataJPAProperty
    @EdmNavigationProperty(name = "Sensor", nullable = false)
    private List<Sensor> Sensors;
    
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

	public AuxFileType getFileType() {
		return FileType;
	}

	public void setFileType(AuxFileType fileType) {
		FileType = fileType;
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

	public TimeRange getValidity() {
		return Validity;
	}

	public void setValidity(TimeRange validity) {
		Validity = validity;
	}

	public ZonedDateTime getCreationDate() {
		return CreationDate;
	}

	public void setCreationDate(ZonedDateTime creationDate) {
		CreationDate = creationDate;
	}

	public List<Band> getBands() {
		return Bands;
	}

	public void setBands(List<Band> bands) {
		Bands = bands;
	}

	public List<Sensor> getSensors() {
		return Sensors;
	}

	public void setSensors(List<Sensor> sensors) {
		Sensors = sensors;
	}

	public Checksum getChecksum() {
		return Checksum;
	}

	public void setChecksum(Checksum checksum) {
		Checksum = checksum;
	}
	
   
}

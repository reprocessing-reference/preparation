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

import javax.persistence.Embedded;
import javax.persistence.Entity;
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
	private AuxFileTypeJPA FileType;
	
	private String FullName;
    
	@ManyToOne(optional = false)
	private BaselineJPA Baseline;
	
	@Embedded
	private TimeRangeJPA Validity;
   
	private ZonedDateTime CreationDate;
	
	@ManyToMany
	private List<BandJPA> Bands;
        
    @ManyToMany
    private List<SensorJPA> Sensors;
    
    @Embedded
    private ChecksumJPA Checksum;
    
	public AuxFileJPA() {
		// TODO Auto-generated constructor stub
	}

	public UUID getIdentifier() {
		return identifier;
	}

	public void setIdentifier(UUID id_) {
		identifier = id_;
	}

	public AuxFileTypeJPA getFileType() {
		return FileType;
	}

	public void setFileType(AuxFileTypeJPA fileType) {
		FileType = fileType;
	}

	public String getFullName() {
		return FullName;
	}

	public void setFullName(String fullName) {
		FullName = fullName;
	}

	public BaselineJPA getBaseline() {
		return Baseline;
	}

	public void setBaseline(BaselineJPA baseline) {
		Baseline = baseline;
	}

	public TimeRangeJPA getValidity() {
		return Validity;
	}

	public void setValidity(TimeRangeJPA validity) {
		Validity = validity;
	}

	public ZonedDateTime getCreationDate() {
		return CreationDate;
	}

	public void setCreationDate(ZonedDateTime creationDate) {
		CreationDate = creationDate;
	}

	public List<BandJPA> getBands() {
		return Bands;
	}

	public void setBands(List<BandJPA> bands) {
		Bands = bands;
	}

	public List<SensorJPA> getSensors() {
		return Sensors;
	}

	public void setSensors(List<SensorJPA> sensors) {
		Sensors = sensors;
	}

	public ChecksumJPA getChecksum() {
		return Checksum;
	}

	public void setChecksum(ChecksumJPA checksum) {
		Checksum = checksum;
	}
	
   
}

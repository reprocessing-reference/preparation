/**
 * Copyright (c) 2015 SDL Group
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
package com.csgroup.auxip;

import com.csgroup.jpadatasource.annotations.ODataJPAEntity;
import com.csgroup.jpadatasource.annotations.ODataJPAProperty;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmNavigationProperty;
import com.sdl.odata.api.edm.annotations.EdmProperty;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.List;

/**
 * @author rdevries
 */
@EdmEntity(namespace = "OData.CSC", key = "Id", containerName = "Container")
@EdmEntitySet("Products")
@ODataJPAEntity(value = "com.csgroup.auxip.model.jpa.ProductJPA")
public class Product {

    @EdmProperty(name = "Id")
    @ODataJPAProperty
    private UUID Id;
    
    @EdmProperty(name = "Name")
    @ODataJPAProperty
    private String Name;
    
    @EdmProperty(name = "ContentType")
    @ODataJPAProperty
    private String ContentType;
    
    @EdmProperty(name = "ContentLength")
    @ODataJPAProperty
    private long ContentLength;
    
    @EdmProperty(name = "OriginDate", precision = 3)
    @ODataJPAProperty
    private ZonedDateTime OriginDate;
    
    @EdmProperty(name = "PublicationDate", precision = 3)
    @ODataJPAProperty
    private ZonedDateTime PublicationDate;
    
    @EdmProperty(name = "EvictionDate", precision = 3)
    @ODataJPAProperty
    private ZonedDateTime EvictionDate;
          
   //"Checksum" Type="Collection(OData.CSC.Checksum)"/>
    @EdmProperty(name = "Checksum", nullable = false)
    @ODataJPAProperty
    private List<Checksum> Checksums;
    
    @EdmProperty(name = "ContentDate", nullable = false)
    @ODataJPAProperty
    private TimeRange ContentDate;
       
    @EdmProperty(name = "StringAttributes", nullable = true)
    @ODataJPAProperty
    private List<StringAttribute> StringAttributes;
    
    @EdmProperty(name = "IntegerAttributes", nullable = true)
    @ODataJPAProperty
    private List<IntegerAttribute> IntegerAttributes;
    
    @EdmProperty(name = "DoubleAttributes", nullable = true)
    @ODataJPAProperty
    private List<DoubleAttribute> DoubleAttributes;
    
    @EdmProperty(name = "DateTimeOffsetAttributes", nullable = true)
    @ODataJPAProperty
    private List<DateTimeOffsetAttribute> DateTimeOffsetAttributes;   
		
	public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getContentType() {
		return ContentType;
	}

	public void setContentType(String contentType) {
		ContentType = contentType;
	}

	public long getContentLength() {
		return ContentLength;
	}

	public void setContentLength(long contentLength) {
		ContentLength = contentLength;
	}

	public ZonedDateTime getOriginDate() {
		return OriginDate;
	}

	public void setOriginDate(ZonedDateTime originDate) {
		OriginDate = originDate;
	}

	public ZonedDateTime getPublicationDate() {
		return PublicationDate;
	}

	public void setPublicationDate(ZonedDateTime publicationDate) {
		PublicationDate = publicationDate;
	}

	public ZonedDateTime getEvictionDate() {
		return EvictionDate;
	}

	public void setEvictionDate(ZonedDateTime evictionDate) {
		EvictionDate = evictionDate;
	}
	
	public List<Checksum> getChecksums() {
		return Checksums;
	}

	public void setChecksums(List<Checksum> checksums) {
		Checksums = checksums;
	}

	public TimeRange getContentDate() {
		return ContentDate;
	}

	public void setContentDate(TimeRange contentDate) {
		ContentDate = contentDate;
	}
	
}

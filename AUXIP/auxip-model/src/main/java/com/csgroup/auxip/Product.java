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
public class Product {

    @EdmProperty(name = "Id")
    private UUID Id;
    
    @EdmProperty(name = "Name")
    private String Name;
    
    @EdmProperty(name = "ContentType")
    private String ContentType;
    
    @EdmProperty(name = "ContentLength")
    private long ContentLength;
    
    @EdmProperty(name = "OriginDate", precision = 3)
    private ZonedDateTime OriginDate;
    
    @EdmProperty(name = "PublicationDate", precision = 3)
    private ZonedDateTime PublicationDate;
    
    @EdmProperty(name = "EvictionDate", precision = 3)
    private ZonedDateTime EvictionDate;
          
   //"Checksum" Type="Collection(OData.CSC.Checksum)"/>
    @EdmProperty(name = "Checksum", nullable = false)
    private List<Checksum> Checksums;
    
    @EdmProperty(name = "ContentDate", nullable = false)
    private TimeRange ContentDate;

       
    @EdmNavigationProperty(name = "Attributes", nullable = false)
    private List<Attribute> m_attributes;
    
    @EdmNavigationProperty(name = "StringAttributes", nullable = false)
    private List<StringAttribute> m_stringAttributes;
    
    @EdmNavigationProperty(name = "IntegerAttributes", nullable = false)
    private List<IntegerAttribute> m_integerAttributes;
    
    @EdmNavigationProperty(name = "DoubleAttributes", nullable = false)
    private List<DoubleAttribute> m_doubleAttributes;

    @EdmNavigationProperty(name = "DateTimeOffsetAttributes", nullable = false)
    private List<DateTimeOffsetAttribute> m_dateTimeOffsetAttributes;
    
	public Product(UUID id, String name, String contentType, long contentLength, ZonedDateTime originDate,
		ZonedDateTime publicationDate, ZonedDateTime evictionDate, List<Attribute> attributes) {
	Id = id;
	Name = name;
	ContentType = contentType;
	ContentLength = contentLength;
	OriginDate = originDate;
	PublicationDate = publicationDate;
	EvictionDate = evictionDate;
	m_attributes = attributes;
}

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

	public List<Attribute> getAttributes() {
		return m_attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		m_attributes = attributes;
	}
    
    

   
}

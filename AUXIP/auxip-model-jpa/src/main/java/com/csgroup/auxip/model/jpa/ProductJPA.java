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
package com.csgroup.auxip.model.jpa;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import java.util.List;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author besquis
 */
@Entity(name = "Product")
public class ProductJPA {
	@Id    
	private UUID Id;
    private String Name;
    private String ContentType;
    private long ContentLength;
    private ZonedDateTime OriginDate;
    private ZonedDateTime PublicationDate;
    private ZonedDateTime EvictionDate;    
    @OneToMany
    private List<ChecksumJPA> Checksums;
    @Embedded
    private TimeRangeJPA ContentDate;
    @OneToMany
    private List<AttributeJPA> m_attributes;
    @OneToMany
    private List<StringAttributeJPA> m_stringAttributes;
    @OneToMany
    private List<IntegerAttributeJPA> m_integerAttributes;
    @OneToMany
    private List<DoubleAttributeJPA> m_doubleAttributes;
    @OneToMany
    private List<DateTimeOffsetAttributeJPA> m_dateTimeOffsetAttributes;
    
	public ProductJPA(UUID id, String name, String contentType, long contentLength, ZonedDateTime originDate,
		ZonedDateTime publicationDate, ZonedDateTime evictionDate, List<AttributeJPA> attributes) {
	Id = id;
	Name = name;
	ContentType = contentType;
	ContentLength = contentLength;
	OriginDate = originDate;
	PublicationDate = publicationDate;
	EvictionDate = evictionDate;
	m_attributes = attributes;
    }
	
	public ProductJPA() {
		// TODO Auto-generated constructor stub
	}
	
	
	public UUID getId() {
		return Id;
	}

	public void setId(UUID id) {
		Id = id;
	}
	@Column
	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}
	@Column
	public String getContentType() {
		return ContentType;
	}

	public void setContentType(String contentType) {
		ContentType = contentType;
	}
	@Column
	public long getContentLength() {
		return ContentLength;
	}

	public void setContentLength(long contentLength) {
		ContentLength = contentLength;
	}
	@Column
	public ZonedDateTime getOriginDate() {
		return OriginDate;
	}

	public void setOriginDate(ZonedDateTime originDate) {
		OriginDate = originDate;
	}
	@Column
	public ZonedDateTime getPublicationDate() {
		return PublicationDate;
	}

	public void setPublicationDate(ZonedDateTime publicationDate) {
		PublicationDate = publicationDate;
	}
	@Column
	public ZonedDateTime getEvictionDate() {
		return EvictionDate;
	}

	public void setEvictionDate(ZonedDateTime evictionDate) {
		EvictionDate = evictionDate;
	}
	
	public List<AttributeJPA> getAttributes() {
		return m_attributes;
	}

	public void setAttributes(List<AttributeJPA> attributes) {
		m_attributes = attributes;
	}

	public List<IntegerAttributeJPA> getIntegerAttributes() {
		return m_integerAttributes;
	}

	public void setIntegerAttributes(List<IntegerAttributeJPA> m_integerAttributes) {
		this.m_integerAttributes = m_integerAttributes;
	}

	public List<DoubleAttributeJPA> getDoubleAttributes() {
		return m_doubleAttributes;
	}

	public void setDoubleAttributes(List<DoubleAttributeJPA> m_doubleAttributes) {
		this.m_doubleAttributes = m_doubleAttributes;
	}

	public List<DateTimeOffsetAttributeJPA> getDateTimeOffsetAttributes() {
		return m_dateTimeOffsetAttributes;
	}

	public void setDateTimeOffsetAttributes(List<DateTimeOffsetAttributeJPA> m_dateTimeOffsetAttributes) {
		this.m_dateTimeOffsetAttributes = m_dateTimeOffsetAttributes;
	}

	public List<StringAttributeJPA> getStringAttributes() {
		return m_stringAttributes;
	}

	public void setStringAttributes(List<StringAttributeJPA> m_stringAttributes) {
		this.m_stringAttributes = m_stringAttributes;
	}

	public List<ChecksumJPA> getChecksums() {
		return Checksums;
	}

	public void setChecksums(List<ChecksumJPA> checksums) {
		Checksums = checksums;
	}
	
	@Column
	public TimeRangeJPA getContentDate() {
		return ContentDate;
	}

	public void setContentDate(TimeRangeJPA contentDate) {
		ContentDate = contentDate;
	}
    
    

   
}

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
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<ChecksumJPA> Checksum;
    @Embedded
    private TimeRangeJPA ContentDate;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<StringAttributeJPA> StringAttributes;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<IntegerAttributeJPA> IntegerAttributes;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<DoubleAttributeJPA> DoubleAttributes;
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<DateTimeOffsetAttributeJPA> DateTimeOffsetAttributes;
        
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
	
	@Column
	public TimeRangeJPA getContentDate() {
		return ContentDate;
	}

	public void setContentDate(TimeRangeJPA contentDate) {
		ContentDate = contentDate;
	}
    
    

   
}

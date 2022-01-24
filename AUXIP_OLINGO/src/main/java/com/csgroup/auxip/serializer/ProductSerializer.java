/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.csgroup.auxip.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.codec.DecoderException;
import org.apache.olingo.commons.api.Constants;

import org.apache.olingo.server.api.serializer.SerializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csgroup.auxip.archive.ArchiveCreator;
import com.csgroup.auxip.model.jpa.Attribute;
import com.csgroup.auxip.model.jpa.Checksum;
import com.csgroup.auxip.model.jpa.DateTimeOffsetAttribute;
import com.csgroup.auxip.model.jpa.DoubleAttribute;
import com.csgroup.auxip.model.jpa.IntegerAttribute;
import com.csgroup.auxip.model.jpa.Product;
import com.csgroup.auxip.model.jpa.StringAttribute;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ProductSerializer {
	private static final Logger LOG = LoggerFactory.getLogger(ProductSerializer.class);

	private final boolean isIEEE754Compatible;
	private final boolean isODataMetadataNone;
	private final boolean isODataMetadataFull;

	protected static final String IO_EXCEPTION_TEXT = "An I/O exception occurred.";

	public ProductSerializer() {
		isIEEE754Compatible = true;
		isODataMetadataNone = true;
		isODataMetadataFull = true;
	}

	public void SerializeProductList(final List<Product> products, final OutputStream outputStream) throws SerializerException
	{
		SerializerException cachedException;
		try {
			JsonGenerator json = new JsonFactory().createGenerator(outputStream);
			json.writeStartObject();
			json.writeStringField("@odata.context", "$metadata#Products(Attributes())");
			json.writeFieldName(Constants.VALUE);
			json.writeStartArray();
			for (final Product entity : products) {
				json.writeStartObject();
				// json.writeStringField("@odata.mediaContentType", "text/plain");
				json.writeStringField("Id", entity.getId().toString());
				json.writeStringField("Name", entity.getName());
				json.writeStringField("ContentType", entity.getContentType());
				json.writeFieldName("ContentLength");
				json.writeNumber(entity.getContentLength());
				ZonedDateTime zdt = entity.getOriginDate().toInstant().atZone(ZoneId.of("Z"));
				json.writeStringField("OriginDate", zdt.format(DateTimeFormatter.ISO_DATE_TIME));
				zdt = entity.getPublicationDate().toInstant().atZone(ZoneId.of("Z"));
				json.writeStringField("PublicationDate", zdt.format(DateTimeFormatter.ISO_DATE_TIME));
				zdt = entity.getEvictionDate().toInstant().atZone(ZoneId.of("Z"));
				json.writeStringField("EvictionDate", zdt.format(DateTimeFormatter.ISO_DATE_TIME));
				json.writeFieldName("Checksum");
				json.writeStartArray();
				for (final Checksum checksum : entity.getChecksum())
				{
					json.writeStartObject();
					zdt = checksum.getChecksumDate().toInstant().atZone(ZoneId.of("Z"));
					json.writeStringField("ChecksumDate", zdt.format(DateTimeFormatter.ISO_DATE_TIME));
					json.writeStringField("Algorithm", checksum.getAlgorithm());
					json.writeStringField("Value",checksum.getValue());
					json.writeEndObject();
				}
				json.writeEndArray();
				json.writeFieldName("ContentDate");
				json.writeStartObject();
				zdt = entity.getContentDate().getStart().toInstant().atZone(ZoneId.of("Z"));
				json.writeStringField("Start", zdt.format(DateTimeFormatter.ISO_DATE_TIME));
				zdt = entity.getContentDate().getEnd().toInstant().atZone(ZoneId.of("Z"));
				json.writeStringField("Stop", zdt.format(DateTimeFormatter.ISO_DATE_TIME));
				json.writeEndObject();
				json.writeFieldName("StringAttributes");
				json.writeStartArray();
				for (final StringAttribute str_attr : entity.getStringAttributes())
				{
					json.writeStartObject();
					json.writeStringField("@odata.type", "#OData.CSC.StringAttribute");
					json.writeStringField("Name", str_attr.getName());
					json.writeStringField("ValueType", "String");
					json.writeStringField("Value", str_attr.getValue());
					json.writeEndObject();
				}
				json.writeEndArray();
				json.writeFieldName("DateTimeOffsetAttributes");
				json.writeStartArray();
				for (final DateTimeOffsetAttribute dt_attr : entity.getDateTimeOffsetAttributes())
				{
					json.writeStartObject();
					json.writeStringField("@odata.type", "#OData.CSC.DateTimeOffsetAttribute");
					json.writeStringField("Name", dt_attr.getName());
					json.writeStringField("ValueType", "DateTimeOffset");
					zdt = dt_attr.getValue().toInstant().atZone(ZoneId.of("Z"));
					json.writeStringField("Value", zdt.format(DateTimeFormatter.ISO_DATE_TIME));
					json.writeEndObject();
				}
				json.writeEndArray();
				json.writeFieldName("IntegerAttributes");
				json.writeStartArray();
				for (final IntegerAttribute int_attr : entity.getIntegerAttributes())
				{
					json.writeStartObject();
					json.writeStringField("@odata.type", "#OData.CSC.IntegerAttribute");
					json.writeStringField("Name", int_attr.getName());
					json.writeStringField("ValueType", "DateTimeOffset");
					json.writeNumberField("Value", int_attr.getValue());
					json.writeEndObject();
				}
				json.writeEndArray();
				json.writeFieldName("DoubleAttributes");
				json.writeStartArray();
				for (final DoubleAttribute do_attr : entity.getDoubleAttributes())
				{
					json.writeStartObject();
					json.writeStringField("@odata.type", "#OData.CSC.DoubleAttribute");
					json.writeStringField("Name", do_attr.getName());
					json.writeStringField("ValueType", "Double");
					json.writeNumberField("Value", do_attr.getValue());
					json.writeEndObject();
				}
				json.writeEndArray();
				json.writeEndObject();
			}
			json.writeEndArray();
			json.close();
		} catch (final IOException e) {
			LOG.error(e.getLocalizedMessage());
			cachedException =
					new SerializerException(IO_EXCEPTION_TEXT, e, SerializerException.MessageKeys.IO_EXCEPTION);
			throw cachedException;
		}
	}

}

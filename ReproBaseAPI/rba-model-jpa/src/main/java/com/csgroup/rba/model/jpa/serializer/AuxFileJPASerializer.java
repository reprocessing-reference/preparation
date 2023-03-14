package com.csgroup.rba.model.jpa.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csgroup.rba.model.jpa.AuxFileJPA;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class AuxFileJPASerializer {
	private static final Logger LOG = LoggerFactory.getLogger(AuxFileJPASerializer.class);

	private final boolean isIEEE754Compatible;
	private final boolean isODataMetadataNone;
	private final boolean isODataMetadataFull;

	protected static final String IO_EXCEPTION_TEXT = "An I/O exception occurred.";

	public AuxFileJPASerializer() {
		isIEEE754Compatible = true;
		isODataMetadataNone = true;
		isODataMetadataFull = true;
	}

	public void SerializeAuxFile(final AuxFileJPA aux, final OutputStream outputStream) throws Exception
	{
		Exception cachedException;
		try {
			JsonGenerator json = new JsonFactory().createGenerator(outputStream);
			json.writeStartObject();
			json.writeStringField("@odata.context", "$metadata#AuxFiles");
			json.writeStringField("Id", aux.getIdentifier().toString());
			json.writeStringField("FullName", aux.getFullName());
			json.writeStringField("Unit", aux.getUnit());
			json.writeStringField("Band", aux.getBand());
			json.writeStringField("Baseline", aux.getBaseline());
			json.writeStringField("IpfVersion", aux.getIpfVersion());
			json.writeStringField("ValidityStart", aux.getValidityStart().format(DateTimeFormatter.ISO_DATE_TIME));
			json.writeStringField("ValidityStop", aux.getValidityStop().format(DateTimeFormatter.ISO_DATE_TIME));
			json.writeStringField("SensingTimeApplicationStart", aux.getSensingTimeApplicationStart().format(DateTimeFormatter.ISO_DATE_TIME));
			json.writeStringField("SensingTimeApplicationStop", aux.getSensingTimeApplicationStop().format(DateTimeFormatter.ISO_DATE_TIME));
			json.writeStringField("AuxType@odata.bind", "AuxTypes('"+aux.getAuxType().getLongName()+"')");
			json.writeEndObject();
			json.close();
		} catch (final IOException e) {
			LOG.error(e.getLocalizedMessage());
			cachedException =
					new Exception(IO_EXCEPTION_TEXT, e);
			throw cachedException;
		}
	}
}

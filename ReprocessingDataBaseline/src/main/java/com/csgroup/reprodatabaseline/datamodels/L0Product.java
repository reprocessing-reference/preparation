package com.csgroup.reprodatabaseline.datamodels;

import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.joran.conditional.ElseAction;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.Transient;


/**
 * @author Naceur MESKINI
 */
@Entity(name = "l0_products")
public class L0Product {
	private static final Logger LOG = LoggerFactory.getLogger(L0Product.class);

    @Id
    private String name;
    private LocalDateTime validityStart;
    private LocalDateTime validityStop;

    public String getName() {
        return name;
    }

    public LocalDateTime getValidityStart() {
        return validityStart;
    }
    public LocalDateTime getValidityStop() {
        return validityStop;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setValidityStart(LocalDateTime validityStart) {
        this.validityStart = validityStart;
    }
    public void setValidityStop(LocalDateTime validityStop) {
        this.validityStop = validityStop;
    }
}

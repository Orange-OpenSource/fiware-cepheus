/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.time.Instant;
import java.util.List;

/**
 * Created by pborscia on 10/08/2015.
 */
@JacksonXmlRootElement(localName = "registerContextRequest")
public class RegisterContext {

    @JsonProperty(value = "contextRegistrations", required = true)
    @JacksonXmlElementWrapper(localName = "contextRegistrationList")
    @JacksonXmlProperty(localName = "contextRegistration")
    private List<ContextRegistration> contextRegistrationList;

    private String duration;

    private String registrationId;

    @JsonIgnore
    private Instant expirationDate;

    public RegisterContext() {
    }

    public RegisterContext(List<ContextRegistration> contextRegistrationList) {
        this.contextRegistrationList = contextRegistrationList;
    }

    public List<ContextRegistration> getContextRegistrationList() {
        return contextRegistrationList;
    }

    public void setContextRegistrationList(List<ContextRegistration> contextRegistrationList) {
        this.contextRegistrationList = contextRegistrationList;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    @Override
    public String toString() {
        return "RegisterContext{" +
                "contextRegistrationList=" + contextRegistrationList +
                ", duration='" + duration + '\'' +
                ", registrationId='" + registrationId + '\'' +
                '}';
    }
}

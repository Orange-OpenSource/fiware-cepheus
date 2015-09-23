/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.net.URI;
import java.util.List;

/**
 * Created by pborscia on 10/08/2015.
 */
public class ContextRegistration {

    @JsonProperty("entities")
    @JacksonXmlElementWrapper(localName = "entityIdList")
    @JacksonXmlProperty(localName = "entityId")
    private List<EntityId> entityIdList;

    @JsonProperty("attributes")
    @JacksonXmlElementWrapper(localName = "contextRegistrationAttributeList")
    @JacksonXmlProperty(localName = "contextRegistrationAttribute")
    private List<ContextRegistrationAttribute> contextRegistrationAttributeList;

    @JsonProperty("metadata")
    @JacksonXmlElementWrapper(localName = "registrationMetadataList")
    @JacksonXmlProperty(localName = "contextMetadata")
    private List<ContextMetadata> registrationMetadataList;

    @JsonProperty(required = true)
    private URI providingApplication;

    public ContextRegistration() {
    }

    public ContextRegistration(URI providingApplication) {
        this.providingApplication = providingApplication;
    }

    public List<EntityId> getEntityIdList() {
        return entityIdList;
    }

    public void setEntityIdList(List<EntityId> entityIdList) {
        this.entityIdList = entityIdList;
    }

    public List<ContextRegistrationAttribute> getContextRegistrationAttributeList() {
        return contextRegistrationAttributeList;
    }

    public void setContextRegistrationAttributeList(List<ContextRegistrationAttribute> contextRegistrationAttributeList) {
        this.contextRegistrationAttributeList = contextRegistrationAttributeList;
    }

    public List<ContextMetadata> getRegistrationMetadataList() {
        return registrationMetadataList;
    }

    public void setRegistrationMetadataList(List<ContextMetadata> registrationMetadataList) {
        this.registrationMetadataList = registrationMetadataList;
    }

    public URI getProvidingApplication() {
        return providingApplication;
    }

    public void setProvidingApplication(URI providingApplication) {
        this.providingApplication = providingApplication;
    }

    @Override
    public String toString() {
        return "ContextRegistration{" +
                "entityIdList=" + entityIdList +
                ", contextRegistrationAttributeList=" + contextRegistrationAttributeList +
                ", registrationMetadataList=" + registrationMetadataList +
                ", providingApplication=" + providingApplication +
                '}';
    }
}

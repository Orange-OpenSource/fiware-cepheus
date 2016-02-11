/*
 * Copyright (C) 2016 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Response to GET /contextEntities/{entityID}/attributes/{attributeName}
 */
public class ContextAttributeResponse {

    @JsonProperty("attributes")
    @JacksonXmlElementWrapper(localName = "contextAttributeList")
    @JacksonXmlProperty(localName = "contextAttribute")
    private List<ContextAttribute> contextAttributeList;

    private StatusCode statusCode;

    public ContextAttributeResponse() {
    }

    public List<ContextAttribute> getContextAttributeList() {
        return contextAttributeList;
    }

    public void setContextAttributeList(List<ContextAttribute> contextAttributeList) {
        this.contextAttributeList = contextAttributeList;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return "ContextAttributeResponse{" +
                "contextAttributeList=" + contextAttributeList +
                ", statusCode=" + statusCode +
                '}';
    }
}

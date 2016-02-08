/*
 * Copyright (C) 2016 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Request response for PUT /contextEntities/{entityID}
 */
@JacksonXmlRootElement(localName = "updateContextElementResponse")
public class UpdateContextElementResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    StatusCode errorCode;

    @JsonProperty("contextResponses")
    @JacksonXmlElementWrapper(localName = "contextResponseList")
    @JacksonXmlProperty(localName = "contextAttributeResponse")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ContextAttributeResponse> contextAttributeResponses;

    public UpdateContextElementResponse() {
    }

    public StatusCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(StatusCode errorCode) {
        this.errorCode = errorCode;
    }

    public List<ContextAttributeResponse> getContextAttributeResponses() {
        return contextAttributeResponses;
    }

    public void setContextAttributeResponses(List<ContextAttributeResponse> contextAttributeResponses) {
        this.contextAttributeResponses = contextAttributeResponses;
    }

    @Override
    public String toString() {
        return "UpdateContextElementResponse{" +
                "errorCode=" + errorCode +
                ", contextAttributeResponses=" + contextAttributeResponses +
                '}';
    }
}

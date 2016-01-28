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
 * Request message for PUT /contextEntities/{entityID}
 */
@JacksonXmlRootElement(localName = "updateContextElementRequest")
public class UpdateContextElement {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String attributeDomainName;

    @JsonProperty(value = "attributes")
    @JacksonXmlElementWrapper(localName="contextAttributeList")
    @JacksonXmlProperty(localName="contextAttribute")
    List<ContextAttribute> contextAttributes;

    public UpdateContextElement() {
    }

    public String getAttributeDomainName() {
        return attributeDomainName;
    }

    public void setAttributeDomainName(String attributeDomainName) {
        this.attributeDomainName = attributeDomainName;
    }

    public List<ContextAttribute> getContextAttributes() {
        return contextAttributes;
    }

    public void setContextAttributes(List<ContextAttribute> contextAttributes) {
        this.contextAttributes = contextAttributes;
    }

    @Override
    public String toString() {
        return "UpdateContextElement{" +
                "attributeDomainName='" + attributeDomainName + '\'' +
                ", contextAttributes=" + contextAttributes +
                '}';
    }
}

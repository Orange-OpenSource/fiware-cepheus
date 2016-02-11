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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Request message for POST /contextEntities/{entityID}
 */
@JacksonXmlRootElement(localName = "appendContextElementRequest")
public class AppendContextElement {

    @JsonProperty(value = "attributes")
    @JacksonXmlElementWrapper(localName = "contextAttributeList")
    @JacksonXmlProperty(localName = "contextAttribute")
    private List<ContextAttribute> attributeList;

    public AppendContextElement() {
    }

    public AppendContextElement(List<ContextAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    public List<ContextAttribute> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<ContextAttribute> attributeList) {
        this.attributeList = attributeList;
    }

    @Override public String toString() {
        return "AppendContextElement{" +
                "attributeList=" + attributeList +
                '}';
    }
}

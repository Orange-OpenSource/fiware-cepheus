/*
 * Copyright (C) 2015 Orange
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Context Attribute
 */
public class ContextAttribute {


    private String name;

    private String type;

    @JacksonXmlProperty(localName = "contextValue")
    private Object value;

    @JsonProperty("metadatas")
    @JacksonXmlElementWrapper(localName = "metadata")
    @JacksonXmlProperty(localName = "contextMetadata")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ContextMetadata> metadata;

    public ContextAttribute() {
    }

    public ContextAttribute(String name, String type, Object value)
    {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object get(String name) {
        return value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public List<ContextMetadata> getMetadata() {
        if (metadata == null) {
            return Collections.emptyList();
        }
        return metadata;
    }

    public void setMetadata(List<ContextMetadata> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(ContextMetadata metadata) {
        if (this.metadata == null) {
            this.metadata = new LinkedList<>();
        }
        this.metadata.add(metadata);
    }

    @Override
    public String toString() {
        return "ContextAttribute{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value=" + value +
                ", metadata=" + metadata +
                '}';
    }
}

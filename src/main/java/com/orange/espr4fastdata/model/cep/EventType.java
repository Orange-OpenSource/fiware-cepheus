/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.cep;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class EventType {

    private String id;
    private String type;
    private boolean isPattern;
    private Set<Attribute> attributes; // using a set to handle equals on unordered attributes

    public EventType() {
    }

    public EventType(String id, String type, boolean isPattern) {
        this.id = id;
        this.type = type;
        this.isPattern = isPattern;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Set<Attribute> getAttributes() {
        if (attributes == null)
        {
            attributes = new HashSet<>();
        }
        return attributes;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("isPattern")
    public boolean isPattern() {
        return isPattern;
    }

    public void setIsPattern(Boolean isPattern) {
        this.isPattern = isPattern;
    }

    @JsonProperty("attributes")
    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof EventType))
            return false;

        EventType eventType = (EventType) o;

        if (isPattern != eventType.isPattern)
            return false;
        if (id != null ? !id.equals(eventType.id) : eventType.id != null)
            return false;
        if (type != null ? !type.equals(eventType.type) : eventType.type != null)
            return false;
        return !(attributes != null ? !attributes.equals(eventType.attributes) : eventType.attributes != null);
    }

    public void addAttribute(Attribute attribute) {
        if (attributes == null) {
            attributes = new HashSet<>();
        }
        attributes.add(attribute);
    }
}

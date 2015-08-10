/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by pborscia on 10/08/2015.
 */
public class ContextRegistrationAttribute {

    @JsonProperty(required = true)
    private String name;

    private String type;

    @JsonProperty(required = true)
    private Boolean isDomain;

    private List<ContextMetadata> metadata;

    public ContextRegistrationAttribute() {
    }

    public ContextRegistrationAttribute(String name, Boolean isDomain) {
        this.name = name;
        this.isDomain = isDomain;
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

    public Boolean getIsDomain() {
        return isDomain;
    }

    public void setIsDomain(Boolean isDomain) {
        this.isDomain = isDomain;
    }

    public List<ContextMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<ContextMetadata> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "ContextRegistrationAttribute{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", isDomain=" + isDomain +
                ", metadata=" + metadata +
                '}';
    }
}

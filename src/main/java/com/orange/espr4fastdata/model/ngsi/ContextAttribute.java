/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.ngsi;

import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
public class ContextAttribute {

    private String name;
    private String type;
    private String contextValue;
    private List<ContextMetadata> metadata;

    public ContextAttribute() {
    }

    public ContextAttribute(String name, String type, String contextValue) {
        this.name = name;
        this.type = type;
        this.contextValue = contextValue;
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

    public String getContextValue() {
        return contextValue;
    }

    public void setContextValue(String contextValue) {
        this.contextValue = contextValue;
    }

    public List<ContextMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<ContextMetadata> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "ContextAttribute{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", contextValue='" + contextValue + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}

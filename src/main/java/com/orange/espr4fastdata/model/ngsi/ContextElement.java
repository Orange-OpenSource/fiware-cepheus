/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.ngsi;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
public class ContextElement {
    @JsonUnwrapped
    private EntityId entityId;
    private String attributeDomainName;

    @JsonProperty("attributes")
    private List<ContextAttribute> contextAttributeList;
    private List<ContextMetadata> contextMetadataList;

    public ContextElement() {
    }

    public EntityId getEntityId() {
        return entityId;
    }

    public void setEntityId(EntityId entityId) {
        this.entityId = entityId;
    }

    public String getAttributeDomainName() {
        return attributeDomainName;
    }

    public void setAttributeDomainName(String attributeDomainName) {
        this.attributeDomainName = attributeDomainName;
    }

    public List<ContextAttribute> getContextAttributeList() {
        return contextAttributeList;
    }

    public void setContextAttributeList(List<ContextAttribute> contextAttributeList) {
        this.contextAttributeList = contextAttributeList;
    }

    public List<ContextMetadata> getContextMetadataList() {
        return contextMetadataList;
    }

    public void setContextMetadataList(List<ContextMetadata> contextMetadataList) {
        this.contextMetadataList = contextMetadataList;
    }

    @Override
    public String toString() {
        return "ContextElement{" +
                "entityId=" + entityId +
                ", attributeDomainName='" + attributeDomainName + '\'' +
                ", contextAttributeList=" + contextAttributeList +
                ", contextMetadataList=" + contextMetadataList +
                '}';
    }
}

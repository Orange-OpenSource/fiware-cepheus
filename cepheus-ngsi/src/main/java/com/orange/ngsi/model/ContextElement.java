/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.xml.bind.annotation.XmlElement;
import java.util.Collections;
import java.util.List;

/**
 * Context Entity
 */
public class ContextElement {

    private EntityId entityId;

    @JacksonXmlElementWrapper(localName = "contextAttributeList")
    @JacksonXmlProperty(localName = "contextAttribute")
    private List<ContextAttribute> contextAttributeList;

    public ContextElement() {
    }

    public EntityId getEntityId() {
        return entityId;
    }

    public void setEntityId(EntityId entityId) {
        this.entityId = entityId;
    }

    public List<ContextAttribute> getContextAttributeList() {
        if (contextAttributeList == null) {
            return Collections.emptyList();
        }
        return contextAttributeList;
    }

    public void setContextAttributeList(List<ContextAttribute> contextAttributeList) {
        this.contextAttributeList = contextAttributeList;
    }

    @Override
    public String toString() {
        return "ContextElement{" +
                "entityId=" + entityId +
                ", contextAttributeList=" + contextAttributeList +
                '}';
    }
}

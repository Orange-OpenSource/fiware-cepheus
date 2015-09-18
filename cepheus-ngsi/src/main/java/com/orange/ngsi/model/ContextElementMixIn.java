package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.Collections;
import java.util.List;

/**
 * Created by pborscia on 16/09/2015.
 */
public class ContextElementMixIn {

    @JsonUnwrapped
    EntityId entityId;

    @JsonProperty("attributes") List<ContextAttribute> contextAttributeList;

    public ContextElementMixIn() {
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


}

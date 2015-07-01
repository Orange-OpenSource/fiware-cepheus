package com.orange.espr4fastdata.model.ngsi;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
public class ContextElement {
    @JsonUnwrapped
    private EntityId entityId;
    private String attributeDomainName;
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

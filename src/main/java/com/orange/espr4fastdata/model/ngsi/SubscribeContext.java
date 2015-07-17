package com.orange.espr4fastdata.model.ngsi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.time.Period;
import java.util.List;

/**
 * Created by pborscia on 17/07/2015.
 */
public class SubscribeContext {

    @JsonProperty("entities")
    List<EntityId> entityIdList;

    @JsonProperty("attributes")
    List<String> attributeList;

    URI reference;

    String duration;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Restriction restriction;

    @JsonProperty("notifyConditions")
    List<NotifyCondition> notifyConditionList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String throttling;

    public SubscribeContext() {
    }

    public List<EntityId> getEntityIdList() {
        return entityIdList;
    }

    public void setEntityIdList(List<EntityId> entityIdList) {
        this.entityIdList = entityIdList;
    }

    public List<String> getAttributeList() {
        return attributeList;
    }

    public void setAttributeList(List<String> attributeList) {
        this.attributeList = attributeList;
    }

    public URI getReference() {
        return reference;
    }

    public void setReference(URI reference) {
        this.reference = reference;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    public List<NotifyCondition> getNotifyConditionList() {
        return notifyConditionList;
    }

    public void setNotifyConditionList(List<NotifyCondition> notifyConditionList) {
        this.notifyConditionList = notifyConditionList;
    }

    public String getThrottling() {
        return throttling;
    }

    public void setThrottling(String throttling) {
        this.throttling = throttling;
    }
}

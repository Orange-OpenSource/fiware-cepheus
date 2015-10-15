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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * UpdateContextSubscription
 */
@JacksonXmlRootElement(localName = "updateContextSubscriptionRequest")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateContextSubscription {

    private String subscriptionId;

    String duration;

    Restriction restriction;

    @JacksonXmlElementWrapper(localName = "notifyConditionList")
    @JacksonXmlProperty(localName = "notifyCondition")
    List<NotifyCondition> notifyConditions;

    String throttling;

    public UpdateContextSubscription() {
    }

    public UpdateContextSubscription(String subscriptionId, String duration, List<NotifyCondition> notifyConditions, String throttling) {
        this.subscriptionId = subscriptionId;
        this.notifyConditions = notifyConditions;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
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

    public List<NotifyCondition> getNotifyConditions() {
        return notifyConditions;
    }

    public void setNotifyConditions(List<NotifyCondition> notifyConditions) {
        this.notifyConditions = notifyConditions;
    }

    public String getThrottling() {
        return throttling;
    }

    public void setThrottling(String throttling) {
        this.throttling = throttling;
    }

    @Override
    public String toString() {
        return "UpdateContextSubscription{" +
                "subscriptionId='" + subscriptionId + '\'' +
                ", duration='" + duration + '\'' +
                ", restriction=" + restriction +
                ", notifyConditions=" + notifyConditions +
                ", throttling='" + throttling + '\'' +
                '}';
    }
}

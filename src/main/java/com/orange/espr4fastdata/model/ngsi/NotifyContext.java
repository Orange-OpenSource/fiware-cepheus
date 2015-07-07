/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.ngsi;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
public class NotifyContext {

    @JsonProperty(required = true)
    private String subscriptionId;
    @JsonProperty(required = true)
    private URI originator;

    private List<ContextElementResponse> contextElementResponseList;

    public NotifyContext() {
    }

    public NotifyContext(String subscriptionId, URI originator) {
        this.subscriptionId = subscriptionId;
        this.originator = originator;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public URI getOriginator() {
        return originator;
    }

    public void setOriginator(URI originator) {
        this.originator = originator;
    }

    public List<ContextElementResponse> getContextElementResponseList() {
        return contextElementResponseList;
    }

    public void setContextElementResponseList(List<ContextElementResponse> contextElementResponseList) {
        this.contextElementResponseList = contextElementResponseList;
    }

    @Override
    public String toString() {
        return "NotifyContext{" +
                "subscriptionId='" + subscriptionId + '\'' +
                ", originator=" + originator +
                ", contextElementResponseList=" + contextElementResponseList +
                '}';
    }
}

package com.orange.newespr4fastdata.model;

import java.net.URI;
import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
public class NotifyContext {

    private String subscriptionId;
    private URI originator;
    private List<ContextElementResponse> contextElementResponseList;

    public NotifyContext() {
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

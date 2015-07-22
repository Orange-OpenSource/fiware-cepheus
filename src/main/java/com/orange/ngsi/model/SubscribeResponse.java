package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by pborscia on 17/07/2015.
 */
public class SubscribeResponse {

    String subscriptionId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String duration;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String throttling;

    public SubscribeResponse() {
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

    public String getThrottling() {
        return throttling;
    }

    public void setThrottling(String throttling) {
        this.throttling = throttling;
    }
}

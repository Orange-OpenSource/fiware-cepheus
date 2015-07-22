package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by pborscia on 17/07/2015.
 */
public class SubscribeError {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String subscriptionId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    StatusCode errorCode;

    public SubscribeError() {
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public StatusCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(StatusCode errorCode) {
        this.errorCode = errorCode;
    }
}

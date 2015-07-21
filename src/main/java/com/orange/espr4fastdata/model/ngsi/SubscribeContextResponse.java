package com.orange.espr4fastdata.model.ngsi;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

/**
 * Created by pborscia on 17/07/2015.
 */
public class SubscribeContextResponse {

    SubscribeResponse subscribeResponse;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    SubscribeError subscribeError;

    public SubscribeContextResponse() {
    }

    public SubscribeContextResponse(SubscribeResponse subscribeResponse, SubscribeError subscribeError) {
        this.subscribeResponse = subscribeResponse;
        this.subscribeError = subscribeError;
    }

    public SubscribeResponse getSubscribeResponse() {
        return subscribeResponse;
    }

    public void setSubscribeResponse(SubscribeResponse subscribeResponse) {
        this.subscribeResponse = subscribeResponse;
    }

    public SubscribeError getSubscribeError() {
        return subscribeError;
    }

    public void setSubscribeError(SubscribeError subscribeError) {
        this.subscribeError = subscribeError;
    }
}

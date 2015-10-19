/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * UpdateContextSubscriptionResponse
 */
@JacksonXmlRootElement(localName = "updateContextSubscriptionResponse")
public class UpdateContextSubscriptionResponse {

    private SubscribeResponse subscribeResponse;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private SubscribeError subscribeError;

    public UpdateContextSubscriptionResponse() {
    }

    public UpdateContextSubscriptionResponse(SubscribeResponse subscribeResponse) {
        this.subscribeResponse = subscribeResponse;
    }

    public UpdateContextSubscriptionResponse(SubscribeError subscribeError) {
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

    @Override
    public String toString() {
        return "UpdateContextSubscriptionResponse{" +
                "subscribeResponse=" + subscribeResponse +
                ", subscribeError=" + subscribeError +
                '}';
    }
}

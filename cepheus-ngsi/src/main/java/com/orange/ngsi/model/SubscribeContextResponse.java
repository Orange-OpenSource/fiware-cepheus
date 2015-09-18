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
 * Created by pborscia on 17/07/2015.
 */
@JacksonXmlRootElement(localName = "subscribeContextResponse")
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

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 03/07/2015.
 */
public class NotifyContextResponse {

    private StatusCode responseCode;

    public NotifyContextResponse() {
    }

    public NotifyContextResponse(StatusCode responseCode) {
        this.responseCode = responseCode;
    }

    public StatusCode getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(StatusCode responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "NotifyContextResponse{" +
                "responseCode=" + responseCode +
                '}';
    }
}

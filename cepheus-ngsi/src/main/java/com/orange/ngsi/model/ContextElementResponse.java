/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import javax.validation.constraints.NotNull;

/**
 * Created by pborscia on 04/06/2015.
 */
public class ContextElementResponse {

    @NotNull
    private ContextElement contextElement;

    private StatusCode statusCode;

    public ContextElementResponse() {
    }

    public ContextElementResponse(ContextElement contextElement, StatusCode statusCode) {
        this.contextElement = contextElement;
        this.statusCode = statusCode;
    }

    public ContextElement getContextElement() {
        return contextElement;
    }

    public void setContextElement(ContextElement contextElement) {
        this.contextElement = contextElement;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }
}

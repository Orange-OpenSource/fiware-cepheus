package com.orange.espr4fastdata.model.ngsi;

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

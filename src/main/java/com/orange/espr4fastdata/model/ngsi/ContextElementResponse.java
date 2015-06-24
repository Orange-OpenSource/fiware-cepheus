package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 04/06/2015.
 */
public class ContextElementResponse {
    private ContextElement contextElement;
    private StatusCode statusCode;

    public ContextElementResponse() {
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

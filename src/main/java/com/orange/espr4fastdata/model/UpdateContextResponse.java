package com.orange.espr4fastdata.model;

import java.util.List;

/**
 * Created by pborscia on 05/06/2015.
 */
public class UpdateContextResponse {
    StatusCode errorCode;
    List<ContextElementResponse> contextElementResponses;

    public UpdateContextResponse() {
    }

    public StatusCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(StatusCode errorCode) {
        this.errorCode = errorCode;
    }

    public List<ContextElementResponse> getContextElementResponses() {
        return contextElementResponses;
    }

    public void setContextElementResponses(List<ContextElementResponse> contextElementResponses) {
        this.contextElementResponses = contextElementResponses;
    }

    @Override
    public String toString() {
        return "UpdateContextResponse{" +
                "errorCode=" + errorCode +
                ", contextElementResponses=" + contextElementResponses +
                '}';
    }
}

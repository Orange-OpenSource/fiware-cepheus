/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by pborscia on 11/08/2015.
 */
public class QueryContextResponse {

    @JsonProperty("contextResponses")
    List<ContextElementResponse> contextElementResponses;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private StatusCode errorCode;

    public QueryContextResponse() {
    }

    public List<ContextElementResponse> getContextElementResponses() {
        return contextElementResponses;
    }

    public void setContextElementResponses(List<ContextElementResponse> contextElementResponses) {
        this.contextElementResponses = contextElementResponses;
    }

    public StatusCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(StatusCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "QueryContextResponse{" +
                "contextElementResponses=" + contextElementResponses +
                ", errorCode=" + errorCode +
                '}';
    }
}

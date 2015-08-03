/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by pborscia on 05/06/2015.
 */
public class UpdateContextResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
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

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by pborscia on 10/08/2015.
 */
public class RegisterContextResponse {

    private String duration;

    @JsonProperty(required = true)
    private String registrationId;

    private StatusCode errorCode;

    public RegisterContextResponse() {
    }

    public RegisterContextResponse(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public StatusCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(StatusCode errorCode) {
        this.errorCode = errorCode;
    }
}

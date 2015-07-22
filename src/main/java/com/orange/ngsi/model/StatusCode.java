/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by pborscia on 04/06/2015.
 */
public class StatusCode {


    private String code;

    private String reasonPhrase;

    private String detail;

    public StatusCode() {
    }

    @JsonIgnore
    public StatusCode(CodeEnum code, String... paramDetail) {
        this.code = code.getLabel();
        this.reasonPhrase = code.getShortPhrase();
        this.detail = String.format(code.getLongPhrase(), paramDetail);

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


}

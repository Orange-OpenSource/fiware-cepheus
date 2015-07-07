/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 04/06/2015.
 */
public enum StatusCode {

    CODE_200("200","OK","All under control"),
    CODE_400("400","BAD REQUEST","BAD REQUEST"),
    CODE_403("403","Forbidden", "Request is not allowed"),
    CODE_404("404","ContextElement not found","The ContextElement requested is not found"),
    CODE_470("470","Subscription ID not found","The subscription ID specified does not correspond to an active subscription"),
    CODE_471("471","Missing parameter","A parameter is missing in the request"),
    CODE_472("472","Invalid parameter","A parameter is not valid/allowed in the request"),
    CODE_473("473","Error in metadata","There is e generic error in the metadata"),
    CODE_480("480","Regular Expression for EntityId not allowed","A regular expression for EntityId is not allowed by the receiver"),
    CODE_481("481","Entity Type required","The EntityType is required by the receiver"),
    CODE_482("482","AttributeList required","The AttributList is required"),
    CODE_500("500","Receiver internal error","An unknown error at the receiver has occured");

    private String code;
    private String reason;
    private String detail;

    StatusCode(String code, String reason, String detail) {
        this.code = code;
        this.reason = reason;
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}

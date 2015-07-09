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
public class StatusCode {


    private String code;

    private String reasonPhrase;

    private String detail;


    public StatusCode(CodeEnum code, String... paramDetail) {
        this.code = code.getLabel();
        this.reasonPhrase = code.getShortPhrase();
        this.detail = String.format(code.getLongPhrase(), paramDetail);

        /*switch (code){
            case CODE_200 : this.reasonPhrase = "OK";
                            this.detail = "All is OK";
                            break;

            case CODE_400 : this.reasonPhrase = "Bad request";
                            this.detail = "BAD REQUEST";
                            break;

            case CODE_403 : this.reasonPhrase = "Forbidden";
                            this.detail = "Request is not allowed";
                            break;

            case CODE_404 : this.reasonPhrase = "ContextElement not found";
                            this.detail = String.format("The ContextElement requested %s is not found", paramDetail[0]);
                            break;

            case CODE_470 : this.reasonPhrase = "Subscription ID not found";
                            this.detail = String.format("The subscription ID specified %s does not correspond to an active subscription", paramDetail[0]);
                            break;

            case CODE_471 : this.reasonPhrase = "Missing parameter";
                            this.detail = String.format("The parameter %s of type %s is missing in the request", paramDetail[0], paramDetail[1]);
                            break;

            case CODE_472 : this.reasonPhrase = "Invalid parameter";
                            this.detail = String.format("A parameter %s is not valid/allowed in the request", paramDetail[0]);
                            break;

            case CODE_473 : this.reasonPhrase = "Error in metadata";
                            this.detail = "There is a generic error in the metadata";
                            break;

            case CODE_480 : this.reasonPhrase = "Regular Expression for EntityId not allowed";
                            this.detail = String.format("A regular expression %s for EntityId %s is not allowed by the receiver", paramDetail[0], paramDetail[1]);
                            break;

            case CODE_481 : this.reasonPhrase = "Entity Type required";
                            this.detail = String.format("The EntityType %s is required by the receiver", paramDetail[0]);
                            break;

            case CODE_482 : this.reasonPhrase = "AttributeList required";
                            this.detail = "The AttributList is required";
                            break;

            case CODE_500 : this.reasonPhrase = "Receiver internal error";
                            this.detail = "An unknown error at the receiver has occured";
                            break;

            default :   this.reasonPhrase = "Receiver internal error";
                        this.detail = "An unknown error at the receiver has occured";
                        break;

        }*/
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

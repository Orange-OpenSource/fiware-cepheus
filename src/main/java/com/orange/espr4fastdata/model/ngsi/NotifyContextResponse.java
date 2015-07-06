package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 03/07/2015.
 */
public class NotifyContextResponse {

    private StatusCode ResponseCode;

    public NotifyContextResponse() {
    }

    public NotifyContextResponse(StatusCode responseCode) {
        ResponseCode = responseCode;
    }

    public StatusCode getResponseCode() {
        return ResponseCode;
    }

    public void setResponseCode(StatusCode responseCode) {
        ResponseCode = responseCode;
    }

    @Override
    public String toString() {
        return "NotifyContextResponse{" +
                "ResponseCode=" + ResponseCode +
                '}';
    }
}

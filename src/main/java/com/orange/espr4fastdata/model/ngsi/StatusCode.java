package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 04/06/2015.
 */
public enum StatusCode {

    CODE_200("200","OK","All under control"), CODE_400("400","BAD REQUEST","BAD REQUEST");

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

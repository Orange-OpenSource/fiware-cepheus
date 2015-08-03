package com.orange.ngsi.model;

/**
 * Created by pborscia on 17/07/2015.
 */
public enum NotifyConditionEnum {

    ONTIMEINTERVAL("ONTIMEINTERVAL"), ONVALUE("ONVALUE"), ONCHANGE("ONCHANGE");

    String label;

    NotifyConditionEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

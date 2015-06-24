package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 05/06/2015.
 */
public enum UpdateAction {

    UPDATE("update"),APPEND("append"),DELETE("delete");

    private String label;

    UpdateAction(String label) {
        this.label=label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

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

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by pborscia on 17/07/2015.
 */
public class OperationScope {

    @JacksonXmlProperty(localName = "scopeType")
    String type;

    @JacksonXmlProperty(localName = "scopeValue")
    Object value;

    public OperationScope() {
    }

    public OperationScope(String type, Object scopeValue) {
        this.type = type;
        this.value = scopeValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "OperationScope{" +
                "type='" + type + '\'' +
                ", value=" + value +
                '}';
    }
}

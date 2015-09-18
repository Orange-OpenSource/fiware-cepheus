/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

/**
 * Created by pborscia on 17/07/2015.
 */
public class Restriction {

    String attributeExpression;

    @JacksonXmlElementWrapper(localName = "scopes")
    @JacksonXmlProperty(localName = "operationScope")
    List<OperationScope> scopes;

    public Restriction() {
    }

    public String getAttributeExpression() {
        return attributeExpression;
    }

    public void setAttributeExpression(String attributeExpression) {
        this.attributeExpression = attributeExpression;
    }

    public List<OperationScope> getScopes() {
        return scopes;
    }

    public void setScopes(List<OperationScope> scopes) {
        this.scopes = scopes;
    }
}

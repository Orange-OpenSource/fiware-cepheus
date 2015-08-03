/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import java.util.List;

/**
 * Created by pborscia on 17/07/2015.
 */
public class Restriction {

    String attributeExpression;

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

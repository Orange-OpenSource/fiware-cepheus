package com.orange.espr4fastdata.model.ngsi;

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

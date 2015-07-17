package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 17/07/2015.
 */
public class OperationScope {

    String scopeType;

    Object scopeValue;

    public OperationScope() {
    }

    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
    }

    public Object getScopeValue() {
        return scopeValue;
    }

    public void setScopeValue(Object scopeValue) {
        this.scopeValue = scopeValue;
    }
}

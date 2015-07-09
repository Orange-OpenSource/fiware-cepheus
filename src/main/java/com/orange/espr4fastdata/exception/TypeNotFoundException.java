package com.orange.espr4fastdata.exception;

/**
 * Created by pborscia on 09/07/2015.
 */
public class TypeNotFoundException extends Exception {


    private final String typeName;

    private final Object response;

    public TypeNotFoundException(String typeName, Object response) {
        super("");
        this.typeName = typeName;
        this.response = response;

    }

    @Override
    public String getMessage() {
        return "The type '" + this.typeName + "' is not present in the configuration level.";
    }

    public String getTypeName() {
        return typeName;
    }

    public Object getResponse() {
        return response;
    }
}

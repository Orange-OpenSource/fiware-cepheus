/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.exception;

/**
 * Created by pborscia on 08/07/2015.
 */
public class UnsupportedOperationException extends Exception {

    private final String operationName;

    public UnsupportedOperationException(String operationName) {
        super("");
        this.operationName = operationName;
    }


    @Override
    public String getMessage() {
        return "This " + this.operationName + " operation is not supported.";
    }

    public String getOperationName() {
        return operationName;
    }

}

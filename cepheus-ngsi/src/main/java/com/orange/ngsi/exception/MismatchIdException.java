/*
 * Copyright (C) 2016 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.exception;

/**
 * Exception triggered when a REST ID parameter mismatches with the ID in the request body
 */
public class MismatchIdException extends Exception {

    private final String parameterId;

    private final String bodyId;

    public MismatchIdException(String parameterId, String bodyId) {
        this.parameterId = parameterId;
        this.bodyId = bodyId;
    }

    public String getMessage() {
        return "mismatch id between parameter " + parameterId + " and body " + bodyId;
    }

    public String getParameterId() {
        return parameterId;
    }

    public String getBodyId() {
        return bodyId;
    }
}

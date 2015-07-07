/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.exception;

/**
 * Created by pborscia on 04/06/2015.
 */
public class EventTypeNotFoundException extends Exception {

    private final String message = "The event type does not exist.";

    public EventTypeNotFoundException() {
    }

    public EventTypeNotFoundException(String message) {
        message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

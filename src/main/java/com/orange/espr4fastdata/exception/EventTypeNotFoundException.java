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

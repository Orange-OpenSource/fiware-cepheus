package com.orange.newespr4fastdata.exception;

/**
 * Created by pborscia on 04/06/2015.
 */
public class EventTypeNotFoundException extends Exception {

    private String message = "The event type does not exist.";

    public EventTypeNotFoundException() {}

    public EventTypeNotFoundException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

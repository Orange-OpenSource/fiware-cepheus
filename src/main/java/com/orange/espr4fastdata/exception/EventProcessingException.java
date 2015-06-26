package com.orange.espr4fastdata.exception;

/**
 * Handle error on event processing
 */
public class EventProcessingException extends Exception {

    public EventProcessingException(String message) {
        super(message);
    }

    public EventProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}

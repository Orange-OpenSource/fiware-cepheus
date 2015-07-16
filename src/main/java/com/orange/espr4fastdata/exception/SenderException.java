package com.orange.espr4fastdata.exception;

/**
 * Handle errors on sender
 */
public class SenderException extends Exception {

    public SenderException(String message) {
        super(message);
    }

    public SenderException(String message, Throwable cause) {
        super(message, cause);
    }
}

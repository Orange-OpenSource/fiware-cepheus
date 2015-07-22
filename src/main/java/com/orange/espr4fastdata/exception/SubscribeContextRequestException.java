package com.orange.espr4fastdata.exception;

/**
 * Handle errors on sender
 */
public class SubscribeContextRequestException extends Exception {

    public SubscribeContextRequestException(String message) {
        super(message);
    }

    public SubscribeContextRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

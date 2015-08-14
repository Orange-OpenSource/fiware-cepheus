package com.orange.cepheus.broker.exception;


/**
 * Exception that can occur during a r
 */
public class MissingRemoteBrokerException extends Exception {
    public MissingRemoteBrokerException(String message) {
        super(message);
    }
}

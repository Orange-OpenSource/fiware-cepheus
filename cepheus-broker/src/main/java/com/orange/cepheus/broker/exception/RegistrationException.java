/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.exception;

/**
 * Exception that can occur during a registration
 */
public class RegistrationException extends Exception {
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}

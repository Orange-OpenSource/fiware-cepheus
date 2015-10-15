/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.model;

import com.orange.ngsi.model.RegisterContext;

import java.time.Instant;

/**
 * Registration model
 */
public class Registration {

    Instant expirationDate;

    RegisterContext registerContext;

    public Registration() {
    }

    public Registration(Instant expirationDate, RegisterContext registerContext) {
        this.expirationDate = expirationDate;
        this.registerContext = registerContext;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public RegisterContext getRegisterContext() {
        return registerContext;
    }

    public void setRegisterContext(RegisterContext registerContext) {
        this.registerContext = registerContext;
    }
}

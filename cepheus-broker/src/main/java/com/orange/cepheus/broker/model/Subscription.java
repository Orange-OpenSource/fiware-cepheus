/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.model;

import com.orange.ngsi.model.SubscribeContext;

import java.time.Instant;

/**
 * Created by pborscia on 13/10/2015.
 */
public class Subscription {

    String subscriptionId;

    Instant expirationDate;

    SubscribeContext subscribeContext;

    public Subscription() {
    }

    public Subscription(String subscriptionId, Instant expirationDate, SubscribeContext subscribeContext) {
        this.subscriptionId = subscriptionId;
        this.expirationDate = expirationDate;
        this.subscribeContext = subscribeContext;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public SubscribeContext getSubscribeContext() {
        return subscribeContext;
    }

    public void setSubscribeContext(SubscribeContext subscribeContext) {
        this.subscribeContext = subscribeContext;
    }
}

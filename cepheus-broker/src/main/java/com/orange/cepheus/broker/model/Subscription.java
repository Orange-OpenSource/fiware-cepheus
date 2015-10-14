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

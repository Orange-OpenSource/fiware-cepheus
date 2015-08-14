/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.cepheus.broker.exception.SubscriptionException;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.UnsubscribeContext;
import com.orange.ngsi.model.UpdateContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles subscriptions.
 */
@Component
public class Subscriptions {

    private Map<String, SubscribeContext> subscriptions = new ConcurrentHashMap<>();

    /**
     * Add or updates a subscription.
     * @param subscribeContext
     * @return the subscriptionId
     * @throws SubscriptionException
     */
    public String updateSubscription(SubscribeContext subscribeContext) throws SubscriptionException {
        throw new NotImplementedException();
    }

    /**
     * Removes a subscription.
     * @param unsubscribeContext
     * @return
     */
    public boolean deleteSubscription(UnsubscribeContext unsubscribeContext) {
        throw new NotImplementedException();
    }

    /**
     * Send notifyContext asynchronously to all subscribers matching the updateContext.
     * @param updateContext
     */
    public void notifySubscribersOnUpdate(UpdateContext updateContext) {
        throw new NotImplementedException();
    }

    /**
     * Removed all expired subscriptions every minute.
     */
    @Scheduled(fixedDelay = 60000)
    public void purgeExpiredSubscriptions() {
        throw new NotImplementedException();
    }
}

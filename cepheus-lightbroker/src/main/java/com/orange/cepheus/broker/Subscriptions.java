/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.cepheus.broker.exception.RegistrationException;
import com.orange.cepheus.broker.exception.SubscriptionException;
import com.orange.ngsi.model.EntityId;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.UnsubscribeContext;
import com.orange.ngsi.model.UpdateContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Handles subscriptions.
 */
@Component
public class Subscriptions {

    private Map<String, SubscribeContext> subscriptions = new ConcurrentHashMap<>();

    /**
     * Cache of compiled patterns
     */
    private Map<String, Pattern> cachedPatterns = new ConcurrentHashMap<>();

    /**
     * Add a subscription.
     * @param subscribeContext
     * @return the subscriptionId
     * @throws SubscriptionException
     */
    public String addSubscription(SubscribeContext subscribeContext) throws SubscriptionException {
        //if duration is not present, then lb set duration to P1M
        Duration duration = convertDuration(subscribeContext.getDuration());
        if (duration.isNegative()) {
            throw new SubscriptionException("negative duration is not allowed", new Throwable());
        }
        if (duration.isZero()) {
            duration = convertDuration("P1M");
        }

        // Compile all entity patterns now to check for conformance (result is cached for later use)
        try {
            subscribeContext.getEntityIdList().forEach(this::getPattern);
        } catch (PatternSyntaxException e) {
            throw new SubscriptionException("bad pattern", e);
        }

        // Generate a subscription id
        String subscriptionId = UUID.randomUUID().toString();

        //set the expiration date
        subscribeContext.setExpirationDate(Instant.now().plus(duration));

        subscriptions.put(subscriptionId, subscribeContext);

        return subscriptionId;
    }

    /**
     * updates a subscription.
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

    /**
     *
     * @param subscriptionId the id of the subscription
     * @return the corresponding subscription or null if not found
     */
    public SubscribeContext getSubscription(String subscriptionId) {
        return subscriptions.get(subscriptionId);
    }

    /**
     * @return the duration in String format
     * @throws SubscriptionException
     */
    private Duration convertDuration(String duration) throws SubscriptionException {
        // Use java.xml.datatype functions as java.time do not handle durations with months and years...
        try {
            long longDuration = DatatypeFactory.newInstance().newDuration(duration).getTimeInMillis(new Date());
            return Duration.ofMillis(longDuration);
        } catch (Exception e) {
            throw new SubscriptionException("bad duration: " + duration, e);
        }
    }

    /**
     * Compile (or get from cache) the patter corresponding to the entity id
     * @param entityId the entity id
     * @return the pattern, or null if entity id is not a pattern
     * @throws PatternSyntaxException
     */
    public Pattern getPattern(final EntityId entityId) throws PatternSyntaxException {
        if (!entityId.getIsPattern()) {
            return null;
        }
        String id = entityId.getId();
        Pattern pattern = cachedPatterns.get(id);
        if (pattern == null) {
            pattern = Pattern.compile(id);
            cachedPatterns.put(id, pattern);
        }
        return pattern;
    }


}

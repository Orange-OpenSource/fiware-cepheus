/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.cepheus.broker.exception.SubscriptionException;
import com.orange.cepheus.broker.exception.SubscriptionPersistenceException;
import com.orange.cepheus.broker.model.Subscription;
import com.orange.cepheus.broker.persistence.SubscriptionsRepository;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.PatternSyntaxException;

/**
 * Handles subscriptions.
 */
@Component
public class Subscriptions {

    private static Logger logger = LoggerFactory.getLogger(Subscriptions.class);

    private Map<String, Subscription> subscriptions;

    @Autowired
    private Patterns patterns;

    @Autowired
    SubscriptionsRepository subscriptionsRepository;

    @PostConstruct
    protected void loadSubscriptionsOnStartup() {
        try {
            subscriptions = subscriptionsRepository.getAllSubscriptions();
        } catch (SubscriptionPersistenceException e) {
            logger.error("Failed to load subscriptions from database", e);
        }
    }

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
            subscribeContext.getEntityIdList().forEach(patterns::getPattern);
        } catch (PatternSyntaxException e) {
            throw new SubscriptionException("bad pattern", e);
        }

        // Generate a subscription id
        String subscriptionId = UUID.randomUUID().toString();

        //create subscription and set the expiration date and subscriptionId
        Subscription subscription = new Subscription(subscriptionId, Instant.now().plus(duration), subscribeContext);

        subscriptions.put(subscriptionId, subscription);
        try {
            subscriptionsRepository.saveSubscription(subscription);
        } catch (SubscriptionPersistenceException e) {
            logger.error("Failed to save subscription into database", e);
        }

        return subscriptionId;
    }

    /**
     * Removes a subscription.
     * @param unsubscribeContext
     * @return false if there is not subscription to delete
     */
    public boolean deleteSubscription(UnsubscribeContext unsubscribeContext) {
        String subscriptionId = unsubscribeContext.getSubscriptionId();
        Subscription subscription = subscriptions.remove(subscriptionId);
        try {
            subscriptionsRepository.removeSubscription(subscriptionId);
        } catch (SubscriptionPersistenceException e) {
            logger.error("Failed to remove subscription from database", e);
        }
        return (subscription != null);
    }

    /**
     * find subscriptionID matching the updateContext.
     * @param searchEntityId the entity id to search
     * @param searchAttributes the attributes to search
     * @return list of matching subscription
     */
    public Iterator<Subscription> findSubscriptions(EntityId searchEntityId, Set<String> searchAttributes) {

        // Filter out expired subscriptions
        Predicate<Subscription> filterExpired = subscription -> subscription.getExpirationDate().isAfter(Instant.now());

        // Filter only matching entity ids
        Predicate<EntityId> filterEntityId = patterns.getFilterEntityId(searchEntityId);

        // Only filter by attributes if search is looking for them
        final boolean noAttributes = searchAttributes == null || searchAttributes.size() == 0;

        // Filter each subscription (remove expired) and return its providing application
        // if at least one of its listed entities matches the searched context element
        // and if all searched attributes are defined in the subscription (if any)
        return subscriptions.values().stream()
                .filter(filterExpired)
                .filter(subscription -> subscription.getSubscribeContext().getEntityIdList().stream().filter(filterEntityId).findFirst().isPresent()
                        && (noAttributes || subscription.getSubscribeContext().getAttributeList().containsAll(searchAttributes))).iterator();

    }

    /**
     * Removed all expired subscriptions every minute.
     */
    @Scheduled(fixedDelay = 60000)
    public void purgeExpiredSubscriptions() {
        final Instant now = Instant.now();
        subscriptions.forEach((subscriptionId, subscribeContext) -> {
            if (subscribeContext.getExpirationDate().isBefore(now)) {
                subscriptions.remove(subscriptionId);
                try {
                    subscriptionsRepository.removeSubscription(subscriptionId);
                } catch (SubscriptionPersistenceException e) {
                    logger.error("Failed to remove subscription from database", e);
                }
            }
        });
    }

    /**
     *
     * @param subscriptionId the id of the subscription
     * @return the corresponding subscription or null if not found
     */
    public Subscription getSubscription(String subscriptionId) {
        Subscription subscription = subscriptions.get(subscriptionId);
        return subscription;
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

}

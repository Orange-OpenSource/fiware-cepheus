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
import com.orange.ngsi.model.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

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
     * @return false if there is not subscription to delete
     */
    public boolean deleteSubscription(UnsubscribeContext unsubscribeContext) {
        SubscribeContext subscribeContext = subscriptions.remove(unsubscribeContext.getSubscriptionId());
        return (subscribeContext != null);
    }

    /**
     * find subscriptionID matching the updateContext.
     * @param searchEntityId the entity id to search
     * @param searchAttributes the attributes to search
     * @return list of matching subscription
     */
    public Iterator<SubscribeContext> findSubscriptions(EntityId searchEntityId, Set<String> searchAttributes) {
        final boolean searchType = hasType(searchEntityId);
        final Pattern pattern = getPattern(searchEntityId);

        // Filter out expired subscriptions
        Predicate<SubscribeContext> filterExpired = subscribeContext -> subscribeContext.getExpirationDate().isAfter(Instant.now());

        // Filter only matching entity ids
        Predicate<EntityId> filterEntityId = entityId -> {
            // Match by type if any
            if (searchType && (!hasType(entityId) || !searchEntityId.getType().equals(entityId.getType()))) {
                return false;
            }
            // Match pattern if any
            if (pattern != null) {
                // Match two patterns by equality
                if (entityId.getIsPattern()) {
                    return searchEntityId.getId().equals(entityId.getId());
                }
                return pattern.matcher(entityId.getId()).find();
            } else {
                if (entityId.getIsPattern()) {
                    return getPattern(entityId).matcher(searchEntityId.getId()).find();
                }
                // Match two patterns by equality
                return searchEntityId.getId().equals(entityId.getId());
            }
        };

        // Only filter by attributes if search is looking for them
        final boolean noAttributes = searchAttributes == null || searchAttributes.size() == 0;

        // Filter each registration (remove expired) and return its providing application
        // if at least one of its listed entities matches the searched context element
        // and if all searched attributes are defined in the registration (if any)
        return subscriptions.values().stream()
                .filter(filterExpired)
                .filter(subscribeContext -> subscribeContext.getEntityIdList().stream().filter(filterEntityId).findFirst().isPresent()
                        && (noAttributes || subscribeContext.getAttributeList().containsAll(searchAttributes))).iterator();

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
            }
        });
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
     * @return TRUE if the type is not null or empty
     */
    private boolean hasType(final EntityId entityId) {
        final String type = entityId.getType();
        return type != null && !"".equals(type);
    }

    /**
     * Compile (or get from cache) the patter corresponding to the entity id
     * @param entityId the entity id
     * @return the pattern, or null if entity id is not a pattern
     * @throws PatternSyntaxException
     */
    private Pattern getPattern(final EntityId entityId) throws PatternSyntaxException {
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

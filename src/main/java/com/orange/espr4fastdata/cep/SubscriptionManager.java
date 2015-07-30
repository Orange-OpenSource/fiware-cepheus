/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.model.Attribute;
import com.orange.espr4fastdata.model.Configuration;
import com.orange.espr4fastdata.model.EventTypeIn;
import com.orange.espr4fastdata.model.Provider;
import com.orange.ngsi.client.SubscribeContextRequest;
import com.orange.ngsi.model.EntityId;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.SubscribeError;
import com.orange.ngsi.model.SubscribeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * SubscriptionManager manage subscriptions of EventTypeIn to provider
 * When a configuration is loaded, SubscriptionManager send subscription to every provider
 * Every five minutes SubscriptionManager verify if subscription is valid (subscriptionDate
 * is not null or (subscriptionDate + duration) < current Date
 */
@Component
public class SubscriptionManager {

    private static Logger logger = LoggerFactory.getLogger(SubscriptionManager.class);

    /**
     * Inner class for concurrent subscriptions tracking using a RW lock.
     */
    private class Subscriptions {
        private HashSet<String> subscriptionIds = new HashSet<>();
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        public boolean isSubscriptionValid(String subscriptionId) {
            readWriteLock.readLock().lock();
            boolean result = subscriptionIds.contains(subscriptionId);
            readWriteLock.readLock().unlock();
            return result;
        }

        private void addSubscription(String subscriptionId) {
            readWriteLock.writeLock().lock();
            subscriptionIds.add(subscriptionId);
            readWriteLock.writeLock().unlock();
        }

        private void removeSubscription(String subscriptionId) {
            readWriteLock.writeLock().lock();
            subscriptionIds.remove(subscriptionId);
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * Periodicity of the subscription task. Default: every 5 min.
     * Must be smaller than the subscription duration !
     */
    @Value("${subscriptionManager.periodicity:300000}")
    private long subscriptionPeriodicity;

    /**
     * Duration of a NGSI subscription as text.
     */
    @Value("${subscriptionManager.duration:P1H}")
    private String subscriptionDuration;

    @Autowired
    private SubscribeContextRequest subscribeContextRequest;

    @Autowired
    private TaskScheduler taskScheduler;

    private List<EventTypeIn> eventTypeIns = Collections.emptyList();

    private Subscriptions subscriptions = new Subscriptions();

    private ScheduledFuture scheduledFuture;

    private URI hostURI;

    /**
     * Update subscription to new provider of the incomming events defined in the Configuration
     *
     * @param configuration the new configuration
     */
    public void setConfiguration(Configuration configuration) {
        hostURI = configuration.getHost();

        // Use a new subscription set on each new configuration
        // this prevents active subscription tasks to add subscription ids from the previous configuration
        // this will also copy the subscription information of the previous configuration to this new configuration
        subscriptions = migrateSubscriptions(configuration);

        // Keep a reference to configuration for next migration
        eventTypeIns = configuration.getEventTypeIns();

        // TODO : send unsubscribeContext with removedEventTypesIn

        // force launch of subscription process for new or invalid subscriptions
        scheduleSubscriptionTask();
    }

    /**
     * Check that a given subscription is valid
     *
     * @param subscriptionId the id of subscription
     * @return true if subscription is valid
     */
    public boolean isSubscriptionValid(String subscriptionId) {
        return subscriptions.isSubscriptionValid(subscriptionId);
    }

    /**
     * Cancel any previous schedule, run subscription task immediately and schedule it again.
     */
    private void scheduleSubscriptionTask() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        scheduledFuture = taskScheduler.scheduleWithFixedDelay(this::periodicSubscriptionTask, subscriptionPeriodicity);
    }

    private void periodicSubscriptionTask() {

        Instant now = Instant.now();
        Instant nextSubscriptionTaskDate = now.plusMillis(subscriptionPeriodicity);
        logger.info("Launch of the periodic subscription task at {}", now.toString());

        // Futures will use the current subscription list.
        // So that they will not add old subscriptions to a new configuration.
        Subscriptions subscriptions = this.subscriptions;

        for (EventTypeIn eventType : eventTypeIns) {
            SubscribeContext subscribeContext = null;
            for (Provider provider : eventType.getProviders()) {

                boolean deadlineIsPassed = false;
                Instant subscriptionDate = provider.getSubscriptionDate();
                if (subscriptionDate != null) {
                    Instant subscriptionEndDate = subscriptionDate.plus(Duration.parse(subscriptionDuration));
                    // check if deadline is passed
                    if (nextSubscriptionTaskDate.compareTo(subscriptionEndDate) >= 0) {
                        deadlineIsPassed = true;
                        String subscriptionId = provider.getSubscriptionId();
                        // if delay is passed then clear the subscription info in provider et suppress subscription
                        if (subscriptionId != null) {
                            subscriptions.removeSubscription(subscriptionId);
                            provider.setSubscriptionId(null);
                            provider.setSubscriptionDate(null);
                        }
                    }
                }
                //Send subscription if subscription is a new subscription or we do not receive a response (subscriptionDate is null)
                //Send subscription if deadline is passed
                if ((subscriptionDate == null) || deadlineIsPassed) {
                    // lazy build body request only when the first request requires it
                    if (subscribeContext == null) {
                        subscribeContext = buildSubscribeContext(eventType);
                    }
                    doSubscription(provider, subscribeContext, subscriptions);
                }
            }
        }
    }

    private void doSubscription(Provider provider, SubscribeContext subscribeContext, Subscriptions subscriptions) {
        subscribeContextRequest.postSubscribeContextRequest(subscribeContext, provider.getUrl(),
                new SubscribeContextRequest.SubscribeContextResponseListener() {
                    @Override public void onError(SubscribeError subscribeError, Throwable t) {
                        if (subscribeError != null) {
                            logger.warn("SubscribeError received for {}: {} | {}", provider.getUrl(),
                                    subscribeError.getErrorCode().getCode(), subscribeError.getErrorCode().getDetail());
                        } else {
                            logger.warn("Error during subscription for {}: {}", provider.getUrl(), t.toString());
                        }
                    }

                    @Override public void onSuccess(SubscribeResponse subscribeResponse) {
                        String subscriptionId = subscribeResponse.getSubscriptionId();

                        provider.setSubscriptionDate(Instant.now());
                        provider.setSubscriptionId(subscriptionId);
                        subscriptions.addSubscription(subscriptionId);
                    }
                });
    }

    private SubscribeContext buildSubscribeContext(EventTypeIn eventType) {
        SubscribeContext subscribeContext = new SubscribeContext();

        EntityId entityId = new EntityId(eventType.getId(), eventType.getType(), eventType.isPattern());
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        subscribeContext.setAttributeList(eventType.getAttributes().stream().map(Attribute::getName).collect(Collectors.toList()));
        subscribeContext.setReference(hostURI);
        subscribeContext.setDuration(subscriptionDuration);
        return subscribeContext;
    }

    /**
     * Migrate subscriptions from previous configuration to the new configuration
     * @param configuration the new configuration where the subscriptions must be insterted
     * @return the list of id of the migrated subscriptions
     */
    private Subscriptions migrateSubscriptions(Configuration configuration) {
        Subscriptions newSubscriptions = new Subscriptions();

        // For every eventType, find the corresponding one in previous configuration
        configuration.getEventTypeIns().forEach(eventTypeIn ->
            eventTypeIns.stream().filter(e -> e.equals(eventTypeIn)).findFirst().ifPresent(e -> {

                // For every provider, find the corresponding one in previous configuration
                eventTypeIn.getProviders().forEach(provider ->
                    e.getProviders().stream().filter(p -> p.getUrl().equals(provider.getUrl())).findFirst().ifPresent(oldProvider -> {

                        // Migrate the subscription
                        provider.setSubscriptionId(oldProvider.getSubscriptionId());
                        provider.setSubscriptionDate(oldProvider.getSubscriptionDate());
                        newSubscriptions.addSubscription(oldProvider.getSubscriptionId());
                    })
                );
            })
        );

        return newSubscriptions;
    }
}

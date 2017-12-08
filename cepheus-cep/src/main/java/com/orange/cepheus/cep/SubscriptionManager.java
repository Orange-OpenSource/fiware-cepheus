/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.orange.cepheus.cep.model.Attribute;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.cepheus.cep.model.EventTypeIn;
import com.orange.cepheus.cep.model.Provider;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.EntityId;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.SubscribeContextResponse;
import com.orange.ngsi.model.SubscribeError;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import javax.annotation.PreDestroy;
import java.io.IOException;
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
 * Every five minutes SubscriptionManager verify if subscription is valid
 */
@Component()
public class SubscriptionManager {

    private static Logger logger = LoggerFactory.getLogger(SubscriptionManager.class);
    
    private Configuration configuration;
    private HttpHeaders httpHeaders = null;
    /**
     * Inner class for concurrent subscriptions tracking using a RW lock.
     */
    private static class Subscriptions {
        private HashSet<String> subscriptionIds = new HashSet<>();
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        public boolean isSubscriptionValid(String subscriptionId) {
            try {
                readWriteLock.readLock().lock();
                return subscriptionIds.contains(subscriptionId);
            } finally {
                readWriteLock.readLock().unlock();
            }
        }

        private void addSubscription(String subscriptionId) {
            try {
                readWriteLock.writeLock().lock();
                subscriptionIds.add(subscriptionId);
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }

        private void removeSubscription(String subscriptionId) {
            try {
                readWriteLock.writeLock().lock();
                subscriptionIds.remove(subscriptionId);
            } finally {
                readWriteLock.writeLock().unlock();
            }
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
    @Value("${subscriptionManager.duration:PT1H}")
    private String subscriptionDuration;

    @Value("${subscriptionManager.validateSubscriptionsId:true}")
    private boolean validateSubscriptionsId;

    @Autowired
    private NgsiClient ngsiClient;

    @Autowired
    private TaskScheduler taskScheduler;

    private List<EventTypeIn> eventTypeIns = Collections.emptyList();

    private Subscriptions subscriptions = new Subscriptions();

    private ScheduledFuture scheduledFuture;

    private URI hostURI;

    /**
     * Update subscription to new provider of the incoming events defined in the Configuration
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
     * unsubscribe if is invalid
     *
     * @param subscriptionId the id of subscription
     * @return true if subscription is valid
     */
    public boolean validateSubscriptionId(String subscriptionId, String originatorUrl) {
        if (validateSubscriptionsId) {
            boolean isValid = subscriptions.isSubscriptionValid(subscriptionId);
            if (!isValid) {
                logger.warn("unsubscribeContext request: clean invalid subscription id {} / {}", subscriptionId, originatorUrl);
                //TODO: add support multi-tenant subscription
                ngsiClient.unsubscribeContext(originatorUrl, null, subscriptionId).addCallback(
                        unsubscribeContextResponse ->
                                logger.debug("unsubscribeContext completed for {}", originatorUrl),
                        throwable ->
                                logger.warn("unsubscribeContext failed for {}", originatorUrl, throwable)
                );
            }
            return isValid;
        }
        return true;
    }

    @PreDestroy
    public void shutdownGracefully() {

        logger.info("Shutting down SubscriptionManager (cleanup subscriptions)");

        // Cancel the scheduled subscription task
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        // Unsubscribe from all providers
        eventTypeIns.forEach(eventTypeIn -> eventTypeIn.getProviders().forEach(this::unsubscribeProvider));

        // Try to stop gracefully (letting all unsubscribe complete)
        try {
            ngsiClient.shutdownGracefully();
        } catch (IOException e) {
            logger.warn("Failed to shutdown gracefully NGSI pending requests", e);
        }
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
                    subscribeProvider(provider, subscribeContext, subscriptions);
                }
            }
        }
    }

    /**
     * Subscribe to a provider
     * @param provider
     * @param subscribeContext
     * @param subscriptions
     */
    private void subscribeProvider(Provider provider, SubscribeContext subscribeContext, Subscriptions subscriptions) {
        logger.debug("Subscribe to {} for {}", provider.getUrl(), subscribeContext.toString());
       
        //added code for checking provider service name and service path
          if (StringUtils.isNotEmpty(provider.getServiceName())|| StringUtils.isNotEmpty(provider.getServicePath())) {
                  httpHeaders = getHeadersForProvider(provider);
              }
        // change httpheader value from null
        ngsiClient.subscribeContext(provider.getUrl(), httpHeaders, subscribeContext).addCallback(subscribeContextResponse -> {
            SubscribeError error = subscribeContextResponse.getSubscribeError();
            if (error == null) {
                String subscriptionId = subscribeContextResponse.getSubscribeResponse().getSubscriptionId();

                provider.setSubscriptionDate(Instant.now());
                provider.setSubscriptionId(subscriptionId);
                subscriptions.addSubscription(subscriptionId);

                logger.debug("Subscription done for {}", provider.getUrl());
            } else {
                logger.warn("Error during subscription for {}: {}", provider.getUrl(), error.getErrorCode());
            }
        }, throwable -> {
            logger.warn("Error during subscription for {}", provider.getUrl(), throwable);
        });
    }

    /**
     * Unsubscribe from a provider
     * @param provider the provider to unusubscribe from
     */
    private void unsubscribeProvider(Provider provider) {
        final String subscriptionID = provider.getSubscriptionId();
        if (subscriptionID != null) {
            logger.debug("Unsubscribe from {} for {}", provider.getUrl(), provider.getSubscriptionId());

            // Don't wait for result, remove immediately from subscriptions list
            subscriptions.removeSubscription(subscriptionID);
            //change null to httpheader
            ngsiClient.unsubscribeContext(provider.getUrl(), httpHeaders, provider.getSubscriptionId()).addCallback(
                    response -> logger.debug("Unsubribe response for {}: {}", subscriptionID, response.getStatusCode().getCode()),
                    throwable -> logger.debug("Error during unsubscribe for {}", subscriptionID, throwable));

            // Reset provider subscription data
            provider.setSubscriptionDate(null);
            provider.setSubscriptionId(null);
        }
    }

    private SubscribeContext buildSubscribeContext(EventTypeIn eventType) {
        SubscribeContext subscribeContext = new SubscribeContext();

        EntityId entityId = new EntityId(eventType.getId(), eventType.getType(), eventType.isPattern());
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        subscribeContext.setAttributeList(eventType.getAttributes().stream().map(Attribute::getName).collect(Collectors.toList()));
        subscribeContext.setReference(hostURI.resolve("/ngsi10/notifyContext"));
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

        // For every previous eventType, find the corresponding one in new configuration
        eventTypeIns.forEach(oldEventTypeIn -> {
            Optional<EventTypeIn> maybeEventTypeIn =
                    configuration.getEventTypeIns().stream().filter(e -> e.equals(oldEventTypeIn)).findFirst();
            if (maybeEventTypeIn.isPresent()) {
                EventTypeIn newEventTypeIn = maybeEventTypeIn.get();

                // For every previous provider, find the corresponding one in new configuration
                oldEventTypeIn.getProviders().forEach(oldProvider -> {

                    Optional<Provider> optionalProvider =
                            newEventTypeIn.getProviders().stream().filter(p -> p.getUrl().equals(oldProvider.getUrl())).findFirst();
                    if (optionalProvider.isPresent()) {
                        Provider provider = optionalProvider.get();

                        // Migrate the subscription
                        provider.setSubscriptionId(oldProvider.getSubscriptionId());
                        provider.setSubscriptionDate(oldProvider.getSubscriptionDate());
                        newSubscriptions.addSubscription(oldProvider.getSubscriptionId());
                    } else {
                        // Provider not found in new configuration, unsubscribe from it
                        unsubscribeProvider(oldProvider);
                    }
                });
            } else {
                // EventType not found in new configuration, unsubscribe from all providers
                oldEventTypeIn.getProviders().forEach(this::unsubscribeProvider);
            }
        });

        return newSubscriptions;
    }
    // added code for setting provider httpHeader
    public HttpHeaders getHeadersForProvider(Provider provider) {
       HttpHeaders httpHeaders = ngsiClient.getRequestHeaders(provider.getUrl());
       if (provider.getServiceName() != null) {
           httpHeaders.add("Fiware-Service", provider.getServiceName());
       } else if (configuration.getService() != null) {
           httpHeaders.add("Fiware-Service", configuration.getService());
       }
       if (provider.getServicePath() != null) {
           httpHeaders.add("Fiware-ServicePath", provider.getServicePath());
       } else if (configuration.getServicePath() != null) {
           httpHeaders.add("Fiware-ServicePath", configuration.getServicePath());
       }
       return httpHeaders;
   }
}

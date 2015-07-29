/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.model.cep.*;
import com.orange.ngsi.client.SubscribeContextRequest;
import com.orange.ngsi.model.EntityId;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.SubscribeError;
import com.orange.ngsi.model.SubscribeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * SubscriptionManager manage subscriptions of EventTypeIn to provider
 * When a configuration is loaded, SubscriptionManager send subscription to every provider
 * Every five minutes SubscriptionManager verify if subscription is valid (subscriptionDate
 * is not null or (subscriptionDate + duration) < current Date
 */
@Component
public class SubscriptionManager {

    private static Logger logger = LoggerFactory.getLogger(SubscriptionManager.class);

    private HashSet<String> subscriptionIds = new HashSet<>();

    @Value("${subscriptionManager.duration:P1H}")
    private String duration;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private List<EventTypeIn> storedEventTypeIns = new LinkedList<>();

    private URI hostURI;

    @Autowired
    SubscribeContextRequest subscribeContextRequest;

    public void setConfiguration(Configuration configuration) throws URISyntaxException {

        Collection<EventTypeIn> previousEventTypesIn = storedEventTypeIns;
        Collection<EventTypeIn> newEventTypesIn = configuration.getEventTypeIns();

        // find the eventTypeIns that have been deleted
        List<EventTypeIn> removedEventTypesIn = new LinkedList<>(previousEventTypesIn);
        removedEventTypesIn.removeAll(newEventTypesIn);

        // find the new eventTypeIns
        List<EventTypeIn> addedEventTypes = new LinkedList<>(newEventTypesIn);
        addedEventTypes.removeAll(previousEventTypesIn);

        subscriptionIds = new HashSet<>();
        hostURI = new URI(configuration.getHost());

        // transfer the informations of subscription to those who remain
        for(EventTypeIn eventTypeIn : configuration.getEventTypeIns()) {
            EventTypeIn storedEventTypeIn = getStoredEventIn(eventTypeIn);
            if (storedEventTypeIn != null) {
                for(Provider provider : eventTypeIn.getProviders()) {
                    Provider storedProvider = getStoredProvider(storedEventTypeIn, provider);
                    if (storedProvider != null) {
                        provider.setSubscriptionDate(storedProvider.getSubscriptionDate());
                        provider.setSubscriptionId(storedProvider.getSubscriptionId());
                        subscriptionIds.add(provider.getSubscriptionId());
                    }
                }
            }
        }

        storedEventTypeIns = configuration.getEventTypeIns();

        // TODO : send unsubscribeContext with removedEventTypesIn

        //launch subscription for new eventType and also subscription not valid
        periodicSubscriptionTask();
    }

    //fixedDelay : time between the end of the last invocation and the start of the next
    //initialDelay is fixed because we do not want the task execution starts this method before to set configuration
    @Scheduled(fixedDelayString = "${subscriptionManager.fixedDelay:300000}",
            initialDelayString = "${subscriptionManager.initialDelay:300000}")
    public void periodicSubscriptionTask() throws URISyntaxException {

        Instant timestampCurrent = Instant.now();
        logger.debug("Launch periodicSubscriptionTask at {}", timestampCurrent.toString());

        for (EventTypeIn eventType : storedEventTypeIns) {
            SubscribeContext subscribeContext = getSubscriptionContext(eventType);
            for (Provider provider : eventType.getProviders()) {
                Boolean deadlineIsPassed = false;
                Instant subscriptionDate = provider.getSubscriptionDate();
                if (subscriptionDate != null) {
                    Instant oneDurationLater = subscriptionDate.plus(Duration.parse(duration));
                    Duration delay = Duration.between(timestampCurrent, oneDurationLater);

                    //check if subscription is valid : check if deadline is passed ?
                    if (delay.isNegative() || delay.isZero()) {
                        deadlineIsPassed = true;
                        String subscriptionId = provider.getSubscriptionId();
                        // if delay is passed then clear the subscription info in provider et suppress subscription
                        if (subscriptionId != null) {
                            subscriptionIds.remove(subscriptionId);
                            provider.setSubscriptionId(null);
                            provider.setSubscriptionDate(null);
                        }
                    }
                }
                //Send subscription if subscription is a new subscription or we do not receive a response (subscriptionDate is null)
                //Send subscription if deadline is passed
                if ((subscriptionDate == null) || (deadlineIsPassed)) {
                    subscribeContextRequest.postSubscribeContextRequest(subscribeContext, provider.getUrl(), new SubscribeContextRequest.SubscribeContextResponseListener() {
                        @Override
                        public void onError(SubscribeError subscribeError, Throwable t) {
                            if (subscribeError != null) {
                                String message = "SubscribeError received: " + subscribeError.getErrorCode().getCode() + " | " + subscribeError
                                        .getErrorCode().getDetail();
                                logger.warn(message);
                            } else {
                                logger.warn("SubscribeError", t);
                            }
                        }

                        @Override
                        public void onSuccess(SubscribeResponse subscribeResponse) {
                            provider.setSubscriptionDate(Instant.now());
                            provider.setSubscriptionId(subscribeResponse.getSubscriptionId());
                            subscriptionIds.add(provider.getSubscriptionId());
                        }
                    });
                }
            }
        }
    }

    private SubscribeContext getSubscriptionContext(EventTypeIn eventType) {
        SubscribeContext subscribeContext = new SubscribeContext();

        EntityId entityId = new EntityId(eventType.getId(), eventType.getType(), eventType.isPattern());
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));

        List<String> attributes = new ArrayList<>();
        for (Attribute attribute : eventType.getAttributes()) {
            attributes.add(attribute.getName());
        }
        subscribeContext.setAttributeList(attributes);
        subscribeContext.setReference(hostURI);
        subscribeContext.setDuration(duration);
        return subscribeContext;
    }

    private EventTypeIn getStoredEventIn(EventTypeIn eventTypeIn) {
        for(EventTypeIn storedEventTypeIn : storedEventTypeIns) {
            if (eventTypeIn.equals(storedEventTypeIn)) {
                return storedEventTypeIn;
            }
        }
        return null;
    }

    private Provider getStoredProvider(EventTypeIn storedEventTypeIn, Provider provider) {
        for(Provider storedProvider : storedEventTypeIn.getProviders()) {
            if (provider.getUrl().equals(storedProvider.getUrl())) {
                return storedProvider;
            }
        }
        return null;
    }
}

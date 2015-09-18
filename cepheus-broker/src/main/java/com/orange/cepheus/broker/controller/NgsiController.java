/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.controller;

import com.orange.cepheus.broker.Configuration;
import com.orange.cepheus.broker.LocalRegistrations;
import com.orange.cepheus.broker.Subscriptions;
import com.orange.cepheus.broker.exception.MissingRemoteBrokerException;
import com.orange.cepheus.broker.exception.RegistrationException;
import com.orange.cepheus.broker.exception.SubscriptionException;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import com.orange.ngsi.server.NgsiBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * NGSI Controller : NGSI operation implemented by Cepheus-lightbroker
 */
@RestController
@RequestMapping(value = {"/v1", "/v1/registry", "/ngsi9", "/NGSI9", "/ngsi10", "/NGSI10"})
public class NgsiController extends NgsiBaseController {

    private static Logger logger = LoggerFactory.getLogger(NgsiController.class);

    @Autowired
    LocalRegistrations localRegistrations;

    @Autowired
    Subscriptions subscriptions;

    @Autowired
    NgsiClient ngsiClient;

    @Autowired
    Configuration configuration;

    @Override
    public RegisterContextResponse registerContext(final RegisterContext register) throws RegistrationException {
        logger.debug("<= registerContext with id:{} duration:{}", register.getRegistrationId(), register.getDuration());

        RegisterContextResponse registerContextLocalResponse = new RegisterContextResponse();
        //register new registration or update previous registration (if registrationId != null) or remove registration (if duration = 0)
        registerContextLocalResponse.setRegistrationId(localRegistrations.updateRegistrationContext(register));

        return registerContextLocalResponse;
    }

    @Override
    public UpdateContextResponse updateContext(final UpdateContext update) throws ExecutionException, InterruptedException, URISyntaxException {

        //TODO : search providingApplication for all contextElement of updateContext
        ContextElement contextElement = update.getContextElements().get(0);
        Set<String> attributesName = contextElement.getContextAttributeList().stream().map(ContextAttribute::getName).collect(Collectors.toSet());

        logger.debug("<= updateContext with entityId: {} and attributes: {} ", contextElement.getEntityId().toString(), attributesName);

        // Search registrations to forward updateContext
        Iterator<URI> providingApplication = localRegistrations.findProvidingApplication(contextElement.getEntityId(), attributesName);
        if (providingApplication.hasNext()) {
            // Forward the update to the first providing Application (command)
            final String urlProvider = providingApplication.next().toString();
            logger.debug("=> updateContext forwarded to {}", urlProvider);
            return ngsiClient.updateContext(urlProvider, null, update).get();
        }

        // Forward the update to the remote broker
        final String brokerUrl = configuration.getRemoteUrl();
        if (brokerUrl == null || brokerUrl.isEmpty()) {
            logger.warn("No remote.url parameter defined to forward updateContext");
        } else {
            logger.debug("=> updateContext forwarded to remote broker {}", brokerUrl);
            ngsiClient.updateContext(brokerUrl, getRemoteBrokerHeaders(), update).addCallback(
                    updateContextResponse -> logger.debug("UpdateContext completed for {} ", brokerUrl),
                    throwable -> logger.warn("UpdateContext failed for {}: {}", brokerUrl, throwable.toString()));
        }

        List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_200);
        for (ContextElement c : update.getContextElements()) {
            contextElementResponseList.add(new ContextElementResponse(c, statusCode));
        }

        String originator = configuration.getLocalUrl();
        if (originator == null || originator.isEmpty()) {
            logger.warn("No local.url parameter defined to use as originator for sending notifyContext");
        } else {
            // Send notifications to matching subscriptions
            Iterator<SubscribeContext> matchingSubscriptions = subscriptions.findSubscriptions(contextElement.getEntityId(), attributesName);
            while (matchingSubscriptions.hasNext()) {
                SubscribeContext subscribeContext = matchingSubscriptions.next();
                NotifyContext notifyContext = new NotifyContext(subscribeContext.getSubscriptionId(), new URI(originator));
                notifyContext.setContextElementResponseList(contextElementResponseList);
                String providerUrl = subscribeContext.getReference().toString();

                logger.debug("=> notifyContext to {}", providerUrl);

                ngsiClient.notifyContext(providerUrl, null, notifyContext).addCallback(
                                notifyContextResponse -> logger.debug("NotifyContext completed for {}", providerUrl),
                                throwable -> logger.warn("NotifyContext failed for {}: {}", providerUrl, throwable.toString()));
            }
        }

        UpdateContextResponse updateContextResponse = new UpdateContextResponse();
        updateContextResponse.setContextElementResponses(contextElementResponseList);
        return updateContextResponse;
    }

    @Override
    public QueryContextResponse queryContext(final QueryContext query) throws ExecutionException, InterruptedException, MissingRemoteBrokerException {
        logger.debug("<= queryContext on entities: {}", query.getEntityIdList().toString());

        Set<String> attributes = new HashSet<>();
        if (query.getAttributeList() != null) {
            attributes.addAll(query.getAttributeList());
        }

        //TODO : search providingApplication for all entities of queryContext
        Iterator<URI> providingApplication = localRegistrations.findProvidingApplication(query.getEntityIdList().get(0), attributes);
        if (providingApplication.hasNext()) {
            // forward to providing application
            final String urlProvider = providingApplication.next().toString();
            logger.debug("=> queryContext forwarded to : {}", urlProvider);
            return ngsiClient.queryContext(urlProvider, null, query).get();
        }

        String brokerUrl = configuration.getRemoteUrl();
        if (brokerUrl == null || brokerUrl.isEmpty()) {
            throw new MissingRemoteBrokerException("No remote.url parameter defined to forward queryContext");
        }
        // forward query to remote broker
        logger.debug("=> queryContext forwarded to remote broker : {}", brokerUrl);
        return ngsiClient.queryContext(brokerUrl, getRemoteBrokerHeaders(), query).get();
    }

    @Override
    public SubscribeContextResponse subscribeContext(final SubscribeContext subscribe) throws SubscriptionException {
        logger.debug("<= subscribeContext on entities: {}", subscribe.getEntityIdList().toString());

        SubscribeContextResponse subscribeContextResponse = new SubscribeContextResponse();
        SubscribeResponse subscribeResponse = new SubscribeResponse();

        //add the subscription and return subscriptionId
        String subscriptionId = subscriptions.addSubscription(subscribe);
        subscribeResponse.setSubscriptionId(subscriptionId);
        //return in the response the duration because it is set by the subscriptions class if the duration is null in the request
        subscribeResponse.setDuration(subscriptions.getSubscription(subscriptionId).getDuration());
        subscribeContextResponse.setSubscribeResponse(subscribeResponse);

        return subscribeContextResponse;
    }

    @Override
    public UnsubscribeContextResponse unsubscribeContext(final UnsubscribeContext unsubscribe) {
        logger.debug("<= unsubscribeContext with subscriptionId: {}", unsubscribe.getSubscriptionId());

        String subscriptionId = unsubscribe.getSubscriptionId();
        StatusCode statusCode;
        if (subscriptions.deleteSubscription(unsubscribe)) {
            statusCode = new StatusCode(CodeEnum.CODE_200);
        } else {
            statusCode = new StatusCode(CodeEnum.CODE_470, subscriptionId);
        }
        return new UnsubscribeContextResponse(statusCode, subscriptionId);
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<Object> registrationExceptionHandler(HttpServletRequest req, RegistrationException registrationException) {
        logger.error("Registration error: {}", registrationException.getMessage());

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("400");
        statusCode.setReasonPhrase("registration error");
        statusCode.setDetail(registrationException.getMessage());
        return errorResponse(req.getRequestURI(), statusCode);
    }

    @ExceptionHandler(MissingRemoteBrokerException.class)
    public ResponseEntity<Object> missingRemoteBrokerExceptionHandler(HttpServletRequest req, MissingRemoteBrokerException missingRemoteBrokerException) {
        logger.error("MissingRemoteBrokerException error: {}", missingRemoteBrokerException.getMessage());

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("500");
        statusCode.setReasonPhrase("missing remote broker error");
        statusCode.setDetail(missingRemoteBrokerException.getMessage());
        return errorResponse(req.getRequestURI(), statusCode);
    }

    @ExceptionHandler(SubscriptionException.class)
    public ResponseEntity<Object> subscriptionExceptionHandler(HttpServletRequest req, SubscriptionException subscriptionException) {
        logger.error("Subscription error: {}", subscriptionException.getMessage());

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("400");
        statusCode.setReasonPhrase("subscription error");
        statusCode.setDetail(subscriptionException.getMessage());
        return errorResponse(req.getRequestURI(), statusCode);
    }

    private HttpHeaders getRemoteBrokerHeaders() {
        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders();
        configuration.addRemoteHeaders(httpHeaders);
        return httpHeaders;
    }
}

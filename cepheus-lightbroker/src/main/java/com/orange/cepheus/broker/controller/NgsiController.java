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
import org.springframework.beans.factory.annotation.Value;
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
        logger.debug("registerContext incoming request id:{} duration:{}", register.getRegistrationId(), register.getDuration());

        RegisterContextResponse registerContextLocalResponse = new RegisterContextResponse();
        //register new registration or update previous registration (if registrationId != null) or remove registration (if duration = 0)
        registerContextLocalResponse.setRegistrationId(localRegistrations.updateRegistrationContext(register));

        return registerContextLocalResponse;
    }

    @Override
    public UpdateContextResponse updateContext(final UpdateContext update) throws ExecutionException, InterruptedException, URISyntaxException {
        logger.debug("updateContext incoming request action:{}", update.getUpdateAction());

        //TODO : search providingApplication for all contextElement of updateContext
        ContextElement contextElement = update.getContextElements().get(0);
        Set<String> attributesName = contextElement.getContextAttributeList().stream().map(ContextAttribute::getName).collect(Collectors.toSet());

        // search providingApplication to forward updateContext
        logger.debug("updateContext incoming request on entityId: {} and on attributes: {} ", contextElement.getEntityId().toString(), attributesName);
        Iterator<URI> providingApplication = localRegistrations.findProvidingApplication(contextElement.getEntityId(), attributesName);

        if (providingApplication.hasNext()) {
            //send the update to the first providing Application (command)
            final String urlProvider = providingApplication.next().toString();
            logger.debug("providingApplication to forward updateContext founded: {}", urlProvider);
            return ngsiClient.updateContext(urlProvider, null, update).get();
        } else {
            logger.debug("Not providingApplication then forward to the remote broker");
            //forward the update to the remote broker

            // When no remote broker is define, don't do anything.
            if ((configuration.getRemoteBroker() == null) || (configuration.getRemoteBroker().getUrl() == null) || (configuration.getRemoteBroker().getUrl().isEmpty())) {
                logger.warn("Not remote broker to foward updateContext coming from providingApplication");
            } else {

                final String urlBroker = configuration.getRemoteBroker().getUrl();
                HttpHeaders httpHeaders = configuration.getHeadersForBroker(ngsiClient.getRequestHeaders());
                ngsiClient.updateContext(urlBroker, httpHeaders, update).addCallback(
                        updateContextResponse -> logger.debug("UpdateContext completed for {} ", urlBroker),
                        throwable -> logger.warn("UpdateContext failed for {}: {}", urlBroker, throwable.toString()));
            }

            //create updateContextResponse
            UpdateContextResponse updateContextResponse = new UpdateContextResponse();
            List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
            StatusCode statusCode = new StatusCode(CodeEnum.CODE_200);
            for (ContextElement c : update.getContextElements()) {
                contextElementResponseList.add(new ContextElementResponse(c, statusCode));
            }
            updateContextResponse.setContextElementResponses(contextElementResponseList);

            //search subscriptions matching to send notifyContext (only update coming from providingApplication and not remote broker
            String originator = configuration.getLocalBroker();
            if (originator != null && !originator.isEmpty()) {
                Iterator<SubscribeContext> subscribeContextIterator = subscriptions.findSubscriptions(contextElement.getEntityId(), attributesName);
                while (subscribeContextIterator.hasNext()) {
                    SubscribeContext subscribeContext = subscribeContextIterator.next();
                    NotifyContext notifyContext = new NotifyContext(subscribeContext.getSubscriptionId(), new URI(originator));
                    notifyContext.setContextElementResponseList(contextElementResponseList);
                    String urlProvider = subscribeContext.getReference().toString();
                    ngsiClient.notifyContext(urlProvider, null, notifyContext).addCallback(
                            notifyContextResponse -> logger.debug("NotifyContext completed for {}", urlProvider),
                            throwable -> logger.warn("NotifyContext failed for {}: {}", urlProvider, throwable.toString()));
                }
            } else {
                logger.warn("Not local broker to set in notifyContext sending to reference application => Not notification sended");
            }

            return updateContextResponse;
        }
    }

    @Override
    public QueryContextResponse queryContext(final QueryContext query) throws ExecutionException, InterruptedException, MissingRemoteBrokerException {
        logger.debug("queryContext incoming request on entities:{}", query.getEntityIdList().toString());

        Set<String> attributes = new HashSet<>();
        if (query.getAttributList() != null) {
            attributes.addAll(query.getAttributList());
        }

        //TODO : search providingApplication for all entities of queryContext
        Iterator<URI> providingApplication = localRegistrations.findProvidingApplication(query.getEntityIdList().get(0), attributes);

        if (providingApplication.hasNext()) {
            // forward to providing application
            final String urlProvider = providingApplication.next().toString();
            return ngsiClient.queryContext(urlProvider, null, query).get();
        } else {
            // forward query to remote broker
            //check if remote broker is configured
            if ((configuration.getRemoteBroker() == null) || (configuration.getRemoteBroker().getUrl() == null) || (configuration.getRemoteBroker().getUrl().isEmpty())) {
                throw new MissingRemoteBrokerException("Not remote broker configured to foward queryContext coming from providingApplication");
            } else {
                String urlBroker = configuration.getRemoteBroker().getUrl();
                HttpHeaders httpHeaders = configuration.getHeadersForBroker(ngsiClient.getRequestHeaders());
                return ngsiClient.queryContext(urlBroker, httpHeaders, query).get();
            }
        }
    }

    @Override
    public SubscribeContextResponse subscribeContext(final SubscribeContext subscribe) throws SubscriptionException {
        logger.debug("subscribeContext incoming request on entities: {}", subscribe.getEntityIdList().toString());

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
        logger.debug("unsubscribeContext in coming request with subscriptionId: {}", unsubscribe.getSubscriptionId());

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

}

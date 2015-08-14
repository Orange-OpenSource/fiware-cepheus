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
import com.orange.cepheus.broker.exception.RegistrationException;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import com.orange.ngsi.server.NgsiBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * NGSI Controller : NGSI operation implemented by Cepheus-lightbroker
 */
@RestController
@RequestMapping("/v1")
public class NgsiController extends NgsiBaseController {

    private static Logger logger = LoggerFactory.getLogger(NgsiController.class);

    @Autowired LocalRegistrations localRegistrations;

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
    public UpdateContextResponse updateContext(final UpdateContext update) throws ExecutionException, InterruptedException {
        logger.debug("updateContext incoming request action:{}", update.getUpdateAction());

        //search only the first
        ContextElement contextElement = update.getContextElements().get(0);
        Set<String> attributesName = contextElement.getContextAttributeList().stream().map(ContextAttribute::getName).collect(Collectors.toSet());

        Iterator<URI> providingApplication = localRegistrations.findProvidingApplication(contextElement.getEntityId(), attributesName);

        if (providingApplication.hasNext()) {
            //send the update to the first providing Application (command)
            final String urlProvider = providingApplication.next().toString();
            return ngsiClient.updateContext(urlProvider, null, update).get();
        } else {

            //forward the update to the remote broker
            final String urlBroker = configuration.getRemoteBroker();

            if (urlBroker != null) {
                //TODO : use fiware-service in http headers
                ngsiClient.updateContext(urlBroker, null, update).addCallback(
                        updateContextResponse -> logger.debug("UpdateContext completed for {} ", urlBroker),
                        throwable -> logger.warn("UpdateContext failed for {}: {}", urlBroker, throwable.toString()));

            } else {
                logger.warn("Not remote broker to foward updateContext coming from providingApplication");
            }

            UpdateContextResponse updateContextResponse = new UpdateContextResponse();
            List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
            StatusCode statusCode = new StatusCode(CodeEnum.CODE_200);
            for (ContextElement c : update.getContextElements()) {
                contextElementResponseList.add(new ContextElementResponse(c, statusCode));
            }
            updateContextResponse.setContextElementResponses(contextElementResponseList);
            return updateContextResponse;
        }
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
}

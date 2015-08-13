/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.controller;

import com.orange.cepheus.broker.Configuration;
import com.orange.cepheus.broker.Registrations;
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

    @Autowired
    Registrations registrations;

    @Autowired
    NgsiClient ngsiClient;

    @Autowired
    Configuration configuration;

    @Override
    public RegisterContextResponse registerContext(final RegisterContext register) throws Registrations.RegistrationException {
        logger.debug("registerContext incoming request id:{} duration:{}", register.getRegistrationId(), register.getDuration());

        RegisterContextResponse registerContextResponse = new RegisterContextResponse();

        //register new registration or update previous registration (if registrationId != null) or remove registration (if duration = 0)
        registerContextResponse.setRegistrationId(registrations.addContextRegistration(register));

        //TODO forward the register to remote broker

        return registerContextResponse;
    }

    @Override
    public UpdateContextResponse updateContext(final UpdateContext update) throws ExecutionException, InterruptedException {
        logger.debug("updateContext incoming request action:{}", update.getUpdateAction());

        //search only the first
        ContextElement contextElement = update.getContextElements().get(0);
        Set<String> attributesName = contextElement.getContextAttributeList().stream().map(ContextAttribute::getName).collect(Collectors.toSet());

        Iterator<URI> providingApplication = registrations.findProvidingApplication(contextElement.getEntityId(), attributesName);
        String urlProvider;
        if (providingApplication.hasNext()) {
            //send the update to the first providing Application
            urlProvider = providingApplication.next().toString();
        } else {
            //forward the update to the remote broker
            StringBuilder urlBrokerBuilder = new StringBuilder("http://").append(configuration.getRemoteHost()).append(':').append(configuration.getRemotePort()).append("/v1/updateContext");
            urlProvider = urlBrokerBuilder.toString();
        }
        //TODO : use fiware-service in http headers
        return ngsiClient.updateContext(urlProvider, null, update).get();
    }

    @ExceptionHandler({Registrations.RegistrationException.class})
    public ResponseEntity<Object> registrationExceptionHandler(HttpServletRequest req, Registrations.RegistrationException registrationException) {
        logger.error("Registration error: {}", registrationException.toString());

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("500");
        statusCode.setReasonPhrase("registration error");
        statusCode.setDetail(registrationException.toString());
        return errorResponse(req.getRequestURI(), statusCode);
    }

}

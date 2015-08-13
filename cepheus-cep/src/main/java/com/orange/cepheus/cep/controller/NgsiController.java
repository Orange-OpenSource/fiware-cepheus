/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.controller;

import com.orange.cepheus.cep.ComplexEventProcessor;
import com.orange.cepheus.cep.EventMapper;
import com.orange.cepheus.cep.SubscriptionManager;
import com.orange.cepheus.cep.exception.EventProcessingException;
import com.orange.cepheus.cep.exception.TypeNotFoundException;
import com.orange.cepheus.cep.model.Event;
import com.orange.ngsi.model.*;
import com.orange.ngsi.server.NgsiBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Controller for the NGSI 9/10 requests supported by the CEP
 */
@RestController
@RequestMapping("/v1")
public class NgsiController extends NgsiBaseController {

    private static Logger logger = LoggerFactory.getLogger(NgsiController.class);

    @Autowired
    protected ComplexEventProcessor complexEventProcessor;

    @Autowired
    public EventMapper eventMapper;

    @Autowired
    public SubscriptionManager subscriptionManager;

    @Override
    public NotifyContextResponse notifyContext(final NotifyContext notify) throws EventProcessingException, TypeNotFoundException {

        logger.debug("notifyContext incoming request id:{} originator:{}", notify.getSubscriptionId(), notify.getOriginator());

        NotifyContextResponse notifyContextResponse = new NotifyContextResponse();

        // Only handle notification if it has a valid subscription
        if (subscriptionManager.isSubscriptionValid(notify.getSubscriptionId())) {
            for (ContextElementResponse response : notify.getContextElementResponseList()) {
                ContextElement element = response.getContextElement();
                Event event = eventMapper.eventFromContextElement(element);
                complexEventProcessor.processEvent(event);
            }
            notifyContextResponse.setResponseCode(new StatusCode(CodeEnum.CODE_200));
        } else {
            logger.warn("notifyContext request: invalid subscription id {} / {}", notify.getSubscriptionId(), notify.getOriginator());
            notifyContextResponse.setResponseCode(new StatusCode(CodeEnum.CODE_470, notify.getSubscriptionId()));
        }

        return notifyContextResponse;
    }

    @Override
    public UpdateContextResponse updateContext(final UpdateContext update) throws TypeNotFoundException {

        logger.debug("updateContext incoming request: {}", update.toString());

        List<ContextElementResponse> responses = new LinkedList<>();

        for (ContextElement element : update.getContextElements()) {
            StatusCode statusCode;
            try {
                Event event = eventMapper.eventFromContextElement(element);
                complexEventProcessor.processEvent(event);
                statusCode = new StatusCode(CodeEnum.CODE_200);
            } catch (EventProcessingException e) {
                logger.error("updateContext incoming request: failed to process event {}", e.toString());
                statusCode = new StatusCode(CodeEnum.CODE_472, "");
                statusCode.setDetail(e.getMessage());
            }
            responses.add(new ContextElementResponse(element, statusCode));
        }

        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(responses);
        return response;
    }

    @ExceptionHandler({TypeNotFoundException.class})
    public ResponseEntity<Object> typeNotFoundExceptionHandler(HttpServletRequest req, TypeNotFoundException typeNotFoundException) {
        logger.error("Type not found: {}", typeNotFoundException.getTypeName());

        return errorResponse(req.getRequestURI(), new StatusCode(CodeEnum.CODE_472, typeNotFoundException.getTypeName()));
    }

    @ExceptionHandler({EventProcessingException.class})
    public ResponseEntity<Object> eventProcessinExceptionHandler(HttpServletRequest req, EventProcessingException eventProcessingException) {
        logger.error("Event processing error: {}", eventProcessingException.toString());

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("500");
        statusCode.setReasonPhrase("event processing error");
        statusCode.setDetail(eventProcessingException.toString());
        return errorResponse(req.getRequestURI(), statusCode);
    }
}

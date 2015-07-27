/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.cep.EventMapper;
import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.exception.MissingRequestParameterException;
import com.orange.espr4fastdata.exception.TypeNotFoundException;
import com.orange.espr4fastdata.model.Event;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Controller for the NGSI 9/10 requests
 */
@RestController
@RequestMapping("/v1")
public class NgsiController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final ComplexEventProcessor complexEventProcessor;

    @Autowired
    public NgsiController(ComplexEventProcessor complexEventProcessor) {
        this.complexEventProcessor = complexEventProcessor;
    }

    @Autowired
    public EventMapper eventMapper;


    @RequestMapping(value = "/notifyContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotifyContextResponse> notifyContext(@RequestBody final NotifyContext notify) throws EventProcessingException, TypeNotFoundException, MissingRequestParameterException {

        checkNotifyContext(notify);

        List<ContextElementResponse> responses = new LinkedList<>();

        for (ContextElementResponse response : notify.getContextElementResponseList()) {

            ContextElement element = response.getContextElement();
            Event event = eventMapper.eventFromContextElement(element);
            complexEventProcessor.processEvent(event);

        }

        NotifyContextResponse notifyContextResponse = new NotifyContextResponse();
        notifyContextResponse.setResponseCode(new StatusCode(CodeEnum.CODE_200));

        return new ResponseEntity<NotifyContextResponse>(notifyContextResponse, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateContextResponse updateContext(@RequestBody final UpdateContext update) throws EventProcessingException, MissingRequestParameterException, TypeNotFoundException {

        checkUpdateContext(update);

        List<ContextElementResponse> responses = new LinkedList<>();

        for (ContextElement element : update.getContextElements()) {
            StatusCode statusCode;
            try {
                Event event = eventMapper.eventFromContextElement(element);
                complexEventProcessor.processEvent(event);
                statusCode = new StatusCode(CodeEnum.CODE_200);
            } catch (EventProcessingException e) {
                statusCode = new StatusCode(CodeEnum.CODE_472, "");
                statusCode.setDetail(e.getMessage());
            }
            responses.add(new ContextElementResponse(element, statusCode));
        }

        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(responses);
        return response;
    }

    private void checkUpdateContext(UpdateContext updateContext) throws MissingRequestParameterException {

        if (updateContext.getUpdateAction() == null) {
            throw new MissingRequestParameterException("updateAction", "UpdateAction");
        }

        if ((updateContext.getContextElements() == null) && (!updateContext.getUpdateAction().isDelete())) {
            throw new MissingRequestParameterException("contextElements", "List<ContextElement>");
        }

        if (updateContext.getContextElements().isEmpty() && (!updateContext.getUpdateAction().isDelete())) {
            throw new MissingRequestParameterException("contextElements", "List<ContextElement>");
        }

        for (ContextElement contextElement : updateContext.getContextElements()) {
            checkContextElement(contextElement);
        }

    }

    private void checkNotifyContext(NotifyContext notifyContext) throws MissingRequestParameterException {

        if ((notifyContext.getSubscriptionId() == null) || (notifyContext.getSubscriptionId().isEmpty())) {
            throw new MissingRequestParameterException("subscriptionId", "string");
        }

        if ((notifyContext.getOriginator() == null) || (notifyContext.getOriginator().toString().isEmpty())){
            throw new MissingRequestParameterException("originator", "URI");
        }

        if (notifyContext.getContextElementResponseList() == null)  {
            throw new MissingRequestParameterException("contextElementResponse", "List<ContextElementResponse>");
        }


        for (ContextElementResponse contextElementResponse : notifyContext.getContextElementResponseList()) {
            checkContextElementResponse(contextElementResponse);
        }

    }

    private void checkContextElementResponse(ContextElementResponse contextElementResponse) throws MissingRequestParameterException {


        if (contextElementResponse.getStatusCode() == null) {
            throw new MissingRequestParameterException("statusCode", "StatusCode");
        }

        if (contextElementResponse.getContextElement() == null) {
            throw new MissingRequestParameterException("contextElement", "ContextElement");
        }

        checkContextElement(contextElementResponse.getContextElement());

    }
    private void checkContextElement(ContextElement contextElement) throws MissingRequestParameterException {

        if (contextElement.getEntityId() == null) {
            throw new MissingRequestParameterException("entityId", "EntityId");
        }

        checkEntityId(contextElement.getEntityId());


    }



    private void checkEntityId(EntityId entityId) throws MissingRequestParameterException {

        if ((entityId.getId() == null) || (entityId.getId().isEmpty())) {
            throw new MissingRequestParameterException("id", "string");
        }

        if ((entityId.getType() == null) || (entityId.getType().isEmpty())) {
            throw new MissingRequestParameterException("type", "string");
        }

        if (entityId.getIsPattern() == null)  {
            entityId.setIsPattern(false);
        }
    }
}

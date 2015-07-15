/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.exception.MissingRequestParameterException;
import com.orange.espr4fastdata.exception.TypeNotFoundException;
import com.orange.espr4fastdata.model.Event;
import com.orange.espr4fastdata.model.ngsi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * Controller for the NGSI 9/10 requests
 */
@RestController
@RequestMapping("/api/v1/ngsi")
public class NgsiController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final ComplexEventProcessor complexEventProcessor;

    @Autowired
    public NgsiController(ComplexEventProcessor complexEventProcessor) {
        this.complexEventProcessor = complexEventProcessor;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new NgsiValidator());
    }

    @RequestMapping(value = "/notifyContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotifyContextResponse> notifyContext(@Valid @RequestBody final NotifyContext notify) throws EventProcessingException, TypeNotFoundException {


        List<ContextElementResponse> responses = new LinkedList<>();

        for (ContextElementResponse response : notify.getContextElementResponseList()) {

            ContextElement element = response.getContextElement();
            Event event = eventFromContextElement(element);
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
                Event event = eventFromContextElement(element);
                complexEventProcessor.processEvent(event);
                statusCode = new StatusCode(CodeEnum.CODE_200);
            } catch (EventProcessingException e) {
                statusCode = new StatusCode(CodeEnum.CODE_472,"");
                statusCode.setDetail(e.getMessage());
            }
            responses.add(new ContextElementResponse(element, statusCode));
        }

        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(responses);
        return response;
    }

    /**
     * Convert a NGSI Context Element to a event.
     * @param contextElement the NGSI Context Element
     * @return an event to process
     * @throws EventProcessingException if the conversion fails
     */
    private Event eventFromContextElement(ContextElement contextElement) throws EventProcessingException, TypeNotFoundException {

        String type = contextElement.getEntityId().getType();

        //TODO check type exists at Configuration level, check all attributes have the correct associated type
        /*if (!complexEventProcessor.typeExistsInConfiguration(type)) {
            UpdateContextResponse updateContextResponse = new UpdateContextResponse();

            throw new TypeNotFoundException(type, updateContextResponse);
        }*/

        // Add all ContextElement attributes and the reserve 'id' attribute
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("id", contextElement.getEntityId().getId());
        for(ContextAttribute contextAttribute : contextElement.getContextAttributeList()) {
            String name = contextAttribute.getName();
            Object value = contextAttribute.getValue().get("value");

            if (value == null) {
                throw new EventProcessingException("Value cannot be null for attribute "+name);
            }
            //attributes.put(name, valueForType(value, contextAttribute.getType(), name));
            attributes.put(name, value);
        }

        return new Event(type, attributes);
    }


    /**
     * @param value the value to convert
     * @param type NGSI type
     * @param name used for error handling
     * @return a Java Object for given value
     * @throws EventProcessingException if the conversion fails
     */
    private Object valueForType(String value, String type, String name) throws EventProcessingException {
        // when type is not defined, handle as string
        if (type == null) {
            return value;
        }
        try {
            switch (type) {
                case "string":
                    return value;
                case "boolean":
                    return new Boolean(value);
                case "int":
                    return Integer.valueOf(value);
                case "float":
                    return Float.valueOf(value);
                case "double":
                    return Double.valueOf(value);
                default:
                    throw new EventProcessingException("Unsupported type "+type+" for attribute "+name);
            }
        } catch (NumberFormatException e) {
            throw new EventProcessingException("Failed to parse value "+value+" for attribute "+name);
        }
    }

    private void checkUpdateContext(UpdateContext updateContext) throws MissingRequestParameterException {

        if (updateContext.getUpdateAction() == null) {
            throw new MissingRequestParameterException("updateAction", "UpdateAction");
            //throw new MissingServletRequestParameterException("updateAction", "UpdateAction");
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

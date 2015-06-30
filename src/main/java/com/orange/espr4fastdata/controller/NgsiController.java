package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.model.Event;
import com.orange.espr4fastdata.model.ngsi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping(value = "/notifyContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> notifyContext(@RequestBody final NotifyContext notify) throws EventProcessingException {

        List<ContextElementResponse> responses = new LinkedList<>();

        for (ContextElementResponse response : notify.getContextElementResponseList()) {
            ContextElement element = response.getContextElement();
            StatusCode statusCode = StatusCode.CODE_200;
            try {
                Event event = eventFromContextElement(element);
                complexEventProcessor.processEvent(event);
            } catch (EventProcessingException e) {
                statusCode = StatusCode.CODE_400;
            }
            responses.add(new ContextElementResponse(element, statusCode));
        }

        //TODO send back a NotifyContextResponse
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/updateContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateContextResponse updateContext(@RequestBody final UpdateContext update) throws EventProcessingException {

        List<ContextElementResponse> responses = new LinkedList<>();

        for (ContextElement element : update.getContextElements()) {
            StatusCode statusCode = StatusCode.CODE_200;
            try {
                Event event = eventFromContextElement(element);
                complexEventProcessor.processEvent(event);
            } catch (EventProcessingException e) {
                statusCode = StatusCode.CODE_400;
            }
            responses.add(new ContextElementResponse(element, statusCode));
        }

        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(responses);
        return response;
    }

    //TODO handle responses for all exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST,reason="Event processing error")
    public ModelAndView handleEventProcessingError(HttpServletRequest req, Exception exception) {
        logger.error("Request: configuration error", exception);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return mav;
    }

    /**
     * Convert a NGSI Context Element to a event.
     * @param contextElement the NGSI Context Element
     * @return an event to process
     * @throws EventProcessingException if the conversion fails
     */
    private Event eventFromContextElement(ContextElement contextElement) throws EventProcessingException {

        String type = contextElement.getEntityId().getType();

        //TODO check type exists at Configuration level, check all attributes have the correct associated type

        // Add all ContextElement attributes and the reserve 'id' attribute
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put("id", contextElement.getEntityId().getId());
        for(ContextAttribute contextAttribute : contextElement.getContextAttributeList()) {
            String name = contextAttribute.getName();
            String value = contextAttribute.getContextValue();
            if (value == null) {
                throw new EventProcessingException("Value cannot be null for attribute "+name);
            }
            attributes.put(name, valueForType(value, contextAttribute.getType(), name));
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
}

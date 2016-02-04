/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.orange.ngsi.ProtocolRegistry;
import com.orange.ngsi.exception.MismatchIdException;
import com.orange.ngsi.exception.MissingRequestParameterException;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Controller for the NGSI 9/10 convenient REST requests
 * Deviation from standard:
 *  - no support for attributeDomains requests
 *  - only NGSI 10 REST requests are supported
 */
public class NgsiRestBaseController {

    private static Logger logger = LoggerFactory.getLogger(NgsiRestBaseController.class);

    @Autowired
    private NgsiValidation ngsiValidation;

    @Autowired
    private ProtocolRegistry protocolRegistry;

    /* Context Entities */

    @RequestMapping(method = RequestMethod.POST,
            value = {"/contextEntities/{entityID}", "/contextEntities/{entityID}/attributes"},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<AppendContextElementResponse> appendContextElement(
            @PathVariable String entityID,
            @RequestBody AppendContextElement appendContextElement,
            HttpServletRequest httpServletRequest) throws Exception {
        ngsiValidation.checkAppendContextElement(appendContextElement);
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(appendContextElement(entityID, appendContextElement), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = {"/contextEntities/{entityID}", "/contextEntities/{entityID}/attributes"},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<UpdateContextElementResponse> updateContextEntity(@PathVariable String entityID,
            @RequestBody UpdateContextElement updateContextElement,
            HttpServletRequest httpServletRequest) throws Exception {
        ngsiValidation.checkUpdateContextElement(updateContextElement);
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(updateContextElement(entityID, updateContextElement), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,
            value = {"/contextEntities/{entityID}", "/contextEntities/{entityID}/attributes"})
    final public ResponseEntity<ContextElementResponse> getContextEntity(@PathVariable String entityID,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(getContextElement(entityID), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,
            value = {"/contextEntities/{entityID}", "/contextEntities/{entityID}/attributes"})
    final public ResponseEntity<StatusCode> deleteContextEntity(@PathVariable String entityID,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(deleteContextElement(entityID), HttpStatus.OK);
    }

    /* Context Attributes */

    @RequestMapping(method = RequestMethod.POST,
            value = "/contextEntities/{entityID}/attributes/{attributeName}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<StatusCode> appendContextAttributeValue(@PathVariable String entityID,
            @PathVariable String attributeName,
            @RequestBody UpdateContextAttribute updateContextAttribute,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        ngsiValidation.checkUpdateContextAttribute(entityID, attributeName, Optional.empty(), updateContextAttribute);
        return new ResponseEntity<>(appendContextAttribute(entityID, attributeName, updateContextAttribute), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = {"/contextEntities/{entityID}/attributes/{attributeName}",
                     "/contextEntities/{entityID}/attributes/{attributeName}/{valueID}"},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<StatusCode> updateContextAttribute(@PathVariable String entityID,
            @PathVariable String attributeName,
            @PathVariable Optional<String> valueID,
            @RequestBody UpdateContextAttribute updateContextAttribute,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        ngsiValidation.checkUpdateContextAttribute(entityID, attributeName, Optional.empty(), updateContextAttribute);
        return new ResponseEntity<>(updateContextAttribute(entityID, attributeName, valueID, updateContextAttribute), HttpStatus.OK);
    }

    @RequestMapping( method = RequestMethod.GET,
            value = {"/contextEntities/{entityID}/attributes/{attributeName}",
                     "/contextEntities/{entityID}/attributes/{attributeName}/{valueID}"})
    final public ResponseEntity<ContextAttributeResponse> getContextAttribute(@PathVariable String entityID,
            @PathVariable String attributeName,
            @PathVariable Optional<String> valueID,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(getContextAttribute(entityID, attributeName, valueID), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,
            value = {"/contextEntities/{entityID}/attributes/{attributeName}",
                     "/contextEntities/{entityID}/attributes/{attributeName}/{valueID}"})
    final public ResponseEntity<StatusCode> deleteContextAttribute(@PathVariable String entityID,
            @PathVariable String attributeName,
            @PathVariable Optional<String> valueID,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(deleteContextAttribute(entityID, attributeName, valueID), HttpStatus.OK);
    }

    /* Entity types */

    @RequestMapping(method = RequestMethod.GET,
            value = {"/contextEntityTypes/{typeName}",
                     "/contextEntityTypes/{typeName}/attributes",
                     "/contextEntityTypes/{typeName}/attributes/{attributeName}"})
    final public ResponseEntity<QueryContextResponse> getContextEntityTypes(
            @PathVariable String typeName,
            @PathVariable Optional<String> attributeName,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(getContextEntitiesType(typeName, attributeName), HttpStatus.OK);
    }

    /* Subscriptions */

    @RequestMapping(method = RequestMethod.POST,
            value = "/contextSubscriptions",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<SubscribeContextResponse> createSubscription(
            @RequestBody SubscribeContext subscribeContext,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        ngsiValidation.checkSubscribeContext(subscribeContext);
        return new ResponseEntity<>(createSubscription(subscribeContext), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,
            value = "/contextSubscriptions/{subscriptionID}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<UpdateContextSubscriptionResponse> updateSubscription(
            @PathVariable String subscriptionID,
            @RequestBody UpdateContextSubscription updateContextSubscription,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        ngsiValidation.checkUpdateSubscription(subscriptionID, updateContextSubscription);
        return new ResponseEntity<>(updateSubscription(updateContextSubscription), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,
            value = "/contextSubscriptions/{subscriptionID}")
    final public ResponseEntity<UnsubscribeContextResponse> deleteSubscription(
            @PathVariable String subscriptionID,
            HttpServletRequest httpServletRequest) throws Exception {
        registerIntoDispatcher(httpServletRequest);
        return new ResponseEntity<>(deleteSubscription(subscriptionID), HttpStatus.OK);
    }

    /*
     * Exception handling
     */

    @ExceptionHandler({MissingRequestParameterException.class})
    public ResponseEntity<Object> missingParameter(HttpServletRequest req, MissingRequestParameterException missingException) {
        logger.error("Missing parameter: {}", missingException.getParameterName());
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_471, missingException.getParameterName(), missingException.getParameterType());
        return errorResponse(req.getRequestURI(), statusCode);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Object> messageNotReadableExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException exception) {
        logger.error("Message not readable: {}", exception.toString());
        return errorResponse(req.getRequestURI(), new StatusCode(CodeEnum.CODE_400));
    }

    @ExceptionHandler({UnsupportedOperationException.class})
    public ResponseEntity<Object> unsupportedOperation(HttpServletRequest req, UnsupportedOperationException exception) {
        logger.error("Unsupported operation: {}", exception.toString());
        return errorResponse(req.getRequestURI(), new StatusCode(CodeEnum.CODE_403));
    }

    @ExceptionHandler({MismatchIdException.class})
    public ResponseEntity<Object> mismatchIdException(HttpServletRequest req, MismatchIdException exception) {
        logger.error("Mismatch id: {}", exception.toString());
        return errorResponse(req.getRequestURI(), new StatusCode(CodeEnum.CODE_472, "subscriptionID"));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exceptionHandler(HttpServletRequest req, Exception exception) {
        logger.error("Exception handler: {}", exception);
        return errorResponse(req.getRequestURI(), new StatusCode(CodeEnum.CODE_500));
    }

    /*
     * Methods overridden by child classes to handle the NGSI v1 convenient REST requests
     */

    protected AppendContextElementResponse appendContextElement(String entityID,
            AppendContextElement appendContextElement) throws Exception {
        throw new UnsupportedOperationException("appendContextElement");
    }

    protected UpdateContextElementResponse updateContextElement(String entityID,
            UpdateContextElement updateContextElement) throws Exception {
        throw new UnsupportedOperationException("updateContextElement");
    }

    protected ContextElementResponse getContextElement(String entityID) throws Exception {
        throw new UnsupportedOperationException("getContextElement");
    }

    protected StatusCode deleteContextElement(String entityID) throws Exception {
        throw new UnsupportedOperationException("deleteContextElement");
    }

    protected StatusCode appendContextAttribute(String entityID, String attributeName,
            UpdateContextAttribute updateContextAttribute) throws Exception {
        throw new UnsupportedOperationException("appendContextAttribute");
    }

    protected StatusCode updateContextAttribute(final String entityID, String attributeName, Optional<String> valueID,
            UpdateContextAttribute updateContextElementRequest) throws Exception {
        throw new UnsupportedOperationException("updateContextAttribute");
    }

    protected ContextAttributeResponse getContextAttribute( String entityID, String attributeName, Optional<String> valueID) throws Exception {
        throw new UnsupportedOperationException("getContextAttribute");
    }

    protected StatusCode deleteContextAttribute(String entityID, String attributeName, Optional<String> valueID) throws Exception {
        throw new UnsupportedOperationException("deleteContextAttribute");
    }

    protected QueryContextResponse getContextEntitiesType(String typeName, Optional<String> attributeName) throws Exception {
        throw new UnsupportedOperationException("getContextEntitiesType");
    }

    protected SubscribeContextResponse createSubscription(final SubscribeContext subscribeContext) throws Exception {
        throw new UnsupportedOperationException("createSubscription");
    }

    protected UpdateContextSubscriptionResponse updateSubscription(
            UpdateContextSubscription updateContextSubscription) throws Exception {
        throw new UnsupportedOperationException("updateSubscription");
    }

    protected UnsubscribeContextResponse deleteSubscription(String subscriptionID) throws Exception {
        throw new UnsupportedOperationException("deleteSubscription");
    }

    /*
     * Other methods for use by child classes.
     */

    /**
     * Response for request error. NGSI requests require custom responses with 200 OK HTTP response code.
     */
    protected ResponseEntity<Object> errorResponse(String path, StatusCode statusCode) {
        return new ResponseEntity<>(statusCode, HttpStatus.OK);
    }

    /**
     * Register the host to protocolRegistry if it supports JSON
     * @param httpServletRequest the request
     */
    private void registerIntoDispatcher(HttpServletRequest httpServletRequest) {
        String uri = httpServletRequest.getRequestURI();

        // Use Accept or fallback to Content-Type if not defined
        String accept = httpServletRequest.getHeader("Accept");
        if (accept == null) {
            accept = httpServletRequest.getHeader("Content-Type");
        }

        if (accept != null && accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            protocolRegistry.registerHost(uri, true);
        }
    }
}

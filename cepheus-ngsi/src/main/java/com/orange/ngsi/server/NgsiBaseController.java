/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.orange.ngsi.Dispatcher;
import com.orange.ngsi.exception.MissingRequestParameterException;
import com.orange.ngsi.model.*;
import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the NGSI 9/10 requests
 */

public class NgsiBaseController {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Autowired
    private NgsiValidation ngsiValidation;

    @Autowired
    private Dispatcher dispatcher;

    /*
     * NGSI v1 API mapping
     */

    @RequestMapping(value = "/notifyContext", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<NotifyContextResponse> notifyContextRequest(@RequestBody final NotifyContext notify, @RequestHeader("Host") final String host, @RequestHeader("Accept") final String accept) throws Exception {
        dispatcher.addJsonHost(host, accept, true);
        ngsiValidation.checkNotifyContext(notify);
        return new ResponseEntity<>(notifyContext(notify), HttpStatus.OK);
    }

    @RequestMapping(value = "/updateContext", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<UpdateContextResponse> updateContextRequest(@RequestBody final UpdateContext updateContext, @RequestHeader("Host") final String host, @RequestHeader("Accept") final String accept) throws Exception {
        ngsiValidation.checkUpdateContext(updateContext);
        return new ResponseEntity<>(updateContext(updateContext), HttpStatus.OK);
    }

    @RequestMapping(value = "/registerContext", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<RegisterContextResponse> registerContextRequest(@RequestBody final RegisterContext registerContext, @RequestHeader("Host") final String host, @RequestHeader("Accept") final String accept) throws Exception {
        ngsiValidation.checkRegisterContext(registerContext);
        return new ResponseEntity<>(registerContext(registerContext), HttpStatus.OK);
    }

    @RequestMapping(value = "/subscribeContext", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<SubscribeContextResponse> subscribeContextRequest(@RequestBody final SubscribeContext subscribeContext, @RequestHeader("Host") final String host, @RequestHeader("Accept") final String accept) throws Exception {
        ngsiValidation.checkSubscribeContext(subscribeContext);
        return new ResponseEntity<>(subscribeContext(subscribeContext), HttpStatus.OK);
    }

    @RequestMapping(value = "/unsubscribeContext", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<UnsubscribeContextResponse> unsubscribeContextRequest(@RequestBody final UnsubscribeContext unsubscribeContext, @RequestHeader("Host") final String host, @RequestHeader("Accept") final String accept) throws Exception {
        ngsiValidation.checkUnsubscribeContext(unsubscribeContext);
        return new ResponseEntity<>(unsubscribeContext(unsubscribeContext), HttpStatus.OK);
    }

    @RequestMapping(value = "/queryContext", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    final public ResponseEntity<QueryContextResponse> queryContextRequest(@RequestBody final QueryContext queryContext, @RequestHeader("Host") final String host, @RequestHeader("Accept") final String accept) throws Exception {
        ngsiValidation.checkQueryContext(queryContext);
        return new ResponseEntity<>(queryContext(queryContext), HttpStatus.OK);
    }

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

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> exceptionHandler(HttpServletRequest req, Exception exception) {
        logger.error("Exception handler: {}", exception);
        return errorResponse(req.getRequestURI(), new StatusCode(CodeEnum.CODE_500));
    }

    /*
     * Methods that can be overridden by child classes to handle the NGSI v1 API
     */

    protected NotifyContextResponse notifyContext(final NotifyContext notify) throws Exception {
        throw new UnsupportedOperationException("notifyContext");
    }

    protected UpdateContextResponse updateContext(final UpdateContext update) throws Exception {
        throw new UnsupportedOperationException("updateContext");
    }

    protected RegisterContextResponse registerContext(final RegisterContext register) throws Exception {
        throw new UnsupportedOperationException("registerContext");
    }

    protected SubscribeContextResponse subscribeContext(final SubscribeContext subscribe) throws Exception {
        throw new UnsupportedOperationException("subscribeContext");
    }

    protected UnsubscribeContextResponse unsubscribeContext(final UnsubscribeContext unsubscribe) throws Exception {
        throw new UnsupportedOperationException("unsubscribeContext");
    }

    protected QueryContextResponse queryContext(final QueryContext query) throws Exception {
        throw new UnsupportedOperationException("queryContext");
    }

    /*
     * Other methods for use by child classes.
     */

    /**
     * Response for request error. NGSI requests require custom responses with 200 OK HTTP response code.
     */
    protected ResponseEntity<Object> errorResponse(String path, StatusCode statusCode) {
        Object entity;
        if (path.contains("/notifyContext")) {
            entity = new NotifyContextResponse(statusCode);
        } else if (path.contains("/updateContext")) {
            UpdateContextResponse updateContextResponse = new UpdateContextResponse();
            updateContextResponse.setErrorCode(statusCode);
            entity = updateContextResponse;
        } else if (path.contains("/registerContext")) {
            RegisterContextResponse registerContextResponse = new RegisterContextResponse();
            registerContextResponse.setErrorCode(statusCode);
            entity = registerContextResponse;
        } else if (path.contains("/subscribeContext")) {
            SubscribeContextResponse subscribeContextResponse = new SubscribeContextResponse();
            SubscribeError error = new SubscribeError();
            error.setErrorCode(statusCode);
            subscribeContextResponse.setSubscribeError(error);
            entity = subscribeContextResponse;
        } else if (path.contains("/unsubscribeContext")) {
            UnsubscribeContextResponse unsubscribeContextResponse = new UnsubscribeContextResponse();
            unsubscribeContextResponse.setStatusCode(statusCode);
            entity = unsubscribeContextResponse;
        } else if (path.contains("/queryContext")) {
            QueryContextResponse queryContextResponse = new QueryContextResponse();
            queryContextResponse.setErrorCode(statusCode);
            entity = queryContextResponse;
        } else {
            // All other non NGSI requests send back NotifyContextResponse
            entity = new NotifyContextResponse(statusCode);
        }
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }
}

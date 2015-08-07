/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.orange.ngsi.exception.MissingRequestParameterException;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for the NGSI 9/10 requests
 */

public class NgsiBaseController {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @Autowired
    private NgsiValidation ngsiValidation;

    /*
     * NGSI v1 API mapping
     */

    @RequestMapping(value = "/notifyContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    final public ResponseEntity<NotifyContextResponse> notifyContextRequest(@RequestBody final NotifyContext notify) throws Exception {
        ngsiValidation.checkNotifyContext(notify);
        return new ResponseEntity<>(notifyContext(notify), HttpStatus.OK);
    }

    @RequestMapping(value = "/updateContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    final public ResponseEntity<UpdateContextResponse> updateContextRequest(@RequestBody final UpdateContext updateContext) throws Exception {
        ngsiValidation.checkUpdateContext(updateContext);
        return new ResponseEntity<>(updateContext(updateContext), HttpStatus.OK);
    }

    @ExceptionHandler({MissingRequestParameterException.class})
    public ResponseEntity<Object> missingParameter(HttpServletRequest req, MissingRequestParameterException missingException) {
        logger.error("Missing parameter: {}", missingException.getParameterName());

        StatusCode statusCode = new StatusCode(CodeEnum.CODE_471, missingException.getParameterName(), missingException.getParameterType());
        return errorResponse(req.getRequestURI(), statusCode);
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
        } else {
            // All other non NGSI requests send back NotifyContextResponse
            entity = new NotifyContextResponse(statusCode);
        }
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }
}

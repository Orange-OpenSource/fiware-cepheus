/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.exception.MissingRequestParameterException;
import com.orange.espr4fastdata.exception.TypeNotFoundException;
import com.orange.ngsi.model.CodeEnum;
import com.orange.ngsi.model.NotifyContextResponse;
import com.orange.ngsi.model.StatusCode;
import com.orange.ngsi.model.UpdateContextResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pborscia on 06/07/2015.
 */
@ControllerAdvice("com.orange.espr4fastdata.controller")
public class NgsiHandlerException extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(NgsiHandlerException.class);

    @ExceptionHandler({MissingRequestParameterException.class})
    public ResponseEntity<Object> missingParameter(HttpServletRequest req, MissingRequestParameterException missingException) {
        logger.error("Missing parameter: {}", missingException.getParameterName());

        Object entity = entityForPath(req.getRequestURI(), new StatusCode(CodeEnum.CODE_471, missingException.getParameterName(), missingException.getParameterType()));
        return new ResponseEntity<Object>(entity, HttpStatus.OK);
    }

    @ExceptionHandler({TypeNotFoundException.class})
    public ResponseEntity<Object> invalidParameter(HttpServletRequest req, TypeNotFoundException typeNotFoundException) {
        logger.error("Type not found: {}", typeNotFoundException.getTypeName());

        Object entity = entityForPath(req.getRequestURI(), new StatusCode(CodeEnum.CODE_472, typeNotFoundException.getTypeName()));
        return new ResponseEntity<Object>(entity, HttpStatus.OK);
    }

    /**
     * Response for request error. NGSI requests require custom responses.
     */
    private Object entityForPath(String path, StatusCode statusCode) {

        if (path.contains("/notifyContext")) {
            return new NotifyContextResponse(statusCode);
        } else if (path.contains("/updateContext")) {
            UpdateContextResponse updateContextResponse = new UpdateContextResponse();
            updateContextResponse.setErrorCode(statusCode);
            return updateContextResponse;
        }

        // All other non NGSI requests send back NotifyContextResponse
        return new NotifyContextResponse(statusCode);
    }
}

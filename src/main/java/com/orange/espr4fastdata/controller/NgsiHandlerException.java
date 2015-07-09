/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.exception.MissingRequestParameterException;
import com.orange.espr4fastdata.model.ngsi.*;
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


    //@ExceptionHandler({Exception.class, Throwable.class})
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> missingParameter(HttpServletRequest req, Exception exception) {

        logger.info("Exception levee {}",exception);


        MissingRequestParameterException missing = (MissingRequestParameterException) exception;

        Object entity = entityForPath(req.getRequestURI(), new StatusCode(CodeEnum.CODE_471, missing.getParameterName(), missing.getParameterType()));
        return new ResponseEntity<Object>(entity, HttpStatus.OK);

    }


    /**
     * Response for request error. NGSI requests require custom responses.
     */
    private Object entityForPath(String path, StatusCode statusCode) {
        /*if (path.contains("/registerContext")) {
            RegisterContextResponse r = new RegisterContextResponse();
            r.setErrorCode(statusCode);
            return r;
        } else if (path.contains("/subscribeContext")) {
            SubscribeContextResponse r = new SubscribeContextResponse();
            SubscribeError e = new SubscribeError();
            e.setErrorCode(statusCode);
            r.setSubscribeError(e);
            return r;
        } else if (path.contains("/unsubscribeContext")) {
            UnsubscribeContextResponse r = new UnsubscribeContextResponse();
            r.setStatusCode(statusCode); // WTF?
            return r;
        } else if (path.contains("/updateContext")) {
            UpdateContextResponse r = new UpdateContextResponse();
            r.setErrorCode(statusCode);
            return r;
        } else if (path.contains("/queryContext")) {
            QueryContextResponse r = new QueryContextResponse();
            r.setErrorCode(statusCode);
            return r;
        }*/

        if (path.contains("/notifyContext")) {
            NotifyContextResponse r = new NotifyContextResponse();
            r.setResponseCode(statusCode);
            return r;
        } else if (path.contains("/updateContext")) {
            UpdateContextResponse r = new UpdateContextResponse();
            r.setErrorCode(statusCode);
            return r;
        }

        // All other non NGSI requests send back NotifyContextResponse
        NotifyContextResponse r = new NotifyContextResponse();
        r.setResponseCode(statusCode);
        return r;
    }
}

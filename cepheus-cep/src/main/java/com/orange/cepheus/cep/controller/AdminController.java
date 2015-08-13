/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.controller;

import com.orange.cepheus.cep.SubscriptionManager;
import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.ngsi.model.CodeEnum;
import com.orange.ngsi.model.StatusCode;
import com.orange.cepheus.cep.persistence.Persistence;
import com.orange.cepheus.cep.ComplexEventProcessor;
import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.model.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * Controller for management of the CEP
 */
@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    public ComplexEventProcessor complexEventProcessor;

    @Autowired
    public Persistence persistence;

    @Autowired
    public SubscriptionManager subscriptionManager;

    // Synchronization note:
    // All calls that modify configuration (which is a rare event compared to event processing) are simply synchronized.

    @RequestMapping(value = "/config", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public synchronized ResponseEntity<?> configuration(@Valid @RequestBody final Configuration configuration) throws ConfigurationException, PersistenceException {
        logger.info("Update configuration");

        complexEventProcessor.setConfiguration(configuration);

        subscriptionManager.setConfiguration(configuration);

        persistence.saveConfiguration(configuration);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public synchronized ResponseEntity<Configuration> configuration() {

        Configuration configuration = complexEventProcessor.getConfiguration();
        if (configuration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(configuration, HttpStatus.OK);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StatusCode> validationExceptionHandler(HttpServletRequest req, MethodArgumentNotValidException exception) {

        StringBuffer sb = new StringBuffer();
        for (ObjectError error : exception.getBindingResult().getAllErrors()) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append(error.getDefaultMessage());
        }
        logger.error("Configuration validation error: {}", sb.toString());

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("400");
        statusCode.setReasonPhrase("Configuration validation error");
        statusCode.setDetail(sb.toString());
        return new ResponseEntity<>(statusCode, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConfigurationException.class)
    public ResponseEntity<StatusCode> configurationExceptionHandler(HttpServletRequest req, ConfigurationException exception) {
        logger.error("Configuration error", exception);

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("400");
        statusCode.setReasonPhrase(exception.getMessage());
        if (exception.getCause() != null) {
            statusCode.setDetail(exception.getCause().getMessage());
        }
        return new ResponseEntity<>(statusCode, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<StatusCode> persistenceExceptionHandler(HttpServletRequest req, PersistenceException exception) {
        logger.error("Persistance error", exception);

        StatusCode statusCode = new StatusCode();
        statusCode.setCode("500");
        statusCode.setReasonPhrase(exception.getMessage());
        if (exception.getCause() != null) {
            statusCode.setDetail(exception.getCause().getMessage());
        }
        return new ResponseEntity<>(statusCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Object> messageNotReadableExceptionHandler(HttpServletRequest req, HttpMessageNotReadableException exception) {
        logger.error("Request not readable: {}", exception.toString());
        StatusCode statusCode = new StatusCode();
        statusCode.setCode("400");
        statusCode.setReasonPhrase(exception.getMessage());
        if (exception.getCause() != null) {
            statusCode.setDetail(exception.getCause().getMessage());
        }
        return new ResponseEntity<>(statusCode, HttpStatus.BAD_REQUEST);
    }
}

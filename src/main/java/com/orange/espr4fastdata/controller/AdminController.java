/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.exception.MissingRequestParameterException;
import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.model.ngsi.StatusCode;
import com.orange.espr4fastdata.persistence.Persistence;
import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.model.cep.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for management of the CEP
 */
@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private ComplexEventProcessor complexEventProcessor;

    private Persistence persistence;

    @Autowired
    public AdminController(ComplexEventProcessor complexEventProcessor, Persistence persistence) {
        this.complexEventProcessor = complexEventProcessor;
        this.persistence = persistence;
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> configuration(@RequestBody final Configuration configuration) throws ConfigurationException, PersistenceException {
        logger.debug("Updating configuration: {}", configuration);

        complexEventProcessor.setConfiguration(configuration);

        persistence.saveConfiguration(configuration);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Configuration> configuration() {

        Configuration configuration = complexEventProcessor.getConfiguration();
        if (configuration == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(configuration, HttpStatus.OK);
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

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public void setComplexEventProcessor(ComplexEventProcessor complexEventProcessor) {
        this.complexEventProcessor = complexEventProcessor;
    }
}

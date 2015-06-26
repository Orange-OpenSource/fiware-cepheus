package com.orange.espr4fastdata.controller;

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
@RequestMapping("/api/v1")
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final ComplexEventProcessor complexEventProcessor;

    @Autowired
    public AdminController(ComplexEventProcessor complexEventProcessor) {
        this.complexEventProcessor = complexEventProcessor;
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> configuration(@RequestBody final Configuration configuration) throws ConfigurationException {
        logger.debug("Updating configuration: {}", configuration);

        complexEventProcessor.setConfiguration(configuration);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ExceptionHandler(ConfigurationException.class)
    @ResponseStatus(value=HttpStatus.BAD_REQUEST,reason="Configuration error")
    public ModelAndView handleError(HttpServletRequest req, Exception exception) {
        logger.error("Request: configuration error", exception);

        ModelAndView mav = new ModelAndView();
        mav.addObject("exception", exception);
        mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return mav;
    }
}

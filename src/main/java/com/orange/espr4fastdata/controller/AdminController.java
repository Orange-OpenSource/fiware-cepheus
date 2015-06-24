package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.cep.ComplexEventProcessing;
import com.orange.espr4fastdata.model.cep.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AdminController {

    private static ComplexEventProcessing complexEventProcessing;

    public AdminController() {
        this.complexEventProcessing = new ComplexEventProcessing();
    }

    @RequestMapping(value = "/config", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> configuration(@RequestBody final Configuration configuration) {
        complexEventProcessing.setConfiguration(configuration);
        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }


}

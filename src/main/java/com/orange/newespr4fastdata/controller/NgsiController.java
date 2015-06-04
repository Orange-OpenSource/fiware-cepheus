package com.orange.newespr4fastdata.controller;

import com.orange.newespr4fastdata.cep.ComplexEventProcessing;
import com.orange.newespr4fastdata.model.ContextElement;
import com.orange.newespr4fastdata.model.ContextElementResponse;
import com.orange.newespr4fastdata.model.NotifyContext;
import com.orange.newespr4fastdata.model.StatusCode;
import com.orange.newespr4fastdata.model.cep.EventIn;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
@RestController
@RequestMapping("/api/v1/ngsi")
public class NgsiController {

    private static ComplexEventProcessing complexEventProcessing;

    public NgsiController() {
        this.complexEventProcessing = new ComplexEventProcessing();
    }

    @RequestMapping(value = "/notifyContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> notifyContext(@RequestBody final NotifyContext notify) {

        //check


        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<StatusCode>(StatusCode.CODE_200, httpHeaders, HttpStatus.OK);
    }


    private List<EventIn> createEventInFromNotifyContext(NotifyContext notifyContext){
        List<EventIn> eventIns = new ArrayList<EventIn>();

        for (ContextElementResponse contextElementResponse : notifyContext.getContextElementResponseList()){
            ContextElement contextElement = contextElementResponse.getContextElement();
            EventIn eventIn = new EventIn();
        }
        return eventIns;
    }
}

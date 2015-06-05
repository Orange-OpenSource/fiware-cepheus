package com.orange.newespr4fastdata.controller;

import com.orange.newespr4fastdata.cep.ComplexEventProcessing;
import com.orange.newespr4fastdata.model.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //send event in Esper
        List<EventIn> eventIns = createEventInFromNotifyContext(notify);
        for(EventIn eventIn : eventIns){
            complexEventProcessing.sendEventInEsper(eventIn);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<StatusCode>(StatusCode.CODE_200, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateContext(@RequestBody final UpdateContext update) {

        //check

        //send event in Esper
        List<EventIn> eventIns = createEventInFromUpdateContext(update);
        for(EventIn eventIn : eventIns){
            complexEventProcessing.sendEventInEsper(eventIn);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<StatusCode>(StatusCode.CODE_200, httpHeaders, HttpStatus.OK);
    }

    private List<EventIn> createEventInFromNotifyContext(NotifyContext notifyContext){
        List<EventIn> eventIns = new ArrayList<EventIn>();

        for (ContextElementResponse contextElementResponse : notifyContext.getContextElementResponseList()){
            ContextElement contextElement = contextElementResponse.getContextElement();
            EventIn eventIn = new EventIn();
            eventIn.setEventTypeName(contextElement.getEntityId().getType());

            eventIn.setAttributesMap(createAttributeMapFromContextElement(contextElementResponse.getContextElement()));

            eventIns.add(eventIn);
        }
        return eventIns;
    }

    private Map createAttributeMapFromContextElement(ContextElement contextElement){
        HashMap<String, Object> attributesMap = new HashMap<String, Object>();

        //add id in attributes
        attributesMap.put("id",contextElement.getEntityId().getId());

        List<ContextAttribute> contextAttributes = contextElement.getContextAttributeList();
        for(ContextAttribute contextAttribute : contextAttributes){
            attributesMap.put(contextAttribute.getName(),convertValueInObjectByType(contextAttribute.getContextValue(), contextAttribute.getType()));
        }
        return attributesMap;
    }

    private Object convertValueInObjectByType(String value, String type){
        switch (type) {
            case "string" : return value;
            case "boolean" : return new Boolean(value);
            case "int" : return Integer.valueOf(value);
            case "float" : return Float.valueOf(value);
            case "double" : return Double.valueOf(value);
            default : return value;
        }

    }

    private List<EventIn> createEventInFromUpdateContext(UpdateContext updateContext){
        List<EventIn> eventIns = new ArrayList<EventIn>();

        for (ContextElement contextElement : updateContext.getContextElements()){
            EventIn eventIn = new EventIn();
            eventIn.setEventTypeName(contextElement.getEntityId().getType());

            eventIn.setAttributesMap(createAttributeMapFromContextElement(contextElement));

            eventIns.add(eventIn);
        }
        return eventIns;
    }
}

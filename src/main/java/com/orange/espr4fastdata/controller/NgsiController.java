package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.cep.ComplexEventProcessing;
import com.orange.espr4fastdata.model.Event;
import com.orange.espr4fastdata.model.ngsi.*;
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
        List<Event> events = createEventInFromNotifyContext(notify);
        for(Event event : events){
            complexEventProcessing.processEvent(event);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<StatusCode>(StatusCode.CODE_200, httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/updateContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateContext(@RequestBody final UpdateContext update) {

        //check

        //send event in Esper
        List<Event> events = createEventInFromUpdateContext(update);
        for(Event event : events){
            complexEventProcessing.processEvent(event);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        return new ResponseEntity<StatusCode>(StatusCode.CODE_200, httpHeaders, HttpStatus.OK);
    }

    private List<Event> createEventInFromNotifyContext(NotifyContext notifyContext){
        List<Event> events = new ArrayList<Event>();

        for (ContextElementResponse contextElementResponse : notifyContext.getContextElementResponseList()){
            ContextElement contextElement = contextElementResponse.getContextElement();
            Event event = new Event();
            event.setType(contextElement.getEntityId().getType());

            event.setAttributes(createAttributeMapFromContextElement(contextElementResponse.getContextElement()));

            events.add(event);
        }
        return events;
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

    private List<Event> createEventInFromUpdateContext(UpdateContext updateContext){
        List<Event> events = new ArrayList<Event>();

        for (ContextElement contextElement : updateContext.getContextElements()){
            Event event = new Event();
            event.setType(contextElement.getEntityId().getType());

            event.setAttributes(createAttributeMapFromContextElement(contextElement));

            events.add(event);
        }
        return events;
    }
}

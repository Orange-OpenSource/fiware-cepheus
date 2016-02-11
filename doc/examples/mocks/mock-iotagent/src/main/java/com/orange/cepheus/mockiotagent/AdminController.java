/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.mockiotagent;

import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by pborscia on 20/08/2015.
 */
@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    private static int[] tempTab = {12,14,18,20,24,19};

    @Autowired
    NgsiClient ngsiClient;

    @Value("${cepheus.broker:http://localhost:8081}")
    String cepheusBroker;

    @Value("${iotagent:http://localhost:8083}")
    String providingApplication;

    @RequestMapping(value = "/registerRoom", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerRoom() throws URISyntaxException, ExecutionException, InterruptedException {
        RegisterContext registerContext = new RegisterContext();
        ContextRegistration registration = new ContextRegistration();
        registration.setProvidingApplication(new URI(providingApplication));
        EntityId entityId = new EntityId("Room*", "Room", true);
        registration.setEntityIdList(Collections.singletonList(entityId));
        List<ContextRegistrationAttribute> attributeList = new ArrayList<>();
        ContextRegistrationAttribute tempAttribute = new ContextRegistrationAttribute("temperature", false);
        tempAttribute.setType("double");
        attributeList.add(tempAttribute);
        ContextRegistrationAttribute floorAttribute = new ContextRegistrationAttribute("floor", false);
        floorAttribute.setType("string");
        attributeList.add(floorAttribute);
        ContextRegistrationAttribute shutterAttribute = new ContextRegistrationAttribute("shutter", false);
        shutterAttribute.setType("string");
        attributeList.add(shutterAttribute);
        registration.setContextRegistrationAttributeList(attributeList);
        registerContext.setContextRegistrationList(Collections.singletonList(registration));
        registerContext.setDuration("PT10M");
        RegisterContextResponse registerContextResponse = ngsiClient.registerContext(cepheusBroker, null, registerContext).get();
        logger.info("RegisterContextResponse of type {} received from {}: RegistrationId {}", "Shutter", cepheusBroker, registerContextResponse.getRegistrationId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/registerShutter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity registerShutter() throws ExecutionException, InterruptedException, URISyntaxException {

        RegisterContext registerContext = new RegisterContext();
        ContextRegistration registration = new ContextRegistration();
        registration.setProvidingApplication(new URI(providingApplication));
        EntityId entityId = new EntityId("Shutter*", "Shutter", true);
        registration.setEntityIdList(Collections.singletonList(entityId));
        ContextRegistrationAttribute attribute = new ContextRegistrationAttribute("status", false);
        attribute.setType("string");
        registration.setContextRegistrationAttributeList(Collections.singletonList(attribute));
        registerContext.setContextRegistrationList(Collections.singletonList(registration));
        registerContext.setDuration("PT10M");
        RegisterContextResponse registerContextResponse = ngsiClient.registerContext(cepheusBroker, null, registerContext).get();
        logger.info("RegisterContextResponse of type {} received from {}: RegistrationId {}", "Shutter", cepheusBroker, registerContextResponse.getRegistrationId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/updateRoom", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity updateRoom() throws ExecutionException, InterruptedException, URISyntaxException {

        for(int varTemp: tempTab) {
            Thread.sleep(5000);
            for (int varRoom = 1 ; varRoom < 5 ; varRoom++) {
                for (int varFloor = 1 ; varFloor < 4 ; varFloor++){
                    UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
                    ContextElement contextElement = new ContextElement();
                    String name = "Room" + varFloor + varRoom;
                    EntityId entityId = new EntityId(name, "Room", false);
                    contextElement.setEntityId(entityId);
                    int value = ((varTemp + (2*varFloor) + varRoom));
                    ContextAttribute tempContextAttribute = new ContextAttribute("temperature","double", String.valueOf(value));
                    ContextAttribute floorContextAttribute = new ContextAttribute("floor","string", "Floor" + varFloor);
                    ContextAttribute shutterContextAttribute = new ContextAttribute("shutter","string", "Shutter" + varFloor + varRoom);
                    List<ContextAttribute> contextAttributeList = new ArrayList<>();
                    contextAttributeList.add(tempContextAttribute);
                    contextAttributeList.add(floorContextAttribute);
                    contextAttributeList.add(shutterContextAttribute);
                    contextElement.setContextAttributeList(contextAttributeList);
                    updateContext.setContextElements(Collections.singletonList(contextElement));
                    ngsiClient.updateContext(cepheusBroker , null, updateContext);
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity responseKO() {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

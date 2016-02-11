/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.mockiotagent;

import com.orange.ngsi.model.*;
import com.orange.ngsi.server.NgsiBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * NGSI Controller : NGSI operation implemented by mock-iotagent
 */
@RestController
@RequestMapping(value = {"/v1","/ngsi10"})
public class NgsiController extends NgsiBaseController {
    private static Logger logger = LoggerFactory.getLogger(NgsiController.class);

    private static int[] tempTab = {12,14,18,20,24,19};

    private String[] statusTab = {"open", "closed"};

    @Override
    public UpdateContextResponse updateContext(final UpdateContext update) {

        //send response with status 200 = OK
        UpdateContextResponse updateContextResponse = new UpdateContextResponse();
        List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_200);
        for (ContextElement contextElement : update.getContextElements()) {
            logger.info("=> updateContext: {}", contextElement.toString());
            contextElementResponseList.add(new ContextElementResponse(contextElement, statusCode));
        }
        updateContextResponse.setContextElementResponses(contextElementResponseList);
        return updateContextResponse;
    }

    @Override
    public QueryContextResponse queryContext(final QueryContext query) {
        logger.info("=> queryContext: {}", query.toString());

        //check if the incoming query is on Room
        EntityId queryEntityId = query.getEntityIdList().get(0);
        if (queryEntityId.getId().contains("Room")) {
            return createRoomQueryResponse(queryEntityId);
        } else if (queryEntityId.getId().contains("Shutter")) {
            return createShutterQueryResponse(queryEntityId);
        } else {
            QueryContextResponse queryResponse = new QueryContextResponse();
            queryResponse.setErrorCode(new StatusCode(CodeEnum.CODE_404, queryEntityId.getId()));
            return queryResponse;
        }
    }

    private QueryContextResponse createRoomQueryResponse(EntityId queryEntityId) {
        QueryContextResponse queryContextResponse = new QueryContextResponse();

        List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
        if ((queryEntityId.getIsPattern()) && (queryEntityId.getId().equals("Room*"))){
            for(int varTemp: tempTab) {
                for (int varRoom = 1 ; varRoom < 5 ; varRoom++) {
                    for (int varFloor = 1 ; varFloor < 4 ; varFloor++){
                        ContextElement contextElement = new ContextElement();
                        String name = "Room" + varFloor + varRoom;
                        EntityId entityId = new EntityId(name, "Room", false);
                        contextElement.setEntityId(entityId);
                        int value = ((varTemp + (2*varFloor) + varRoom));
                        ContextAttribute tempContextAttribute = new ContextAttribute("temperature","double", String.valueOf(value));
                        ContextAttribute floorContextAttribute = new ContextAttribute("floor","string", "Floor"+varFloor);
                        List<ContextAttribute> contextAttributeList = new ArrayList<>();
                        contextAttributeList.add(tempContextAttribute);
                        contextAttributeList.add(floorContextAttribute);
                        contextElement.setContextAttributeList(contextAttributeList);
                        ContextElementResponse contextElementResponse = new ContextElementResponse();
                        contextElementResponse.setContextElement(contextElement);
                        contextElementResponse.setStatusCode(new StatusCode(CodeEnum.CODE_200));
                        contextElementResponseList.add(contextElementResponse);
                    }
                }
            }
        } else {
            String id = queryEntityId.getId();
            int varFloor = Integer.parseInt(String.valueOf(id.charAt(4)));
            int varRoom = Integer.parseInt(String.valueOf(id.charAt(5)));
            Random rand = new Random();
            int i=rand.nextInt(tempTab.length);
            int varTemp = tempTab[i];
            ContextElement contextElement = new ContextElement();

            contextElement.setEntityId(queryEntityId);
            int value = ((varTemp + (2*varFloor) + varRoom));
            ContextAttribute tempContextAttribute = new ContextAttribute("temperature","double", String.valueOf(value));
            ContextAttribute floorContextAttribute = new ContextAttribute("floor","string", "Floor"+varFloor);
            List<ContextAttribute> contextAttributeList = new ArrayList<>();
            contextAttributeList.add(tempContextAttribute);
            contextAttributeList.add(floorContextAttribute);
            contextElement.setContextAttributeList(contextAttributeList);
            ContextElementResponse contextElementResponse = new ContextElementResponse();
            contextElementResponse.setContextElement(contextElement);
            contextElementResponse.setStatusCode(new StatusCode(CodeEnum.CODE_200));
            contextElementResponseList.add(contextElementResponse);
        }
        queryContextResponse.setContextElementResponses(contextElementResponseList);
        return queryContextResponse;
    }

    private QueryContextResponse createShutterQueryResponse(EntityId queryEntityId) {
        QueryContextResponse queryContextResponse = new QueryContextResponse();

        List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
        Random rand = new Random();
        if ((queryEntityId.getIsPattern()) && (queryEntityId.getId().equals("Shutter*"))){
            for (int varRoom = 1 ; varRoom < 5 ; varRoom++) {
                for (int varFloor = 1 ; varFloor < 4 ; varFloor++){
                    ContextElement contextElement = new ContextElement();
                    String name = "Shutter" + varFloor + varRoom;
                    EntityId entityId = new EntityId(name, "Room", false);
                    contextElement.setEntityId(entityId);
                    int i=rand.nextInt(statusTab.length);
                    String varStatus = statusTab[i];
                    ContextAttribute statusContextAttribute = new ContextAttribute("status","string", varStatus);
                    List<ContextAttribute> contextAttributeList = new ArrayList<>();
                    contextAttributeList.add(statusContextAttribute);
                    contextElement.setContextAttributeList(contextAttributeList);
                    ContextElementResponse contextElementResponse = new ContextElementResponse();
                    contextElementResponse.setContextElement(contextElement);
                    contextElementResponse.setStatusCode(new StatusCode(CodeEnum.CODE_200));
                    contextElementResponseList.add(contextElementResponse);
                }
            }

        } else {
            int i=rand.nextInt(statusTab.length);
            String varStatus = statusTab[i];
            ContextElement contextElement = new ContextElement();
            contextElement.setEntityId(queryEntityId);
            ContextAttribute statusContextAttribute = new ContextAttribute("status","string", varStatus);
            List<ContextAttribute> contextAttributeList = new ArrayList<>();
            contextAttributeList.add(statusContextAttribute);
            contextElement.setContextAttributeList(contextAttributeList);
            ContextElementResponse contextElementResponse = new ContextElementResponse();
            contextElementResponse.setContextElement(contextElement);
            contextElementResponse.setStatusCode(new StatusCode(CodeEnum.CODE_200));
            contextElementResponseList.add(contextElementResponse);
        }
        queryContextResponse.setContextElementResponses(contextElementResponseList);
        return queryContextResponse;
    }
}

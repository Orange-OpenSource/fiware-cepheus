/*
 * Copyright (C) 2016 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.controller;

import com.orange.ngsi.exception.UnsupportedOperationException;
import com.orange.ngsi.model.*;
import com.orange.ngsi.server.NgsiRestBaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * NGSI REST convenient operations
 *  - only NGSI-10, no NGSI-9
 *  - forwarding all convenient requests to standard operations (updateContext, queryContext, ...)
 */
@RestController
@RequestMapping(value = {"/v1", "/ngsi10", "/NGSI10"})
public class NgsiRestController extends NgsiRestBaseController {

    @Autowired
    private NgsiController ngsiController;

    @Override
    protected AppendContextElementResponse appendContextElement(String entityID, AppendContextElement appendContextElement) throws Exception {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(entityID, "", false));
        contextElement.setContextAttributeList(appendContextElement.getAttributeList());

        UpdateContext updateContext = new UpdateContext();
        updateContext.setContextElements(Collections.singletonList(contextElement));
        updateContext.setUpdateAction(UpdateAction.APPEND);

        UpdateContextResponse updateContextResponse = ngsiController.updateContext(updateContext);


        // Transform an UpdateContextResponse to a AppendContextElementResponse...
        AppendContextElementResponse appendContextElementResponse = new AppendContextElementResponse();
        if (updateContextResponse.getErrorCode() != null) {
            appendContextElementResponse.setErrorCode(updateContextResponse.getErrorCode());
        } else {
            // There should be only one ContextElementResponse
            List <ContextAttributeResponse> attributeResponses = new LinkedList<>();
            updateContextResponse.getContextElementResponses().forEach(response -> {
                appendContextElementResponse.setEntityId(response.getContextElement().getEntityId());
                response.getContextElement().getContextAttributeList().forEach(contextAttribute -> {
                            ContextAttributeResponse attributeResponse = new ContextAttributeResponse();
                            attributeResponse.setContextAttributeList(response.getContextElement().getContextAttributeList());
                            attributeResponse.setStatusCode(response.getStatusCode());
                            attributeResponses.add(attributeResponse);
                        });
                    });
            appendContextElementResponse.setContextAttributeResponses(attributeResponses);
        }
        return appendContextElementResponse;
    }

    @Override
    protected UpdateContextElementResponse updateContextElement(String entityID, UpdateContextElement updateContextElement) throws Exception {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(entityID, "", false));
        contextElement.setContextAttributeList(updateContextElement.getContextAttributes());

        UpdateContext updateContext = new UpdateContext();
        updateContext.setContextElements(Collections.singletonList(contextElement));
        updateContext.setUpdateAction(UpdateAction.UPDATE);

        UpdateContextResponse updateContextResponse = ngsiController.updateContext(updateContext);

        // Transform an UpdateContextResponse to a AppendContextElementResponse...
        UpdateContextElementResponse updateContextElementResponse = new UpdateContextElementResponse();
        if (updateContextResponse.getErrorCode() != null) {
            updateContextElementResponse.setErrorCode(updateContextResponse.getErrorCode());
        } else {
            // There should be a single ContextElementResponse
            List <ContextAttributeResponse> attributeResponses = new LinkedList<>();
            updateContextResponse.getContextElementResponses().forEach(response -> {
                response.getContextElement().getContextAttributeList().forEach(contextAttribute -> {
                    ContextAttributeResponse attributeResponse = new ContextAttributeResponse();
                    attributeResponse.setContextAttributeList(response.getContextElement().getContextAttributeList());
                    attributeResponse.setStatusCode(response.getStatusCode());
                    attributeResponses.add(attributeResponse);
                });
            });
            updateContextElementResponse.setContextAttributeResponses(attributeResponses);
        }
        return updateContextElementResponse;
    }

    @Override
    protected ContextElementResponse getContextElement(String entityID) throws Exception {

        QueryContext queryContext = new QueryContext((Collections.singletonList(new EntityId(entityID, "", false))));

        QueryContextResponse response = ngsiController.queryContext(queryContext);

        // Transform a QueryContextResponse to a ContextElementResponse
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        if (response.getErrorCode() != null) {
            contextElementResponse.setStatusCode(response.getErrorCode());
        } else {
            // There should be a single ContextElementResponse
            contextElementResponse.setContextElement(response.getContextElementResponses().get(0).getContextElement());
            contextElementResponse.setStatusCode(response.getContextElementResponses().get(0).getStatusCode());
        }
        return contextElementResponse;
    }

    @Override
    protected StatusCode deleteContextElement(String entityID) throws Exception {

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(entityID, "", false));

        UpdateContext updateContext = new UpdateContext();
        updateContext.setContextElements(Collections.singletonList(contextElement));
        updateContext.setUpdateAction(UpdateAction.DELETE);

        UpdateContextResponse response = ngsiController.updateContext(updateContext);

        // Transform an UpdateContextResponse to a StatusCode
        if (response.getErrorCode() != null) {
            return response.getErrorCode();
        }
        // There should be a single ContextElementResponse
        return response.getContextElementResponses().get(0).getStatusCode();
    }

    @Override
    protected StatusCode appendContextAttribute(String entityID, String attributeName, UpdateContextAttribute updateContextAttribute) throws Exception {

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(entityID, "", false));
        contextElement.setContextAttributeList(Collections.singletonList(updateContextAttribute.getAttribute()));

        UpdateContext updateContext = new UpdateContext();
        updateContext.setContextElements(Collections.singletonList(contextElement));
        updateContext.setUpdateAction(UpdateAction.APPEND);

        UpdateContextResponse updateContextResponse = ngsiController.updateContext(updateContext);

        // Transform an UpdateContextResponse to a AppendContextElementResponse...
        AppendContextElementResponse appendContextElementResponse = new AppendContextElementResponse();
        if (updateContextResponse.getErrorCode() != null) {
            return updateContextResponse.getErrorCode();
        }
        // There should be only one ContextElementResponse
        return updateContextResponse.getContextElementResponses().get(0).getStatusCode();
    }

    @Override
    protected StatusCode updateContextAttribute(String entityID, String attributeName,
            UpdateContextAttribute updateContextAttribute) throws Exception {
        return this.updateContextAttributeValue(entityID, attributeName, null, updateContextAttribute);
    }

    @Override
    protected ContextAttributeResponse getContextAttribute(String entityID, String attributeName) throws Exception {

        QueryContext queryContext = new QueryContext((Collections.singletonList(new EntityId(entityID, "", false))));
        queryContext.setAttributeList(Collections.singletonList(attributeName));

        QueryContextResponse response = ngsiController.queryContext(queryContext);

        // Transform a QueryContextResponse to a ContextAttributeResponse
        ContextAttributeResponse contextAttributeResponse = new ContextAttributeResponse();
        if (response.getErrorCode() != null) {
            contextAttributeResponse.setStatusCode(response.getErrorCode());
        } else {
            // There should be a single ContextElementResponse
            contextAttributeResponse.setStatusCode(response.getContextElementResponses().get(0).getStatusCode());
            contextAttributeResponse.setContextAttributeList(response.getContextElementResponses().get(0).getContextElement().getContextAttributeList());
        }
        return contextAttributeResponse;
    }

    @Override
    protected StatusCode updateContextAttributeValue(String entityID, String attributeName, String valueID,
            UpdateContextAttribute updateContextAttribute) throws Exception {

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(entityID, "", false));
        contextElement.setContextAttributeList(Collections.singletonList(updateContextAttribute.getAttribute()));

        UpdateContext updateContext = new UpdateContext();
        updateContext.setContextElements(Collections.singletonList(contextElement));
        updateContext.setUpdateAction(UpdateAction.UPDATE);

        UpdateContextResponse updateContextResponse = ngsiController.updateContext(updateContext);

        // Transform an UpdateContextResponse to a AppendContextElementResponse...
        AppendContextElementResponse appendContextElementResponse = new AppendContextElementResponse();
        if (updateContextResponse.getErrorCode() != null) {
            return updateContextResponse.getErrorCode();
        }
        // There should be only one ContextElementResponse
        return updateContextResponse.getContextElementResponses().get(0).getStatusCode();
    }

    @Override
    protected StatusCode deleteContextAttribute(String entityID, String attributeName) throws Exception {
        return this.deleteContextAttributeValue(entityID, attributeName, null);
    }

    @Override
    protected ContextAttributeResponse getContextAttributeValue(String entityID, String attributeName, String valueID) throws Exception {
        // QueryContext cannot handle filtering by metadata ID
        throw new UnsupportedOperationException("cannot handle valueID");
    }

    @Override
    protected StatusCode deleteContextAttributeValue(String entityID, String attributeName, String valueID) throws Exception {

        ContextAttribute contextAttribute = new ContextAttribute(attributeName, "", "");
        if (valueID != null) {
            contextAttribute.setMetadata(Collections.singletonList(new ContextMetadata("ID", "string", valueID)));
        }

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(entityID, "", false));
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));

        UpdateContext updateContext = new UpdateContext();
        updateContext.setContextElements(Collections.singletonList(contextElement));
        updateContext.setUpdateAction(UpdateAction.DELETE);

        UpdateContextResponse updateContextResponse = ngsiController.updateContext(updateContext);

        if (updateContextResponse.getErrorCode() != null) {
            return updateContextResponse.getErrorCode();
        }
        // There should be only a single ContextElementResponse
        return updateContextResponse.getContextElementResponses().get(0).getStatusCode();
    }

    @Override
    protected QueryContextResponse getContextEntitiesType(String typeName) throws Exception {
        return this.getContextEntitiesType(typeName, null);
    }

    @Override
    protected QueryContextResponse getContextEntitiesType(String typeName, String attributeName) throws Exception {
        QueryContext queryContext = new QueryContext((Collections.singletonList(new EntityId(".*", typeName, true))));
        if (attributeName != null) {
            queryContext.setAttributeList(Collections.singletonList(attributeName));
        }
        return ngsiController.queryContext(queryContext);
    }

    @Override
    protected SubscribeContextResponse createSubscription(SubscribeContext subscribeContext) throws Exception {
        return ngsiController.subscribeContext(subscribeContext);
    }

    @Override
    protected UpdateContextSubscriptionResponse updateSubscription(UpdateContextSubscription updateContextSubscription) throws Exception {
        return ngsiController.updateContextSubscription(updateContextSubscription);
    }

    @Override
    protected UnsubscribeContextResponse deleteSubscription(String subscriptionID) throws Exception {
        return ngsiController.unsubscribeContext(new UnsubscribeContext(subscriptionID));
    }
}

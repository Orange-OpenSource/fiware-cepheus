/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * NGSI REST convenient operations
 *  - NGSI-10 operations only
 *  - except attribute domains operations
 */
@Service
public class NgsiRestClient extends NgsiClient {

    private static Logger logger = LoggerFactory.getLogger(NgsiRestClient.class);

    private final static String basePath = "/ngsi10";
    private final static String entitiesPath = basePath + "/contextEntities/";
    private final static String entityTypesPath = basePath + "/contextEntityTypes/";
    private final static String subscriptionsPath = basePath + "/contextSubscriptions/";
    private final static String attributesPath = "/attributes/";
    private final static String valuesPath = "/";

    /*
     * Context Entities operations
     */

    /**
     * Append attributes to a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param appendContextElement attributes to append
     * @return a future for an AppendContextElementResponse
     */
    public ListenableFuture<AppendContextElementResponse> appendContextElement(String url, HttpHeaders httpHeaders, String entityID, AppendContextElement appendContextElement) {
        return request(HttpMethod.POST, url + entitiesPath + entityID, httpHeaders, appendContextElement, AppendContextElementResponse.class);
    }

    /**
     * Append attributes to a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param updateContextElement attributes to update
     * @return a future for an UpdateContextElementResponse
     */
    public ListenableFuture<UpdateContextElementResponse> updateContextElement(String url, HttpHeaders httpHeaders, String entityID, UpdateContextElement updateContextElement) {
        return request(HttpMethod.PUT, url + entitiesPath + entityID, httpHeaders, updateContextElement, UpdateContextElementResponse.class);
    }

    /**
     * Retrieve a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @return a future for a ContextElementResponse
     */
    public ListenableFuture<ContextElementResponse> getContextElement(String url, HttpHeaders httpHeaders, String entityID) {
        return request(HttpMethod.GET, url + entitiesPath + entityID, httpHeaders, null, ContextElementResponse.class);
    }

    /**
     * Delete a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @return a future for a StatusCode
     */
    public ListenableFuture<StatusCode> deleteContextElement(String url, HttpHeaders httpHeaders, String entityID) {
        return request(HttpMethod.DELETE, url + entitiesPath + entityID, httpHeaders, null, StatusCode.class);
    }

    /*
     * Context Attributes operations
     */

    /**
     * Append an attribute to a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param attributeName the name of the attribute
     * @param updateContextAttribute attribute to append
     * @return a future for a StatusCode
     */
    public ListenableFuture<StatusCode> appendContextAttribute(String url, HttpHeaders httpHeaders, String entityID, String attributeName, UpdateContextAttribute updateContextAttribute) {
        return request(HttpMethod.POST, url + entitiesPath + entityID + attributesPath + attributeName, httpHeaders, updateContextAttribute, StatusCode.class);
    }

    /**
     * Update the attribute of a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param attributeName the name of the attribute
     * @param updateContextAttribute attribute to update
     * @return a future for a StatusCode
     */
    public ListenableFuture<StatusCode> updateContextAttribute(String url, HttpHeaders httpHeaders, String entityID, String attributeName, UpdateContextAttribute updateContextAttribute) {
        return request(HttpMethod.PUT, url + entitiesPath + entityID + attributesPath + attributeName, httpHeaders, updateContextAttribute, StatusCode.class);
    }

    /**
     * Retrieve an attribute of a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param attributeName the name of the attribute
     * @return a future for a ContextAttributeResponse
     */
    public ListenableFuture<ContextAttributeResponse> getContextAttribute(String url, HttpHeaders httpHeaders, String entityID, String attributeName) {
        return request(HttpMethod.GET, url + entitiesPath + entityID + attributesPath + attributeName, httpHeaders, null, ContextAttributeResponse.class);
    }

    /**
     * Retrieve an attribute of a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param attributeName the name of the attribute
     * @return a future for a StatusCode
     */
    public ListenableFuture<StatusCode> deleteContextAttribute(String url, HttpHeaders httpHeaders, String entityID, String attributeName) {
        return request(HttpMethod.DELETE, url + entitiesPath + entityID + attributesPath + attributeName, httpHeaders, null, StatusCode.class);
    }

    /*
     * Context Attribute Value Instance operations
     */

    /**
     * Update the attribute value instance of a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param attributeName the name of the attribute
     * @param valueID the instant ID of the attribute
     * @param updateContextAttribute attribute values to update
     * @return a future for a StatusCode
     */
    public ListenableFuture<StatusCode> updateContextAttributeValue(String url, HttpHeaders httpHeaders, String entityID, String attributeName, String valueID, UpdateContextAttribute updateContextAttribute) {
        return request(HttpMethod.PUT, url + entitiesPath + entityID + attributesPath + attributeName + valuesPath + valueID, httpHeaders, updateContextAttribute, StatusCode.class);
    }

    /**
     * Retrieve the attribute value instance of a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param attributeName the name of the attribute
     * @param valueID the instant ID of the attribute
     * @return a future for a ContextAttributeResponse
     */
    public ListenableFuture<ContextAttributeResponse> getContextAttributeValue(String url, HttpHeaders httpHeaders, String entityID, String attributeName, String valueID) {
        return request(HttpMethod.GET, url + entitiesPath + entityID + attributesPath + attributeName + valuesPath + valueID, httpHeaders, null, ContextAttributeResponse.class);
    }

    /**
     * Delete the attribute value instance of a context element
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param entityID the ID of the entity
     * @param attributeName the name of the attribute
     * @param valueID the instant ID of the attribute
     * @return a future for a StatusCode
     */
    public ListenableFuture<StatusCode> deleteContextAttributeValue(String url, HttpHeaders httpHeaders, String entityID, String attributeName, String valueID) {
        return request(HttpMethod.DELETE, url + entitiesPath + entityID + attributesPath + attributeName + valuesPath + valueID, httpHeaders, null, StatusCode.class);
    }

    /*
     * Context Entity Types operations
     */

    /**
     * Retrieve a context entity type
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param typeName the name of the entity type
     * @return a future for a ContextElementResponse
     */
    public ListenableFuture<QueryContextResponse> getContextEntityType(String url, HttpHeaders httpHeaders, String typeName) {
        return request(HttpMethod.GET, url + entityTypesPath + typeName, httpHeaders, null, QueryContextResponse.class);
    }

    /**
     * Retrieve a context entity type attribute
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param typeName the name of the entity type
     * @param attributeName the name of the attribute
     * @return a future for a ContextElementResponse
     */
    public ListenableFuture<QueryContextResponse> getContextEntityTypeAttribute(String url, HttpHeaders httpHeaders, String typeName, String attributeName) {
        return request(HttpMethod.GET, url + entityTypesPath + typeName + attributesPath + attributeName, httpHeaders, null, QueryContextResponse.class);
    }

    /*
     * Subscriptions operations
     */

    /**
     * Add a subscription
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscribeContext the parameters for subscription
     * @return a future for an SubscribeContextResponse
     */
    public ListenableFuture<SubscribeContextResponse> appendContextSubscription(String url, HttpHeaders httpHeaders, SubscribeContext subscribeContext) {
        return request(HttpMethod.POST, url + subscriptionsPath, httpHeaders, subscribeContext, SubscribeContextResponse.class);
    }

    /**
     * Update a subscription
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscriptionID the ID of the subscription to update
     * @param updateContextSubscription the parameters to update
     * @return a future for an UpdateContextSubscriptionResponse
     */
    public ListenableFuture<UpdateContextSubscriptionResponse> updateContextSubscription(String url, HttpHeaders httpHeaders, String subscriptionID, UpdateContextSubscription updateContextSubscription) {
        return request(HttpMethod.PUT, url + subscriptionsPath + subscriptionID, httpHeaders, updateContextSubscription, UpdateContextSubscriptionResponse.class);
    }

    /**
     * Delete a subscription
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscriptionID the ID of the subscription to delete
     * @return a future for an UnsubscribeContextResponse
     */
    public ListenableFuture<UnsubscribeContextResponse> deleteContextSubscription(String url, HttpHeaders httpHeaders, String subscriptionID) {
        return request(HttpMethod.DELETE, url + subscriptionsPath + subscriptionID, httpHeaders, null, UnsubscribeContextResponse.class);
    }
}

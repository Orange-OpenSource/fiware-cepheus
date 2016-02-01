/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.ProtocolRegistry;
import com.orange.ngsi.model.*;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureAdapter;
import org.springframework.web.client.AsyncRestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Provides methods to request a NGSI 10 v1 component (supporting JSON)
 */
@Service
public class NgsiClient {

    private static Logger logger = LoggerFactory.getLogger(NgsiClient.class);

    @Autowired
    public AsyncRestTemplate asyncRestTemplate;

    @Autowired
    public PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager;

    @Autowired
    public ProtocolRegistry protocolRegistry;

    /**
     * Let some time for the client to shutdown gracefully
     * @throws IOException
     */
    public void shutdownGracefully() throws IOException {
        poolingNHttpClientConnectionManager.shutdown(10000);
    }

    /**
     * Send an update request
     * @param url the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param updateContext the UpdateContext
     * @return a future for an UpdateContextReponse
     */
    public ListenableFuture<UpdateContextResponse> updateContext(String url, HttpHeaders httpHeaders, UpdateContext updateContext) {
        return request(url + "/ngsi10/updateContext", httpHeaders, updateContext, UpdateContextResponse.class);
    }

    /**
     * Send a subscription request
     * @param url the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscribeContext the SubscriptionContext
     * @return a future for a SubscribeContextResponse
     */
    public ListenableFuture<SubscribeContextResponse> subscribeContext(String url, HttpHeaders httpHeaders, SubscribeContext subscribeContext) {
        return request(url + "/ngsi10/subscribeContext", httpHeaders, subscribeContext, SubscribeContextResponse.class);
    }

    /**
     * Send a update subscription request
     * @param url the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param updateContextSubscription the UpdateContextSubscription
     * @return a future for a UpdateContextSubscriptionResponse
     */
    public ListenableFuture<UpdateContextSubscriptionResponse> updateContextSubscription(String url, HttpHeaders httpHeaders, UpdateContextSubscription updateContextSubscription) {
        return request(url + "/ngsi10/updateContextSubscription", httpHeaders, updateContextSubscription, UpdateContextSubscriptionResponse.class);
    }

    /**
     * Send an unsubscription request
     * @param url the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscriptionId the subscription ID to unsubscribe
     * @return a future for a UnsubscribeContextResponse
     */
    public ListenableFuture<UnsubscribeContextResponse> unsubscribeContext(String url, HttpHeaders httpHeaders, String subscriptionId) {
        return request(url + "/ngsi10/unsubscribeContext", httpHeaders, new UnsubscribeContext(subscriptionId), UnsubscribeContextResponse.class);
    }

    /**
     * Send an notify request
     * @param url the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param notifyContext the notifyContext
     * @return a future for a NotifyContextResponse
     */
    public ListenableFuture<NotifyContextResponse> notifyContext(String url, HttpHeaders httpHeaders, NotifyContext notifyContext) {
        return request(url + "/ngsi10/notifyContext", httpHeaders, notifyContext, NotifyContextResponse.class);
    }

    /**
     * Send an query request
     * @param url the URL of the registration provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param queryContext the queryContext
     * @return a future for a QueryContextResponse
     */
    public ListenableFuture<QueryContextResponse> queryContext(String url, HttpHeaders httpHeaders, QueryContext queryContext) {
        return request(url + "/ngsi10/queryContext", httpHeaders, queryContext, QueryContextResponse.class);
    }

    /**
     * Send an register request
     * @param url the URL of the registration consumer
     * @param httpHeaders the HTTP header to use, or null for default
     * @param registerContext the registerContext
     * @return a future for a RegisterContextResponse
     */
    public ListenableFuture<RegisterContextResponse> registerContext(String url, HttpHeaders httpHeaders, RegisterContext registerContext) {
        return request(url + "/ngsi9/registerContext", httpHeaders, registerContext, RegisterContextResponse.class);
    }

    /**
     * The default HTTP request headers used for the requests.
     * @return the HTTP request headers.
     */
    public HttpHeaders getRequestHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_XML);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_XML));
        return requestHeaders;
    }

    /**
     * The default HTTP request headers, depends on the host supporting xml or json
     * @param url
     * @return the HTTP request headers.
     */
    public HttpHeaders getRequestHeaders(String url) {
        HttpHeaders requestHeaders = new HttpHeaders();

        MediaType mediaType = MediaType.APPLICATION_JSON;
        if (url == null || protocolRegistry.supportXml(url)) {
            mediaType = MediaType.APPLICATION_XML;
        }
        requestHeaders.setContentType(mediaType);
        requestHeaders.setAccept(Collections.singletonList(mediaType));
        return requestHeaders;
    }

    /**
     * Default POST request
     */
    protected <T,U> ListenableFuture<T> request(String url, HttpHeaders httpHeaders, U body, Class<T> responseType) {
        return request(HttpMethod.POST, url, httpHeaders, body, responseType);
    }

    /**
     * Make an HTTP request
     */
    protected <T,U> ListenableFuture<T> request(HttpMethod method, String url, HttpHeaders httpHeaders, U body, Class<T> responseType) {
        if (httpHeaders == null) {
            httpHeaders = getRequestHeaders(url);
        }
        HttpEntity<U> requestEntity = new HttpEntity<>(body, httpHeaders);

        ListenableFuture<ResponseEntity<T>> future = asyncRestTemplate.exchange(url, method, requestEntity, responseType);

        return new ListenableFutureAdapter<T, ResponseEntity<T>>(future) {
            @Override
            protected T adapt(ResponseEntity<T> result) throws ExecutionException {
                return result.getBody();
            }
        };
    }
}

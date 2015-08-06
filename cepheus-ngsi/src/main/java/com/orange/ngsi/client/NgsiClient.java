/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

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

    /**
     * Let some time for the client to shutdown gracefully
     * @throws IOException
     */
    public void shutdownGracefully() throws IOException {
        poolingNHttpClientConnectionManager.shutdown(10000);
    }

    /**
     * Send an update request
     * @param brokerUrl the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param updateContext the UpdateContext
     * @return a future for an UpdateContextReponse
     */
    public ListenableFuture<UpdateContextResponse> updateContext(String brokerUrl, HttpHeaders httpHeaders, UpdateContext updateContext) {
        return request(brokerUrl, httpHeaders, updateContext, UpdateContextResponse.class);
    }

    /**
     * Send a subscription request
     * @param providerUrl the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscribeContext the SubscriptionContext
     * @return a future for a SubscribeContextResponse
     */
    public ListenableFuture<SubscribeContextResponse> subscribeContext(String providerUrl, HttpHeaders httpHeaders, SubscribeContext subscribeContext) {
        return request(providerUrl, httpHeaders, subscribeContext, SubscribeContextResponse.class);
    }

    /**
     * Send an unsubscribe request
     * @param providerUrl the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscriptionId the subscription ID to unsubscribe
     * @return a future for a UnsubscribeContextResponse
     */
    public ListenableFuture<UnsubscribeContextResponse> unsubscribeContext(String providerUrl, HttpHeaders httpHeaders, String subscriptionId) {
        return request(providerUrl, httpHeaders, new UnsubscribeContext(subscriptionId), UnsubscribeContextResponse.class);
    }

    /**
     * The default HTTP request headers used for the requests.
     * @return the HTTP request headers.
     */
    public HttpHeaders getRequestHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return requestHeaders;
    }

    private <T,U> ListenableFuture<T> request(String url, HttpHeaders httpHeaders, U body, Class<T> responseType) {
        if (httpHeaders == null) {
            httpHeaders = getRequestHeaders();
        }
        HttpEntity<U> requestEntity = new HttpEntity<>(body, httpHeaders);

        ListenableFuture<ResponseEntity<T>> future = asyncRestTemplate.exchange(url, HttpMethod.POST, requestEntity, responseType);

        return new ListenableFutureAdapter<T, ResponseEntity<T>>(future) {
            @Override
            protected T adapt(ResponseEntity<T> result) throws ExecutionException {
                return result.getBody();
            }
        };
    }
}

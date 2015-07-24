/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.espr4fastdata.exception.SubscribeContextRequestException;
import com.orange.espr4fastdata.model.cep.Provider;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.SubscribeContextResponse;
import com.orange.ngsi.model.SubscribeError;
import com.orange.ngsi.model.SubscribeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashSet;


/**
 * Send subscribeContext requests to the Context Broker
 */

@Service
@Configuration
public class SubscribeContextRequest {

    private static Logger logger = LoggerFactory.getLogger(SubscribeContextRequest.class);

    public interface SubscribeContextResponseListener {
        void onError(SubscribeError subscribeError);
        void onSuccess(SubscribeResponse subscribeResponse);
    }

    @Autowired
    public AsyncRestTemplate asyncRestTemplate;

    public void postSubscribeContextRequest(SubscribeContext subscribeContext, String providerUrl, SubscribeContextResponseListener listener) throws URISyntaxException, SubscribeContextRequestException {

        // Check listener
        if (listener == null) {
            throw new SubscribeContextRequestException("SubscribeContextResponseListener is null");
        }

        // Set the Content-Type header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SubscribeContext> requestEntity = new HttpEntity<>(subscribeContext, requestHeaders);

        URI providerURI = new URI(providerUrl);

        ListenableFuture<ResponseEntity<SubscribeContextResponse>> futureEntity;
        futureEntity = asyncRestTemplate.exchange(providerURI, HttpMethod.POST, requestEntity, SubscribeContextResponse.class);

        futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<SubscribeContextResponse>>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                logger.debug("Response received (async callable)");
                logger.debug("Status Code of SubscribeContextResponse : {} SubscribeContextResponse received : {}", result.getStatusCode(), result.getBody());

                SubscribeContextResponse subscribeContextResponse = (SubscribeContextResponse)result.getBody();
                SubscribeError subscribeError = subscribeContextResponse.getSubscribeError();

                if (subscribeError == null) {
                    listener.onSuccess(subscribeContextResponse.getSubscribeResponse());
                }
                else {
                    listener.onSuccess(subscribeContextResponse.getSubscribeResponse());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("Failed Response received: {} ", t.getCause()
                        + "|" + t.getMessage());
            }
        });

    }

}

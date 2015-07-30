/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.espr4fastdata.model.Broker;
import com.orange.ngsi.model.UpdateContext;
import com.orange.ngsi.model.UpdateContextResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Created by pborscia on 08/06/2015.
 */
@Service
public class UpdateContextRequest {

    private static Logger logger = LoggerFactory.getLogger(UpdateContextRequest.class);

    @Autowired
    public AsyncRestTemplate asyncRestTemplate;

    public void postUpdateContextRequest(UpdateContext updateContext, Broker broker) {

        // Set the Content-Type header, ServiceName and ServicePath
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("Fiware-Service", broker.getServiceName());
        requestHeaders.add("Fiware-ServicePath", broker.getServicePath());

        HttpEntity<UpdateContext> requestEntity = new HttpEntity<>(updateContext, requestHeaders);

        ListenableFuture<ResponseEntity<UpdateContextResponse>> futureEntity;

        futureEntity = asyncRestTemplate.exchange(broker.getUrl(), HttpMethod.POST, requestEntity, UpdateContextResponse.class);

        futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                logger.debug("Response received (async callable)");
                logger.debug("Status Code of UpdateContextResponse : {} UpdateContextResponse received : {}", result.getStatusCode(), result.getBody());

            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("Failed Response received: {} ", t.getCause()
                        + "|" + t.getMessage());

            }
        });


    }


}

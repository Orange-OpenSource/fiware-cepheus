/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.exception.SenderException;
import com.orange.espr4fastdata.model.cep.Broker;
import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import com.orange.espr4fastdata.model.ngsi.UpdateContextResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpStatusCodeException;

/**
 * Created by pborscia on 08/06/2015.
 */
@Service
@Configuration
public class Sender {

    private static Logger logger = LoggerFactory.getLogger(Sender.class);


    @Value("${sender.defaultMaxTotalConnections}")
    private int defaultMaxTotalConnections;

    @Value("${sender.defaultMaxConnectionsPerRoute}")
    private int defaultMaxConnectionsPerRoute;

    @Value("${sender.defaultReadTimeoutMilliseconds}")
    private int defaultReadTimeoutMilliseconds;

    PoolingNHttpClientConnectionManager connectionManager;

    public UpdateContextResponse postMessage(UpdateContext updateContext, Broker broker) {


        UpdateContextResponse updateContextResponse = null;
        try {

            // Set the Content-Type header, ServiceName and ServicePath
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("Fiware-Service", broker.getServiceName());
            requestHeaders.add("Fiware-ServicePath", broker.getServicePath());

            HttpEntity<UpdateContext> requestEntity = new HttpEntity<>(updateContext, requestHeaders);

            logger.info("POOL STATS {} ", connectionManager.getTotalStats().toString());

            ListenableFuture<ResponseEntity<UpdateContextResponse>> futureEntity = null;
            try {
                futureEntity = asyncRestTemplate().exchange(broker.getUrl(), HttpMethod.POST, requestEntity, UpdateContextResponse.class);
            } catch (SenderException e) {
                logger.error("Unable to send http request {} cause {}", e.getMessage(), e.getCause());
            }

            futureEntity
                    .addCallback(new ListenableFutureCallback<ResponseEntity>() {
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



        } catch (HttpStatusCodeException e) {
            logger.warn("POST FAILED with HttpStatusCode: {} ", e.getStatusCode()
                    + "|" + e.getStatusText());
        } catch (RuntimeException e) {
            logger.error("POST FAILED {}",e);

        }
        return updateContextResponse;
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() throws SenderException {
        AsyncRestTemplate restTemplate = new AsyncRestTemplate(
                clientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        //TODO replace by authent interceptor
        //restTemplate.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new ServiceNamePathInterceptor()));

        return restTemplate;
    }

    @Bean
    public AsyncClientHttpRequestFactory clientHttpRequestFactory() throws SenderException {
        HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory(asyncHttpClient());

        return factory;
    }

    @Bean
    public CloseableHttpAsyncClient asyncHttpClient() throws SenderException {
        try {

            logger.info("sender.defaultMaxTotalConnections : {}", defaultMaxTotalConnections);
            logger.info("sender.defaultMaxConnectionsPerRoute : {}", defaultMaxConnectionsPerRoute);
            logger.info("sender.defaultReadTimeoutMilliseconds : {}", defaultReadTimeoutMilliseconds);

            connectionManager = new PoolingNHttpClientConnectionManager(
                    new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
            connectionManager.setMaxTotal(defaultMaxTotalConnections);
            connectionManager
                    .setDefaultMaxPerRoute(defaultMaxConnectionsPerRoute);


            RequestConfig config = RequestConfig.custom()
                    .setConnectTimeout(defaultReadTimeoutMilliseconds)
                    .setSocketTimeout(defaultReadTimeoutMilliseconds)
                    .setConnectionRequestTimeout(defaultReadTimeoutMilliseconds)
                    .build();

            CloseableHttpAsyncClient httpclient = HttpAsyncClientBuilder
                    .create().setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(config).build();
            return httpclient;
        } catch (Exception e) {
            throw new SenderException(e.getMessage(),e.getCause());

        }
    }
}

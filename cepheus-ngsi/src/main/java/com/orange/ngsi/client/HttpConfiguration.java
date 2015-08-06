/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.IOReactorException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;

/**
 * Configure HTTP connections
 */
@Configuration
public class HttpConfiguration {

    @Value("${ngsi.http.maxTotalConnections:20}")
    private int maxTotalConnections;

    @Value("${ngsi.http.maxConnectionsPerRoute:2}")
    private int maxConnectionsPerRoute;

    @Value("${ngsi.http.requestTimeout:2000}")
    private int requestTimeout;

    @Bean
    public AsyncRestTemplate asyncRestTemplate(AsyncClientHttpRequestFactory asyncClientHttpRequestFactory) {
        AsyncRestTemplate restTemplate = new AsyncRestTemplate(asyncClientHttpRequestFactory);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public AsyncClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpAsyncClient closeableHttpAsyncClient) {
        return new HttpComponentsAsyncClientHttpRequestFactory(closeableHttpAsyncClient);
    }

    @Bean
    public CloseableHttpAsyncClient asyncHttpClient(PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager) {

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(requestTimeout)
                .setSocketTimeout(requestTimeout)
                .setConnectionRequestTimeout(requestTimeout)
                .build();

        return HttpAsyncClientBuilder
                .create().setConnectionManager(poolingNHttpClientConnectionManager)
                .setDefaultRequestConfig(config).build();
    }

    @Bean
    PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager() throws IOReactorException {
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
                new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT));
        connectionManager.setMaxTotal(maxTotalConnections);
        connectionManager.setDefaultMaxPerRoute(maxConnectionsPerRoute);
        return connectionManager;
    }
}

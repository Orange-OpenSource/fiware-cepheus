/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.espr4fastdata.Application;
import com.orange.ngsi.model.UpdateAction;
import com.orange.ngsi.model.UpdateContextResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.AsyncRestTemplate;

import java.io.IOException;
import java.util.function.Consumer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.mockito.Mockito.*;
import static com.orange.espr4fastdata.util.Util.*;

import javax.inject.Inject;

/**
 * Test for the NGSI UpdateContext request
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UpdateContextRequestTest {

    private final String brokerUrl = "http://localhost/updateContext";
    private final String serviceName = "myTenant";
    private final String servicePath = "/root/test";

    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    NgsiClient ngsiClient;

    private Consumer<UpdateContextResponse> onSuccess = Mockito.mock(Consumer.class);

    private Consumer<Throwable> onFailure = Mockito.mock(Consumer.class);

    @Before
    public void setup() {
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);
    }

    @After
    public void tearDown() {
        reset(onSuccess);
        reset(onFailure);
    }

    @Test
    public void performPostWith200() throws Exception {
        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders();
        httpHeaders.add("Fiware-Service", serviceName);
        httpHeaders.add("Fiware-ServicePath", servicePath);

        String responseBody = json(mapping, createUpdateContextResponseTempSensor());

        this.mockServer.expect(requestTo(brokerUrl)).andExpect(method(HttpMethod.POST))
                .andExpect(header("Fiware-Service", serviceName))
                .andExpect(header("Fiware-ServicePath", servicePath))
                .andExpect(jsonPath("$.updateAction").value(UpdateAction.UPDATE.getLabel()))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ngsiClient.updateContext(brokerUrl, httpHeaders, createUpdateContextTempSensor(0), onSuccess, onFailure);

        this.mockServer.verify();

        verify(onFailure, never()).accept(anyObject());
        verify(onSuccess).accept(any(UpdateContextResponse.class));
    }

    @Test
    public void performPostWith404() throws Exception {
        Consumer<UpdateContextResponse> onSuccess = Mockito.mock(Consumer.class);
        Consumer<Throwable> onFailure = Mockito.mock(Consumer.class);

        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ngsiClient.updateContext(brokerUrl, null, createUpdateContextTempSensor(0), onSuccess, onFailure);

        this.mockServer.verify();

        verify(onFailure).accept(any(Throwable.class));
        verify(onSuccess, never()).accept(any(UpdateContextResponse.class));
    }

    @Test
    public void performPostWith500() throws Exception {
        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ngsiClient.updateContext(brokerUrl, null, createUpdateContextTempSensor(0), onSuccess, onFailure);

        this.mockServer.verify();

        verify(onFailure).accept(any(Throwable.class));
        verify(onSuccess, never()).accept(any(UpdateContextResponse.class));

    }

    @Test
    public void performPostWithTimeout() throws Exception {
        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(TimeoutResponseCreator.withTimeout());

        ngsiClient.updateContext(brokerUrl, null, createUpdateContextTempSensor(0), onSuccess, onFailure);

        this.mockServer.verify();

        verify(onFailure).accept(any(Throwable.class));
        verify(onSuccess, never()).accept(any(UpdateContextResponse.class));
    }

    public static class TimeoutResponseCreator implements ResponseCreator {

        @Override
        public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static TimeoutResponseCreator withTimeout() {
            return new TimeoutResponseCreator();
        }
    }

}

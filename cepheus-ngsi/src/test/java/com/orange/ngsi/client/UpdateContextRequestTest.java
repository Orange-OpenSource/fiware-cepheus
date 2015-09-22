/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.orange.ngsi.ProtocolRegistry;
import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.model.UpdateAction;
import com.orange.ngsi.model.UpdateContextResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.function.Consumer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static org.mockito.Mockito.*;
import static com.orange.ngsi.Util.*;

import javax.inject.Inject;

/**
 * Test for the NGSI UpdateContext request
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class UpdateContextRequestTest {

    private final String brokerUrl = "http://localhost:8080";
    private final String serviceName = "myTenant";
    private final String servicePath = "/root/test";

    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonConverter;

    @Inject
    public AsyncRestTemplate asyncRestTemplate;

    @Autowired
    public ApplicationContext applicationContext;

    @Mock
    public ProtocolRegistry protocolRegistry;

    @Autowired
    @InjectMocks
    public NgsiClient ngsiClient;

    private XmlMapper xmlmapper = new XmlMapper();

    public Consumer<UpdateContextResponse> onSuccess = Mockito.mock(Consumer.class);

    public Consumer<Throwable> onFailure = Mockito.mock(Consumer.class);

    @Before
    public void setup() throws URISyntaxException {
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        reset(onSuccess);
        reset(onFailure);
        reset(protocolRegistry);
    }

    @Test
    public void performPostXmlWith200() throws Exception {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(true);

        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders(brokerUrl);
        httpHeaders.add("Fiware-Service", serviceName);
        httpHeaders.add("Fiware-ServicePath", servicePath);

        String responseBody = xmlmapper.writeValueAsString(createUpdateContextResponseTempSensor());

        this.mockServer.expect(requestTo(brokerUrl + "/ngsi10/updateContext"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Fiware-Service", serviceName))
                .andExpect(header("Fiware-ServicePath", servicePath))
                .andExpect(xpath("updateContextRequest/updateAction").string(UpdateAction.UPDATE.getLabel()))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_XML));

        ngsiClient.updateContext(brokerUrl, httpHeaders, createUpdateContextTempSensor(0)).get();

        this.mockServer.verify();
    }

    @Test
    public void performPostWith200() throws Exception {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(false);

        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders(brokerUrl);
        httpHeaders.add("Fiware-Service", serviceName);
        httpHeaders.add("Fiware-ServicePath", servicePath);

        String responseBody = json(jsonConverter, createUpdateContextResponseTempSensor());

        this.mockServer.expect(requestTo(brokerUrl + "/ngsi10/updateContext")).andExpect(method(HttpMethod.POST))
                .andExpect(header("Fiware-Service", serviceName))
                .andExpect(header("Fiware-ServicePath", servicePath))
                .andExpect(jsonPath("$.updateAction").value(UpdateAction.UPDATE.getLabel()))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ngsiClient.updateContext(brokerUrl, httpHeaders, createUpdateContextTempSensor(0)).get();

        this.mockServer.verify();
    }

    @Test(expected = HttpClientErrorException.class)
    public void performPostWith404() throws Exception {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(false);

        this.mockServer.expect(requestTo(brokerUrl + "/ngsi10/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ngsiClient.updateContext(brokerUrl, null, createUpdateContextTempSensor(0)).get();
    }

    @Test(expected = HttpServerErrorException.class)
    public void dperformPostWith500() throws Exception {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(false);

        this.mockServer.expect(requestTo(brokerUrl + "/ngsi10/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ngsiClient.updateContext(brokerUrl, null, createUpdateContextTempSensor(0)).get();
    }

    @Test(expected = ResourceAccessException.class)
    public void eperformPostWithTimeout() throws Exception {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(false);

        this.mockServer.expect(requestTo(brokerUrl + "/ngsi10/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(TimeoutResponseCreator.withTimeout());

        ngsiClient.updateContext(brokerUrl, null, createUpdateContextTempSensor(0));
    }

    public static class TimeoutResponseCreator implements ResponseCreator {

        @Override
        public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
            throw new SocketTimeoutException("Testing timeout exception");
        }

        public static TimeoutResponseCreator withTimeout() {
            return new TimeoutResponseCreator();
        }
    }

}

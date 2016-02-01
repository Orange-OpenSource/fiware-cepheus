/*
 * Copyright (C) 2016 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.Util;
import com.orange.ngsi.model.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.function.Consumer;

import static com.orange.ngsi.Util.json;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import static org.junit.Assert.*;
import static com.orange.ngsi.Util.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Tests for the NgsiRestClient class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class NgsiRestClientTest {

    private static String baseUrl = "http://localhost:8080";


    @Autowired
    @InjectMocks
    public NgsiRestClient ngsiRestClient;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter xmlConverter;

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    private MockRestServiceServer mockServer;

    private Consumer<NotifyContextResponse> onSuccess = Mockito.mock(Consumer.class);

    private Consumer<Throwable> onFailure = Mockito.mock(Consumer.class);

    @Before
    public void tearUp() {
        MockitoAnnotations.initMocks(this);
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);
        ngsiRestClient.protocolRegistry.registerHost(baseUrl, true); // support JSON
    }

    @After
    public void tearDown() {
        reset(onSuccess);
        reset(onFailure);
    }

    @Test
    public void testAppendContextElement_JSON() throws Exception {
        String responseBody = json(jsonConverter, createAppendContextElementResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.attributes").isArray())
                .andExpect(jsonPath("$.attributes[0].name").value("temp"))
                .andExpect(jsonPath("$.attributes[0].type").value("float"))
                .andExpect(jsonPath("$.attributes[0].value").value("15.5"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        AppendContextElementResponse response = ngsiRestClient.appendContextElement(baseUrl, null, "123", Util.createAppendContextElementTemperature()).get();

        this.mockServer.verify();

        assertNull(response.getErrorCode());
        assertNotNull(response.getContextAttributeResponses());
        assertEquals(1, response.getContextAttributeResponses().size());
        assertNotNull(response.getContextAttributeResponses().get(0).getContextAttributeList());
        assertEquals(1, response.getContextAttributeResponses().get(0).getContextAttributeList().size());
        assertNotNull(response.getContextAttributeResponses().get(0).getContextAttributeList().get(0));
        assertEquals("temp", response.getContextAttributeResponses().get(0).getContextAttributeList().get(0).getName());
        assertEquals("float", response.getContextAttributeResponses().get(0).getContextAttributeList().get(0).getType());
        assertEquals("15.5", response.getContextAttributeResponses().get(0).getContextAttributeList().get(0).getValue());
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getContextAttributeResponses().get(0).getStatusCode().getCode());
    }

    @Test
    public void testUpdateContextElement_JSON() throws Exception {
        String responseBody = json(jsonConverter, createUpdateContextElementResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.attributes").isArray())
                .andExpect(jsonPath("$.attributes[0].name").value("temp"))
                .andExpect(jsonPath("$.attributes[0].type").value("float"))
                .andExpect(jsonPath("$.attributes[0].value").value("15.5"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        UpdateContextElementResponse response = ngsiRestClient.updateContextElement(baseUrl, null, "123", Util.createUpdateContextElementTemperature()).get();

        this.mockServer.verify();

        assertNull(response.getErrorCode());
        assertNotNull(response.getContextAttributeResponses());
        assertEquals(1, response.getContextAttributeResponses().size());
        assertNotNull(response.getContextAttributeResponses().get(0).getContextAttributeList());
        assertEquals(1, response.getContextAttributeResponses().get(0).getContextAttributeList().size());
        assertNotNull(response.getContextAttributeResponses().get(0).getContextAttributeList().get(0));
        assertEquals("temp", response.getContextAttributeResponses().get(0).getContextAttributeList().get(0).getName());
        assertEquals("float", response.getContextAttributeResponses().get(0).getContextAttributeList().get(0).getType());
        assertEquals("15.5", response.getContextAttributeResponses().get(0).getContextAttributeList().get(0).getValue());
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getContextAttributeResponses().get(0).getStatusCode().getCode());
    }

    @Test
    public void testGetContextElement_JSON() throws Exception {
        String responseBody = json(jsonConverter, createContextElementResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ContextElementResponse response = ngsiRestClient.getContextElement(baseUrl, null, "123").get();

        this.mockServer.verify();

        assertNotNull(response.getContextElement());
        assertNotNull(response.getContextElement().getContextAttributeList());
        assertEquals(1, response.getContextElement().getContextAttributeList().size());
        assertNotNull(response.getContextElement().getContextAttributeList().get(0));
        assertEquals("temp", response.getContextElement().getContextAttributeList().get(0).getName());
        assertEquals("float", response.getContextElement().getContextAttributeList().get(0).getType());
        assertEquals("15.5", response.getContextElement().getContextAttributeList().get(0).getValue());
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getStatusCode().getCode());
    }

    @Test
    public void testDeleteContextElement_JSON() throws Exception {
        String responseBody = json(jsonConverter, new StatusCode(CodeEnum.CODE_200));

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        StatusCode response = ngsiRestClient.deleteContextElement(baseUrl, null, "123").get();

        this.mockServer.verify();

        assertEquals(CodeEnum.CODE_200.getLabel(), response.getCode());
    }

    @Test
    public void testAppendContextAttribute_JSON() throws Exception {
        String responseBody = json(jsonConverter, new StatusCode(CodeEnum.CODE_200));

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123/attributes/temp"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.name").value("temp"))
                .andExpect(jsonPath("$.type").value("float"))
                .andExpect(jsonPath("$.value").value("15.5"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        StatusCode response = ngsiRestClient.appendContextAttribute(baseUrl, null, "123", "temp", Util.createUpdateContextAttributeTemperature()).get();

        this.mockServer.verify();

        assertEquals(CodeEnum.CODE_200.getLabel(), response.getCode());
    }

    @Test
    public void testUpdateContextAttribute_JSON() throws Exception {
        String responseBody = json(jsonConverter, new StatusCode(CodeEnum.CODE_200));

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123/attributes/temp"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.name").value("temp"))
                .andExpect(jsonPath("$.type").value("float"))
                .andExpect(jsonPath("$.value").value("15.5"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        StatusCode response = ngsiRestClient.updateContextAttribute(baseUrl, null, "123", "temp", Util.createUpdateContextAttributeTemperature()).get();

        this.mockServer.verify();

        assertEquals(CodeEnum.CODE_200.getLabel(), response.getCode());
    }

    @Test
    public void testGetContextAttribute_JSON() throws Exception {
        String responseBody = json(jsonConverter, createContextAttributeResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123/attributes/temp"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ContextAttributeResponse response = ngsiRestClient.getContextAttribute(baseUrl, null, "123", "temp").get();

        this.mockServer.verify();

        assertNotNull(response);
        assertNotNull(response.getContextAttributeList());
        assertEquals(1, response.getContextAttributeList().size());
        assertNotNull(response.getContextAttributeList().get(0));
        assertEquals("temp", response.getContextAttributeList().get(0).getName());
        assertEquals("float", response.getContextAttributeList().get(0).getType());
        assertEquals("15.5", response.getContextAttributeList().get(0).getValue());
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getStatusCode().getCode());
    }

    @Test
    public void testDeleteContextAttribute_JSON() throws Exception {
        String responseBody = json(jsonConverter, new StatusCode(CodeEnum.CODE_200));

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123/attributes/temp"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        StatusCode response = ngsiRestClient.deleteContextAttribute(baseUrl, null, "123", "temp").get();

        this.mockServer.verify();

        assertEquals(CodeEnum.CODE_200.getLabel(), response.getCode());
    }

    @Test
    public void testUpdateContextAttributeValue_JSON() throws Exception {
        String responseBody = json(jsonConverter, new StatusCode(CodeEnum.CODE_200));

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123/attributes/temp/1"))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(jsonPath("$.name").value("temp"))
                .andExpect(jsonPath("$.type").value("float"))
                .andExpect(jsonPath("$.value").value("15.5"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        StatusCode response = ngsiRestClient.updateContextAttributeValue(baseUrl, null, "123", "temp", "1", Util.createUpdateContextAttributeTemperature()).get();

        this.mockServer.verify();

        assertEquals(CodeEnum.CODE_200.getLabel(), response.getCode());
    }

    @Test
    public void testGetContextAttributeValue_JSON() throws Exception {
        String responseBody = json(jsonConverter, createContextAttributeResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123/attributes/temp/1"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        ContextAttributeResponse response = ngsiRestClient.getContextAttributeValue(baseUrl, null, "123", "temp", "1").get();

        this.mockServer.verify();

        assertNotNull(response);
        assertNotNull(response.getContextAttributeList());
        assertEquals(1, response.getContextAttributeList().size());
        assertNotNull(response.getContextAttributeList().get(0));
        assertEquals("temp", response.getContextAttributeList().get(0).getName());
        assertEquals("float", response.getContextAttributeList().get(0).getType());
        assertEquals("15.5", response.getContextAttributeList().get(0).getValue());
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getStatusCode().getCode());
    }

    @Test
    public void testDeleteContextAttributeValue_JSON() throws Exception {
        String responseBody = json(jsonConverter, new StatusCode(CodeEnum.CODE_200));

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntities/123/attributes/temp/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        StatusCode response = ngsiRestClient.deleteContextAttributeValue(baseUrl, null, "123", "temp", "1").get();

        this.mockServer.verify();

        assertEquals(CodeEnum.CODE_200.getLabel(), response.getCode());
    }

    @Test
    public void testGetContextEntityType_JSON() throws Exception {
        String responseBody = json(jsonConverter, createQueryContextResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntityTypes/TempSensor"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        QueryContextResponse response = ngsiRestClient.getContextEntityType(baseUrl, null, "TempSensor").get();

        this.mockServer.verify();

        assertNotNull(response);
        assertNull(response.getErrorCode());
        assertNotNull(response.getContextElementResponses());
        assertEquals(1, response.getContextElementResponses().size());
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getContextElementResponses().get(0).getStatusCode().getCode());
        assertNotNull(response.getContextElementResponses().get(0));
        assertNotNull(response.getContextElementResponses().get(0).getContextElement());
        assertNotNull(response.getContextElementResponses().get(0).getContextElement().getEntityId());
        assertEquals("TempSensor", response.getContextElementResponses().get(0).getContextElement().getEntityId().getType());
        assertNotNull(response.getContextElementResponses().get(0).getContextElement().getContextAttributeList());
        assertEquals(1, response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().size());
        assertEquals("temp", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getName());
        assertEquals("float", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getType());
        assertEquals("15.5", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getValue());
    }

    @Test
    public void testGetContextEntityTypeAttribute_JSON() throws Exception {
        String responseBody = json(jsonConverter, createQueryContextResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextEntityTypes/TempSensor/attributes/temp"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        QueryContextResponse response = ngsiRestClient.getContextEntityTypeAttribute(baseUrl, null, "TempSensor", "temp").get();

        this.mockServer.verify();

        assertNotNull(response);
        assertNull(response.getErrorCode());
        assertNotNull(response.getContextElementResponses());
        assertEquals(1, response.getContextElementResponses().size());
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getContextElementResponses().get(0).getStatusCode().getCode());
        assertNotNull(response.getContextElementResponses().get(0));
        assertNotNull(response.getContextElementResponses().get(0).getContextElement());
        assertNotNull(response.getContextElementResponses().get(0).getContextElement().getEntityId());
        assertEquals("TempSensor", response.getContextElementResponses().get(0).getContextElement().getEntityId().getType());
        assertNotNull(response.getContextElementResponses().get(0).getContextElement().getContextAttributeList());
        assertEquals(1, response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().size());
        assertEquals("temp", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getName());
        assertEquals("float", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getType());
        assertEquals("15.5", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getValue());
    }

    @Test
    public void testAppendContextSubscription_JSON() throws Exception {
        String responseBody = json(jsonConverter, createSubscribeContextResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextSubscriptions/"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        SubscribeContextResponse response = ngsiRestClient.appendContextSubscription(baseUrl, null, createSubscribeContextTemperature()).get();

        this.mockServer.verify();

        assertNotNull(response);
        assertNull(response.getSubscribeError());
        assertNotNull(response.getSubscribeResponse());
        assertEquals("12345678", response.getSubscribeResponse().getSubscriptionId());
        assertEquals("P1M", response.getSubscribeResponse().getDuration());
    }

    @Test
    public void testUpdateContextSubscription_JSON() throws Exception {
        String responseBody = json(jsonConverter, createUpdateContextSubscriptionResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextSubscriptions/12345678"))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        UpdateContextSubscriptionResponse response = ngsiRestClient.updateContextSubscription(baseUrl, null, "12345678", createUpdateContextSubscriptionTemperature()).get();

        this.mockServer.verify();

        assertNotNull(response);
        assertNull(response.getSubscribeError());
        assertNotNull(response.getSubscribeResponse());
        assertEquals("12345678", response.getSubscribeResponse().getSubscriptionId());
        assertEquals("P1M", response.getSubscribeResponse().getDuration());
    }

    @Test
    public void testDeleteContextSubscription_JSON() throws Exception {
        String responseBody = json(jsonConverter, createUnsubscribeContextSubscriptionResponseTemperature());

        mockServer.expect(requestTo(baseUrl+"/ngsi10/contextSubscriptions/12345678"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        UnsubscribeContextResponse response = ngsiRestClient.deleteContextSubscription(baseUrl, null, "12345678").get();

        this.mockServer.verify();

        assertNotNull(response);
        assertEquals(CodeEnum.CODE_200.getLabel(), response.getStatusCode().getCode());
        assertEquals("12345678", response.getSubscriptionId());
    }
}

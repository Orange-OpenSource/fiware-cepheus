/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.controller;

import com.orange.cepheus.broker.Application;
import com.orange.cepheus.broker.Configuration;
import com.orange.cepheus.broker.LocalRegistrations;
import com.orange.cepheus.broker.Subscriptions;
import com.orange.cepheus.broker.exception.RegistrationException;
import com.orange.cepheus.broker.exception.RegistrationPersistenceException;
import com.orange.cepheus.broker.exception.SubscriptionException;
import com.orange.cepheus.broker.exception.SubscriptionPersistenceException;
import com.orange.cepheus.broker.model.Subscription;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static com.orange.cepheus.broker.Util.*;
import static com.orange.cepheus.broker.Util.createSubscribeContextTemperature;
import static com.orange.cepheus.broker.Util.createUpdateContextResponseTempSensorAndPressure;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests for the NGSI controller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class NgsiControllerTest {

    private MockMvc mockMvc;

    @Mock
    LocalRegistrations localRegistrations;

    @Mock
    Subscriptions subscriptions;

    @Mock
    NgsiClient ngsiClient;

    @Mock
    Configuration configuration;

    @Mock
    Iterator<URI> providingApplication;

    @Mock
    Iterator<Subscription> matchedSubscriptions;

    @Mock
    ListenableFuture<UpdateContextResponse> updateContextResponseListenableFuture;

    @Mock
    ListenableFuture<QueryContextResponse> queryContextResponseListenableFuture;

    @Mock
    ListenableFuture<NotifyContextResponse> notifyContextResponseListenableFuture;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MappingJackson2HttpMessageConverter mapper;

    @Captor
    private ArgumentCaptor<Set<String>> attributeArgumentCaptor;

    @Captor
    private ArgumentCaptor<EntityId> entityIdArgumentCaptor;

    @InjectMocks
    @Autowired
    private NgsiController ngsiController;

    private HttpHeaders httpHeaders = new HttpHeaders();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        when(configuration.getLocalUrl()).thenReturn("http://localhost:8081");
        when(configuration.getRemoteUrl()).thenReturn("http://orionhost:9999");
        when(configuration.getRemoteServiceName()).thenReturn(null);
        when(configuration.getRemoteServicePath()).thenReturn(null);
        when(configuration.getRemoteAuthToken()).thenReturn(null);
        when(updateContextResponseListenableFuture.get()).thenReturn(createUpdateContextResponseTempSensorAndPressure());
        doNothing().when(updateContextResponseListenableFuture).addCallback(any(), any());
        when(queryContextResponseListenableFuture.get()).thenReturn(createQueryContextResponseTemperature());
        doNothing().when(notifyContextResponseListenableFuture).addCallback(any(), any());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        when(ngsiClient.getRequestHeaders(any())).thenReturn(httpHeaders);
    }

    @After
    public void resetMocks() {
        reset(localRegistrations);
        reset(subscriptions);
        reset(ngsiClient);
        reset(configuration);
        reset(providingApplication);
        reset(matchedSubscriptions);
        reset(updateContextResponseListenableFuture);
        reset(notifyContextResponseListenableFuture);
    }

    @Test
    public void postRegisterContextWithEmptyContextRegistrations() throws Exception {

        mockMvc.perform(post("/v1/registerContext")
                .content(json(mapper, new RegisterContext()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail")
                        .value("The parameter contextRegistrations of type List<ContextRegistration> is missing in the request"));
    }

    @Test
    public void postRegisterContextWithBadContextRegistrations() throws Exception {
        // RegisterContext with a bad pattern
        RegisterContext registerContext = createRegistrationContext();
        registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).setId("]|,\\((");
        registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).setIsPattern(true);

        when(localRegistrations.updateRegistrationContext(any())).thenThrow(new RegistrationException("bad pattern", new RuntimeException()));

        mockMvc.perform(post("/v1/registerContext").content(json(mapper, registerContext)).contentType(MediaType.APPLICATION_JSON))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("registration error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("bad pattern"));
    }

    @Test
    public void postRegisterContextWithPersistenceException() throws Exception {

        when(localRegistrations.updateRegistrationContext(any())).thenThrow(new RegistrationPersistenceException("Failed to save", new RuntimeException()));

        mockMvc.perform(post("/v1/registerContext").content(json(mapper, createRegisterContextTemperature())).contentType(MediaType.APPLICATION_JSON))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("error in registration persistence"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("Failed to save"));
    }

    @Test
    public void postNewRegisterContext() throws Exception {

        when(localRegistrations.updateRegistrationContext(any())).thenReturn("12345678");
        ListenableFuture<RegisterContextResponse> responseFuture = Mockito.mock(ListenableFuture.class);
        doNothing().when(responseFuture).addCallback(any(), any());
        when(ngsiClient.registerContext(any(), eq(null), any())).thenReturn(responseFuture);

        mockMvc.perform(post("/v1/registerContext")
                .content(json(mapper, createRegisterContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.registrationId").value("12345678"));
    }

    @Test
    public void postUpdateContextWithNullUpdateAction() throws Exception {

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, new UpdateContext()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("The parameter updateAction of type UpdateAction is missing in the request"));
    }

    @Test
    public void postUpdateContextWithoutProvidingApplication() throws Exception {

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);
        //subscriptions mock return always without matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(false);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));


        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        // Capture updateContext when postUpdateContextRequest is called on updateContextRequest,
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);
        String urlProvider = "http://orionhost:9999";

        //check ListenableFuture is called at least Once and with addCallback method
        verify(updateContextResponseListenableFuture, atLeastOnce()).addCallback(any(), any());

        //check ngsiClient.getRequestHeaders() is called at least Once
        verify(ngsiClient, atLeastOnce()).getRequestHeaders(eq("http://orionhost:9999"));

        //check configuration.getHeadersForBroker() is called at least Once
        verify(configuration, atLeastOnce()).addRemoteHeaders(any());

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).updateContext(eq(urlProvider), any(), updateContextArg.capture());

        // Check id correspond to the required
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("S1", contextElement.getEntityId().getId());

        //check ngsiClient.notify is not called
        verify(ngsiClient, never()).notifyContext(any(), any(), any());
    }

    @Test
    public void postUpdateContextWithoutProvidingApplicationAndWithoutRemoteBrokerButWithNotify() throws Exception {

        when(configuration.getRemoteUrl()).thenReturn(null);

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always with matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(true, false);
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("999999", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        when(matchedSubscriptions.next()).thenReturn(subscription);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        //ngsiClient mock return always CODE_200
        when(ngsiClient.notifyContext(any(), any(), any())).thenReturn(notifyContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        //check ListenableFuture is called never and with addCallback method
        verify(updateContextResponseListenableFuture, never()).addCallback(any(), any());

        //verify ngsiClient.updateContext is never called
        verify(ngsiClient, never()).updateContext(any(), any(), any());

        //check ngsiClient.notify is called at least once
        // Capture notifyContext when postNotifyContextRequest is called on updateContextRequest,
        ArgumentCaptor<NotifyContext> notifyContextArg = ArgumentCaptor.forClass(NotifyContext.class);
        verify(notifyContextResponseListenableFuture, atLeastOnce()).addCallback(any(), any());
        String urlReference = subscribeContext.getReference().toString();
        verify(ngsiClient, atLeastOnce()).notifyContext(eq(urlReference), any(), notifyContextArg.capture());
        // Check id and status correspond to the required
        assertEquals(1, notifyContextArg.getValue().getContextElementResponseList().size());
        ContextElementResponse contextElementResponse = notifyContextArg.getValue().getContextElementResponseList().get(0);
        assertEquals("S1", contextElementResponse.getContextElement().getEntityId().getId());
        assertEquals("200", contextElementResponse.getStatusCode().getCode());
    }

    @Test
    public void postUpdateContextWithoutProvidingApplicationAndWithoutRemoteBrokerUrlButWithNotify() throws Exception {

        when(configuration.getRemoteUrl()).thenReturn(null);

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always with matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(true, false);
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("999999", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        when(matchedSubscriptions.next()).thenReturn(subscription);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        //ngsiClient mock return always CODE_200
        when(ngsiClient.notifyContext(any(), any(), any())).thenReturn(notifyContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        //check ListenableFuture is called never and with addCallback method
        verify(updateContextResponseListenableFuture, never()).addCallback(any(), any());

        //verify ngsiClient.updateContext is never called
        verify(ngsiClient, never()).updateContext(any(), any(), any());

        //check ngsiClient.notify is called at least once
        // Capture notifyContext when postNotifyContextRequest is called on updateContextRequest,
        ArgumentCaptor<NotifyContext> notifyContextArg = ArgumentCaptor.forClass(NotifyContext.class);
        verify(notifyContextResponseListenableFuture, atLeastOnce()).addCallback(any(), any());
        String urlReference = subscribeContext.getReference().toString();
        verify(ngsiClient, atLeastOnce()).notifyContext(eq(urlReference), any(), notifyContextArg.capture());
        // Check id and status correspond to the required
        assertEquals(1, notifyContextArg.getValue().getContextElementResponseList().size());
        ContextElementResponse contextElementResponse = notifyContextArg.getValue().getContextElementResponseList().get(0);
        assertEquals("S1", contextElementResponse.getContextElement().getEntityId().getId());
        assertEquals("200", contextElementResponse.getStatusCode().getCode());
    }

    @Test
    public void postUpdateContextWithoutProvidingApplicationAndWithEmptyRemoteBrokerButWithNotify() throws Exception {

        when(configuration.getRemoteUrl()).thenReturn("");

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always with matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(true, false);
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("999999", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        when(matchedSubscriptions.next()).thenReturn(subscription);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        //ngsiClient mock return always CODE_200
        when(ngsiClient.notifyContext(any(), any(), any())).thenReturn(notifyContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        //check ListenableFuture is called never and with addCallback method
        verify(updateContextResponseListenableFuture, never()).addCallback(any(), any());

        //verify ngsiClient.updateContext is never called
        verify(ngsiClient, never()).updateContext(any(), any(), any());

        //check ngsiClient.notify is called at least once
        // Capture notifyContext when postNotifyContextRequest is called on updateContextRequest,
        ArgumentCaptor<NotifyContext> notifyContextArg = ArgumentCaptor.forClass(NotifyContext.class);
        verify(notifyContextResponseListenableFuture, atLeastOnce()).addCallback(any(), any());
        String urlReference = subscribeContext.getReference().toString();
        verify(ngsiClient, atLeastOnce()).notifyContext(eq(urlReference), any(), notifyContextArg.capture());
        // Check id and status correspond to the required
        assertEquals(1, notifyContextArg.getValue().getContextElementResponseList().size());
        ContextElementResponse contextElementResponse = notifyContextArg.getValue().getContextElementResponseList().get(0);
        assertEquals("S1", contextElementResponse.getContextElement().getEntityId().getId());
        assertEquals("200", contextElementResponse.getStatusCode().getCode());
    }

    @Test
    public void postUpdateContextWithoutProvidingApplicationAndWithoutRemoteBrokerButWithNullOriginator() throws Exception {

        when(configuration.getRemoteUrl()).thenReturn(null);
        when(configuration.getLocalUrl()).thenReturn(null);

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always with matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(true, false);
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("999999", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        when(matchedSubscriptions.next()).thenReturn(subscription);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        //ngsiClient mock return always CODE_200
        when(ngsiClient.notifyContext(any(), any(), any())).thenReturn(notifyContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        //check ListenableFuture is called never and with addCallback method
        verify(updateContextResponseListenableFuture, never()).addCallback(any(), any());

        //verify ngsiClient.updateContext is never called
        verify(ngsiClient, never()).updateContext(any(), any(), any());

        //check ngsiClient.notify is never called
        verify(notifyContextResponseListenableFuture, never()).addCallback(any(), any());
        verify(ngsiClient, never()).notifyContext(any(), any(), any());
    }

    @Test
    public void postUpdateContextWithoutProvidingApplicationAndWithoutRemoteBrokerButWithEmptyOriginator() throws Exception {

        when(configuration.getRemoteUrl()).thenReturn(null);
        when(configuration.getLocalUrl()).thenReturn("");

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always with matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(true, false);
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("999999", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        when(matchedSubscriptions.next()).thenReturn(subscription);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        //ngsiClient mock return always CODE_200
        when(ngsiClient.notifyContext(any(), any(), any())).thenReturn(notifyContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        //check ListenableFuture is called never and with addCallback method
        verify(updateContextResponseListenableFuture, never()).addCallback(any(), any());

        //verify ngsiClient.updateContext is never called
        verify(ngsiClient, never()).updateContext(any(), any(), any());

        //check ngsiClient.notify is never called
        verify(notifyContextResponseListenableFuture, never()).addCallback(any(), any());
        verify(ngsiClient, never()).notifyContext(any(), any(), any());
    }

    @Test
    public void postUpdateContextWithProvidingApplication() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always without matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(false);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        // Capture updateContext when updateContextRequest is called on updateContextRequest,
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);
        String urlProvider = "http//iotagent:1234";

        //check ListenableFuture is called at least Once and with get method
        verify(updateContextResponseListenableFuture, atLeastOnce()).get();

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).updateContext(eq(urlProvider), any(), updateContextArg.capture());

        // Check id correspond to the required
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("S1", contextElement.getEntityId().getId());

        //check ngsiClient.notify is not called
        verify(ngsiClient, never()).notifyContext(any(), any(), any());
    }

    @Test
    public void postUpdateContextWithProvidingApplicationAndMatchedSusbcriptions() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always without matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(true);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value("1015"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S1", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertFalse(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(2, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));
        assertTrue(attributeArgumentCaptor.getValue().contains("pressure"));

        // Capture updateContext when updateContextRequest is called on updateContextRequest,
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);
        String urlProvider = "http//iotagent:1234";

        //check ListenableFuture is called at least Once and with get method
        verify(updateContextResponseListenableFuture, atLeastOnce()).get();

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).updateContext(eq(urlProvider), any(), updateContextArg.capture());

        // Check id correspond to the required
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("S1", contextElement.getEntityId().getId());

        //check ngsiClient.notify is not called
        verify(ngsiClient, never()).notifyContext(any(), any(), any());
    }

    @Test
    public void postUpdateContextWithThrowExecutionException() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always without matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(false);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        when(updateContextResponseListenableFuture.get()).thenThrow(new ExecutionException("execution exception", new Throwable()));

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        //check ngsiClient.notify is not called
        verify(ngsiClient, never()).notifyContext(any(), any(), any());

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("Receiver internal error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("An unknown error at the receiver has occured"));
    }

    @Test
    public void postUpdateContextWithThrowInterruptedException() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //subscriptions mock return always without matched subscriptions
        when(matchedSubscriptions.hasNext()).thenReturn(false);
        when(subscriptions.findSubscriptions(any(), any())).thenReturn(matchedSubscriptions);

        when(updateContextResponseListenableFuture.get()).thenThrow(new InterruptedException());

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        //check ngsiClient.notify is not called
        verify(ngsiClient, never()).notifyContext(any(), any(), any());

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("Receiver internal error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("An unknown error at the receiver has occured"));
    }

    @Test
    public void postQueryContextWithProvidingApplication() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //ngsiclient mock return always createQueryContextResponseTemperature when call queryContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S*", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertTrue(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(1, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));

        // Capture queryContext when queryContextRequest is called on updateContextRequest,
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);
        String urlProvider = "http//iotagent:1234";

        //check ListenableFuture is called at least Once and with get method
        verify(queryContextResponseListenableFuture, atLeastOnce()).get();

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).queryContext(eq(urlProvider), any(), queryContextArg.capture());

        // Check id correspond to the required
        assertEquals(1, queryContextArg.getValue().getEntityIdList().size());
        assertEquals("S*", queryContextArg.getValue().getEntityIdList().get(0).getId());
    }

    @Test
    public void postQueryContextWithProvidingApplicationWithNullAttributes() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //ngsiclient mock return always createQueryContextResponseTemperature when call queryContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        QueryContext queryContext = createQueryContextTemperature();
        queryContext.setAttributeList(null);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, queryContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S*", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertTrue(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(0, attributeArgumentCaptor.getValue().size());

        // Capture queryContext when queryContextRequest is called on queryContextRequest
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);
        String urlProvider = "http//iotagent:1234";

        //check ListenableFuture is called at least Once and with get method
        verify(queryContextResponseListenableFuture, atLeastOnce()).get();

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).queryContext(eq(urlProvider), any(), queryContextArg.capture());

        // Check id correspond to the required
        assertEquals(1, queryContextArg.getValue().getEntityIdList().size());
        assertEquals("S*", queryContextArg.getValue().getEntityIdList().get(0).getId());

    }

    @Test
    public void postQueryContextWithoutProvidingApplication() throws Exception {

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //ngsiclient mock return always createQueryContextResponseTemperature when call queryContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value("200"));

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on localRegistrations Set<String> searchAttributes
        verify(localRegistrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

        //check entityId
        assertEquals("S*", entityIdArgumentCaptor.getValue().getId());
        assertEquals("TempSensor", entityIdArgumentCaptor.getValue().getType());
        assertTrue(entityIdArgumentCaptor.getValue().getIsPattern());

        //check attributes
        assertEquals(1, attributeArgumentCaptor.getValue().size());
        assertTrue(attributeArgumentCaptor.getValue().contains("temp"));

        // Capture queryContext when queryContextRequest is called on updateContextRequest,
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);
        String urlProvider = "http://orionhost:9999";

        // Capture HttpHeaders queryContextRequest is called on queryContextRequest
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);

        //check ListenableFuture is called at least Once and with get method
        verify(queryContextResponseListenableFuture, atLeastOnce()).get();

        //check ngsiClient.getRequestHeaders() is called at least Once
        verify(ngsiClient, atLeastOnce()).getRequestHeaders(eq("http://orionhost:9999"));

        //check configuration.getHeadersForBroker() is called at least Once
        verify(configuration, atLeastOnce()).addRemoteHeaders(any());

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).queryContext(eq(urlProvider), httpHeadersArg.capture(), queryContextArg.capture());

        // Check id correspond to the required
        assertEquals(1, queryContextArg.getValue().getEntityIdList().size());
        assertEquals("S*", queryContextArg.getValue().getEntityIdList().get(0).getId());
    }

    @Test
    public void postQueryContextWithNullRemoteBroker() throws Exception {

        //configuration mock return null as remoteBroker
        when(configuration.getRemoteUrl()).thenReturn(null);

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("missing remote broker error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("No remote.url parameter defined to forward queryContext"));
    }

    @Test
    public void postQueryContextWithNullRemoteBrokerUrl() throws Exception {

        //configuration mock return null as remoteBroker
        when(configuration.getRemoteUrl()).thenReturn(null);

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("missing remote broker error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("No remote.url parameter defined to forward queryContext"));
    }

    @Test
    public void postQueryContextWithEmptyRemoteBrokerUrl() throws Exception {

        //configuration mock return "" as remoteBrokerUrl
        when(configuration.getRemoteUrl()).thenReturn("");

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("missing remote broker error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("No remote.url parameter defined to forward queryContext"));
    }

    @Test
    public void postQueryContextWithExecutionException() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        when(queryContextResponseListenableFuture.get()).thenThrow(new ExecutionException("execution exception", new Throwable()));

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("Receiver internal error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("An unknown error at the receiver has occured"));
    }

    @Test
    public void postQueryContextWithInterruptedException() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        when(queryContextResponseListenableFuture.get()).thenThrow(new InterruptedException());

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.queryContext(any(), any(), any())).thenReturn(queryContextResponseListenableFuture);

        mockMvc.perform(post("/v1/queryContext")
                .content(json(mapper, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("Receiver internal error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("An unknown error at the receiver has occured"));
    }

    @Test
    public void postSubscribeContextWithEmptyEntities() throws Exception {

        mockMvc.perform(post("/v1/subscribeContext")
                .content(json(mapper, new SubscribeContext()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.detail")
                        .value("The parameter entities of type List<EntityId> is missing in the request"));
    }

    @Test
    public void postSubscribeContextWithBadDuration() throws Exception {
        // Bad duration
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setDuration("PIPO");

        when(subscriptions.addSubscription(any())).thenThrow(new SubscriptionException("bad duration", new RuntimeException()));

        mockMvc.perform(post("/v1/subscribeContext")
                .content(json(mapper, subscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.reasonPhrase").value("subscription error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.detail").value("bad duration"));
    }

    @Test
    public void postNewSubscribeContext() throws Exception {

        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("12345678", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        when(subscriptions.addSubscription(any())).thenReturn("12345678");
        when(subscriptions.getSubscription("12345678")).thenReturn(subscription);

        mockMvc.perform(post("/v1/subscribeContext")
                .content(json(mapper, subscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.subscriptionId").value("12345678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.duration").value("P1M"));

        verify(subscriptions, atLeastOnce()).addSubscription(any());
        verify(subscriptions, atLeastOnce()).getSubscription("12345678");
    }

    @Test
    public void postNewSubscribeContextWithNoDuration() throws Exception {

        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setDuration(null);

        SubscribeContext subscribeContextInSubscription = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("12345678", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContextInSubscription);
        when(subscriptions.addSubscription(any())).thenReturn("12345678");
        when(subscriptions.getSubscription("12345678")).thenReturn(subscription);

        mockMvc.perform(post("/v1/subscribeContext")
                .content(json(mapper, subscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.subscriptionId").value("12345678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.duration").value("P1M"));

        verify(subscriptions, atLeastOnce()).addSubscription(any());
        verify(subscriptions, atLeastOnce()).getSubscription("12345678");
    }

    @Test
    public void postSubscribeContextWithPersistenceFailure() throws Exception {

        SubscribeContext subscribeContext = createSubscribeContextTemperature();

        when(subscriptions.addSubscription(any())).thenThrow(new SubscriptionPersistenceException("table not exist", new RuntimeException()));

        mockMvc.perform(post("/v1/subscribeContext")
                .content(json(mapper, subscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.reasonPhrase").value("error in subscription persistence"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.detail").value("table not exist"));
    }

    @Test
    public void postUnSubscribeContextWhichExist() throws Exception {

        UnsubscribeContext unsubscribeContext = new UnsubscribeContext("12345678");
        when(subscriptions.deleteSubscription(any())).thenReturn(true);

        mockMvc.perform(post("/v1/unsubscribeContext")
                .content(json(mapper, unsubscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscriptionId").value("12345678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_200.getLabel()));

        verify(subscriptions, atLeastOnce()).deleteSubscription(any());
    }

    @Test
    public void postUnSubscribeContextWhichNotExist() throws Exception {

        UnsubscribeContext unsubscribeContext = new UnsubscribeContext("12345678");
        when(subscriptions.deleteSubscription(any())).thenReturn(false);

        mockMvc.perform(post("/v1/unsubscribeContext")
                .content(json(mapper, unsubscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscriptionId").value("12345678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_470.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.reasonPhrase").value(CodeEnum.CODE_470.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.detail").value("The subscription ID specified 12345678 does not correspond to an active subscription"));

        verify(subscriptions, atLeastOnce()).deleteSubscription(any());
    }

    @Test
    public void postUnSubscribeContextWithPersistenceFailure() throws Exception {

        UnsubscribeContext unsubscribeContext = new UnsubscribeContext("12345678");
        when(subscriptions.deleteSubscription(any())).thenThrow(new SubscriptionPersistenceException("table not exist", new RuntimeException()));

        mockMvc.perform(post("/v1/unsubscribeContext")
                .content(json(mapper, unsubscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_500.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.reasonPhrase").value("error in subscription persistence"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.detail").value("table not exist"));

        verify(subscriptions, atLeastOnce()).deleteSubscription(any());
    }
}

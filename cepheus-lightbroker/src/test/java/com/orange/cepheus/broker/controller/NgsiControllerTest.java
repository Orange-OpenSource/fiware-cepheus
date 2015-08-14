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
import com.orange.cepheus.broker.Registrations;
import com.orange.cepheus.broker.Util;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.orange.cepheus.broker.Util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
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
    Registrations registrations;

    @Mock
    NgsiClient ngsiClient;

    @Mock
    Configuration configuration;

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

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        when(configuration.getRemoteBroker()).thenReturn("http://orionhost:9999");
    }

    @After
    public void resetMocks() {
        reset(registrations);
        reset(ngsiClient);
        reset(configuration);
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

        mockMvc.perform(post("/v1/registerContext").content(json(mapper, registerContext)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("registration error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value(""));
    }

    @Test
    public void postNewRegisterContext() throws Exception {

        when(registrations.addContextRegistration(any())).thenReturn("12345678");
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

        //registrations mock return always without providingApplication
        when(registrations.findProvidingApplication(any(), any())).thenReturn(getWithoutProviginApplication());
        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(),any(),any())).thenReturn(getListenableFutureUpdateContextResponseTemperature());

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on registrations Set<String> searchAttributes
        verify(registrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

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
        String urlProvider = "http://orionhost:9999/v1/updateContext";
        //verify urlProvider
        verify(ngsiClient).updateContext(eq(urlProvider), any(), updateContextArg.capture());

        // Check id correspond to the required
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("S1", contextElement.getEntityId().getId());
    }

    @Test
    public void postUpdateContextWithProvidingApplication() throws Exception {

        //registrations mock return always a providingApplication
        when(registrations.findProvidingApplication(any(), any())).thenReturn(getProviginApplication());
        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(),any(),any())).thenReturn(getListenableFutureUpdateContextResponseTemperature());

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //Capture attributes (Set<String> searchAttributes) when findProvidingApplication is called on registrations Set<String> searchAttributes
        verify(registrations).findProvidingApplication(entityIdArgumentCaptor.capture(), attributeArgumentCaptor.capture());

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
        String urlProvider = "http//iotagent:1234/v1/updateContext";
        //verify urlProvider
        verify(ngsiClient).updateContext(eq(urlProvider), any(), updateContextArg.capture());

        // Check id correspond to the required
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("S1", contextElement.getEntityId().getId());
    }

    private Iterator<URI> getWithoutProviginApplication() {
        return new Iterator<URI>() {
            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public URI next() {
                return null;
            }
        };
    }

    private Iterator<URI> getProviginApplication() {
        return new Iterator<URI>() {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public URI next() {
                URI uri = null;
                try {
                    uri = new URI("http//iotagent:1234/v1/updateContext");
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return uri;
            }
        };
    }

    private ListenableFuture<UpdateContextResponse> getListenableFutureUpdateContextResponseTemperature() {
        return new ListenableFuture<UpdateContextResponse>() {
            @Override
            public void addCallback(ListenableFutureCallback<? super UpdateContextResponse> listenableFutureCallback) {

            }

            @Override
            public void addCallback(SuccessCallback<? super UpdateContextResponse> successCallback, FailureCallback failureCallback) {

            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public UpdateContextResponse get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public UpdateContextResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                try {
                    return createUpdateContextResponseTempSensorAndPressure();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

}

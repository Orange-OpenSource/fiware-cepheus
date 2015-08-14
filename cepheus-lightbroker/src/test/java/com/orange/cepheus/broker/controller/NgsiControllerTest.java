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
import com.orange.cepheus.broker.exception.RegistrationException;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import org.junit.After;
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
import static com.orange.cepheus.broker.Util.createUpdateContextResponseTempSensorAndPressure;
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
    LocalRegistrations localRegistrations;

    @Mock
    NgsiClient ngsiClient;

    @Mock
    Configuration configuration;

    @Mock
    Iterator<URI> providingApplication;

    @Mock
    ListenableFuture<UpdateContextResponse> updateContextResponseListenableFuture;

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
        when(updateContextResponseListenableFuture.get()).thenReturn(createUpdateContextResponseTempSensorAndPressure());
        doNothing().when(updateContextResponseListenableFuture).addCallback(any(),any());
    }

    @After
    public void resetMocks() {
        reset(localRegistrations);
        reset(ngsiClient);
        reset(configuration);
        reset(providingApplication);
        reset(updateContextResponseListenableFuture);
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value(15.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value(1015))
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

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).updateContext(eq(urlProvider), any(), updateContextArg.capture());

        // Check id correspond to the required
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("S1", contextElement.getEntityId().getId());
    }

    @Test
    public void postUpdateContextWithoutProvidingApplicationAndWithoutRemoteBroker() throws Exception {

        when(configuration.getRemoteBroker()).thenReturn(null);

        //localRegistrations mock return always without providingApplication
        when(providingApplication.hasNext()).thenReturn(false);
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value(15.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value(1015))
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

    }

    @Test
    public void postUpdateContextWithProvidingApplication() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[0].value").value(15.5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].name").value("pressure"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].type").value("int"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].contextElement.attributes[1].value").value(1015))
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
        String urlProvider = "http//iotagent:1234";

        //check ListenableFuture is called at least Once and with get method
        verify(updateContextResponseListenableFuture, atLeastOnce()).get();

        //verify urlProvider
        verify(ngsiClient, atLeastOnce()).updateContext(eq(urlProvider), any(), updateContextArg.capture());

        // Check id correspond to the required
        ContextElement contextElement = updateContextArg.getValue().getContextElements().get(0);
        assertEquals("S1", contextElement.getEntityId().getId());
    }

    @Test
    public void postUpdateContextWithThrowExecutionException() throws Exception {

        //localRegistrations mock return always a providingApplication
        when(providingApplication.hasNext()).thenReturn(true);
        when(providingApplication.next()).thenReturn(new URI("http//iotagent:1234"));
        when(localRegistrations.findProvidingApplication(any(), any())).thenReturn(providingApplication);

        when(updateContextResponseListenableFuture.get()).thenThrow(new ExecutionException("execution exception", new Throwable()));

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

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

        when(updateContextResponseListenableFuture.get()).thenThrow(new InterruptedException());

        //ngsiclient mock return always createUpdateContextREsponseTemperature when call updateContext
        when(ngsiClient.updateContext(any(), any(), any())).thenReturn(updateContextResponseListenableFuture);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, createUpdateContextTempSensorAndPressure()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value("500"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value("Receiver internal error"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("An unknown error at the receiver has occured"));
    }
}

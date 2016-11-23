/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.controller;

import com.orange.cepheus.cep.Application;
import com.orange.cepheus.cep.ComplexEventProcessor;
import com.orange.cepheus.cep.EventMapper;
import com.orange.cepheus.cep.SubscriptionManager;
import com.orange.cepheus.cep.exception.EventProcessingException;
import com.orange.cepheus.cep.exception.TypeNotFoundException;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.cepheus.cep.model.Event;
import com.orange.cepheus.cep.tenant.TenantFilter;
import com.orange.ngsi.model.CodeEnum;
import com.orange.ngsi.model.NotifyContext;
import com.orange.ngsi.model.UpdateAction;
import com.orange.ngsi.model.UpdateContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static com.orange.cepheus.cep.Util.*;

/**
 * Tests for the NGSI controller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("multi-tenant")
public class NgsiControllerMultiTenantTest {

    private MockMvc mockMvc;

    @Autowired
    TenantFilter tenantFilter;

    @Mock
    SubscriptionManager subscriptionManager;

    @Mock
    EventMapper eventMapper;

    @Mock
    Event event;

    @Mock
    ComplexEventProcessor complexEventProcessor;

    @InjectMocks
    @Autowired
    private NgsiController ngsiController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MappingJackson2HttpMessageConverter mapper;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilter(tenantFilter).build();

        Configuration configuration = getBasicConf();
        mockMvc.perform(post("/v1/admin/config")
                .content(json(mapper, configuration))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void postNotifyContext() throws Exception {

        when(subscriptionManager.validateSubscriptionId(any(), any())).thenReturn(true);
        when(eventMapper.eventFromContextElement(any())).thenReturn(event);
        doNothing().when(complexEventProcessor).processEvent(any());
        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void postNotifyContextWithTypeNotFoundException() throws Exception {

        when(subscriptionManager.validateSubscriptionId(any(), any())).thenReturn(true);
        doThrow(TypeNotFoundException.class).when(eventMapper).eventFromContextElement(any());

        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_472.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_472.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.details").value("A parameter null is not valid/allowed in the request"));
    }

    @Test
    public void postNotifyContextWithEventProcessingException() throws Exception {

        when(subscriptionManager.validateSubscriptionId(any(), any())).thenReturn(true);
        doThrow(EventProcessingException.class).when(eventMapper).eventFromContextElement(any());

        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_500.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value("event processing error"));
    }

    @Test
    public void postNotifyContextWithInvalidateSubscriptionId() throws Exception {

        when(subscriptionManager.validateSubscriptionId(any(), any())).thenReturn(false);

        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_470.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_470.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.details").value("The subscription ID specified 1 does not correspond to an active subscription"));

    }

    @Test
    public void postNotifyContextWithBadServicePath() throws Exception {

        when(subscriptionManager.validateSubscriptionId(any(), any())).thenReturn(true);
        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
                .header("Fiware-Service", "smartcity")
                .header("Fiware-ServicePath", "team1")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Fiware-ServicePath must only start with a / and contain [A-Za-z0-9_/] characters"));
    }

    @Test
    public void postNotifyContextBadService() throws Exception {

        when(subscriptionManager.validateSubscriptionId(any(), any())).thenReturn(true);
        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
                .header("Fiware-Service", "%smartcity")
                .header("Fiware-ServicePath", "/team1")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Fiware-Service header can only contain [A-Za-z0-9_] characters"));
    }

    @Test
    public void postNotifyContextWithTenant() throws Exception {

        when(subscriptionManager.validateSubscriptionId(any(), any())).thenReturn(true);
        Configuration configuration = getBasicConf();
        mockMvc.perform(post("/v1/admin/config")
                .header("Fiware-Service", "smartcity")
                .header("Fiware-ServicePath", "/team1")
                .content(json(mapper, configuration))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
                .header("Fiware-Service", "smartcity")
                .header("Fiware-ServicePath", "/team1")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void postNotifyContextWithEmptySubscriptionId() throws Exception {

        NotifyContext notifyContext = new NotifyContext("", new URI("http://iotAgent"));

        mockMvc.perform(post("/v1/notifyContext")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.details").value("The parameter subscriptionId of type string is missing in the request"));
    }

    @Test
    public void postNotifyContextWithEmptyOriginator() throws Exception {

        NotifyContext notifyContext = new NotifyContext("SubscriptionId", new URI(""));

        mockMvc.perform(post("/v1/notifyContext")
                .content(json(mapper, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.details").value("The parameter originator of type URI is missing in the request"));
    }

    @Test
    public void postUpdateContextWithBadSyntax() throws Exception {

        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        mockMvc.perform(post("/v1/updateContext").content("BAD JSON").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_400.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_400.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.details").value(CodeEnum.CODE_400.getLongPhrase()));
    }

    @Test
    public void postUpdateContextWithEmptyContextElements() throws Exception {

        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        mockMvc.perform(post("/v1/updateContext").content(json(mapper, updateContext)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.details").value("The parameter contextElements of type List<ContextElement> is missing in the request"));
    }

    @Test
    public void postUpdateContextWithTypeNotExistsInConfiguration()  throws Exception {

        UpdateContext updateContext = createUpdateContextPressureSensor();
        when(eventMapper.eventFromContextElement(any())).thenReturn(event);
        doThrow(EventProcessingException.class).when(complexEventProcessor).processEvent(any());

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, updateContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value(CodeEnum.CODE_472.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.reasonPhrase")
                        .value(CodeEnum.CODE_472.getShortPhrase()));
    }

    @Test
    public void postUpdateContextBeforeConf() throws Exception {

        UpdateContext updateContext = createUpdateContextTempSensor(0);

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, updateContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

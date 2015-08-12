/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.controller;

import com.orange.cepheus.cep.Application;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.ngsi.model.CodeEnum;
import com.orange.ngsi.model.NotifyContext;
import com.orange.ngsi.model.UpdateAction;
import com.orange.ngsi.model.UpdateContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;

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
public class NgsiControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MappingJackson2HttpMessageConverter mapper;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        Configuration configuration = getBasicConf();
        mockMvc.perform(post("/v1/admin/config")
                .content(json(mapper, configuration))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void postNotifyContext() throws Exception {

        NotifyContext notifyContext = createNotifyContextTempSensor(0);

        mockMvc.perform(post("/v1/notifyContext")
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.detail").value("The parameter subscriptionId of type string is missing in the request"));
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.detail").value("The parameter originator of type URI is missing in the request"));
    }

    @Test
    public void postUpdateContextWithBadSyntax() throws Exception {

        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        mockMvc.perform(post("/v1/updateContext").content("BAD JSON").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_400.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_400.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value(CodeEnum.CODE_400.getLongPhrase()));
    }

    @Test
    public void postUpdateContextWithEmptyContextElements() throws Exception {

        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        mockMvc.perform(post("/v1/updateContext").content(json(mapper, updateContext)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("The parameter contextElements of type List<ContextElement> is missing in the request"));
    }

    @Test
    public void postUpdateContextWithTypeNotExistsInConfiguration()  throws Exception {

        UpdateContext updateContext = createUpdateContextPressureSensor();

        mockMvc.perform(post("/v1/updateContext")
                .content(json(mapper, updateContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value(CodeEnum.CODE_472.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.reasonPhrase")
                        .value(CodeEnum.CODE_472.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.detail").value(
                        "Event type named 'PressureSensor' has not been defined or is not a Map event type, the name 'PressureSensor' has not been defined as an event type"));
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

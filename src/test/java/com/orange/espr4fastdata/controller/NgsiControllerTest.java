/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.util.Util;
import com.orange.ngsi.model.CodeEnum;
import com.orange.ngsi.model.NotifyContext;
import com.orange.ngsi.model.UpdateAction;
import com.orange.ngsi.model.UpdateContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by pborscia on 05/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class NgsiControllerTest {

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Util util = new Util();

    @Autowired
    private WebApplicationContext webApplicationContext;


    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        for(HttpMessageConverter hmc : converters) {
            if (hmc instanceof MappingJackson2HttpMessageConverter) {
                this.mappingJackson2HttpMessageConverter = hmc;
            }
        }

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        Configuration configuration = util.getBasicConf();
        mockMvc.perform(post("/v1/admin/config")
                .content(this.json(configuration))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void postNotifyContext() {

        NotifyContext notifyContext = null;
        try {
            notifyContext = util.createNotifyContextTempSensor(0);
        } catch (URISyntaxException e) {
            Assert.fail("Not expected URISyntaxException for postNotifyContextBeforeConf");
        }

        try {
            mockMvc.perform(post("/v1/notifyContext")
                    .content(this.json(notifyContext))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            Assert.fail("Not expected URISyntaxException for postNotifyContextBeforeConf");
        }
    }


    @Test
    public void postNotifyContextWithEmptySubscriptionId() {

        NotifyContext notifyContext = null;
        try {
            notifyContext = new NotifyContext("", new URI("http://iotAgent"));
        } catch (URISyntaxException e) {
            Assert.fail("Not expected URISyntaxException for postNotifyContextWithEmptySubscriptionId");
        }

        try {
            mockMvc.perform(post("/v1/notifyContext")
                    .content(this.json(notifyContext))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_471.getLabel()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.detail").value("The parameter subscriptionId of type string is missing in the request"));

        } catch (Exception e) {
            Assert.fail("Not expected Exception for postNotifyContextWithEmptySubscriptionId");
        }
    }

    @Test
    public void postNotifyContextWithEmptyOriginator() {

        NotifyContext notifyContext = null;
        try {
            notifyContext = new NotifyContext("SubscriptionId", new URI(""));
        } catch (URISyntaxException e) {
            Assert.fail("Not expected URISyntaxException for postNotifyContextWithEmptyOriginator");
        }

        try {
            mockMvc.perform(post("/v1/notifyContext")
                    .content(this.json(notifyContext))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_471.getLabel()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.detail").value("The parameter originator of type URI is missing in the request"));

        } catch (Exception e) {
            Assert.fail("Not expected Exception for postNotifyContextWithEmptyOriginator");
        }
    }

    @Test
    public void postUpdateContextWithEmptyContextElements() {

        UpdateContext updateContext = null;

        updateContext = new UpdateContext(UpdateAction.UPDATE);


        try {
            mockMvc.perform(post("/v1/updateContext")
                    .content(this.json(updateContext))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("The parameter contextElements of type List<ContextElement> is missing in the request"));

        } catch (Exception e) {
            Assert.fail("Not expected Exception for postUpdateContextWithEmptyContextElements : " + e);
        }

    }

    @Test
    public void postUpdateContextWithTypeNotExistsInConfiguration(){

        UpdateContext updateContext = null;
        try {
            updateContext = util.createUpdateContextPressureSensor();
        } catch (URISyntaxException e) {
            Assert.fail("Not expected Exception for postUpdateContextWithTypeNotExistsInConfiguration : " + e);
        }

        try {
            mockMvc.perform(post("/v1/updateContext")
                    .content(this.json(updateContext))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode").doesNotExist())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.code").value(CodeEnum.CODE_472.getLabel()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.reasonPhrase").value(CodeEnum.CODE_472.getShortPhrase()))
                    .andExpect(MockMvcResultMatchers.jsonPath("$.contextElementResponses[0].statusCode.detail").value("Event type named 'PressureSensor' has not been defined or is not a Map event type, the name 'PressureSensor' has not been defined as an event type"));

        } catch (Exception e) {
            Assert.fail("Not expected Exception for postUpdateContextWithTypeNotExistsInConfiguration : " + e);
        }
    }


    @Test
    public void postUpdateContextBeforeConf() {

        UpdateContext updateContext = null;
        try {
            updateContext = util.createUpdateContextTempSensor(0);
        } catch (URISyntaxException e) {
            Assert.fail("Not expected URISyntaxException for postUpdateContextBeforeConf");
        }


        try {
            mockMvc.perform(post("/v1/updateContext")
                    .content(this.json(updateContext))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            Assert.fail("expected Exception for postUpdateContextBeforeConf");
        }
    }



    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }



}

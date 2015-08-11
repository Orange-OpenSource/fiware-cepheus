
/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static com.orange.ngsi.Util.*;

/**
 * Tests for the NGSI base controller.
 * This class uses the two NotImplementedControllerHelper and FakeControllerHelper classes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@WebAppConfiguration
public class NgsiBaseControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void checkNotifyContextNotImplemented() throws Exception {
        try {
            mockMvc.perform(post("/ni/notifyContext")
                    .content(json(mapping, createNotifyContextTempSensor(0)))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError());
            Assert.fail("should throw an UnsupportedOperationException");
        } catch (Exception ex) {
            Assert.assertEquals(UnsupportedOperationException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void checkUpdateContextNotImplemented() throws Exception {
        try {
            mockMvc.perform(post("/ni/updateContext")
                    .content(json(mapping, createUpdateContextTempSensor(0)))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError());
            Assert.fail("should throw an UnsupportedOperationException");
        } catch (Exception ex) {
            Assert.assertEquals(UnsupportedOperationException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void checkRegisterContextNotImplemented() throws Exception {
        try {
            mockMvc.perform(post("/ni/registerContext")
                    .content(json(mapping, createRegisterContextTemperature()))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError());
            Assert.fail("should throw an UnsupportedOperationException");
        } catch (Exception ex) {
            Assert.assertEquals(UnsupportedOperationException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void checkSubscribeContextNotImplemented() throws Exception {
        try {
            mockMvc.perform(post("/ni/subscribeContext")
                    .content(json(mapping, createSubscribeContextTemperature()))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().is4xxClientError());
            Assert.fail("should throw an UnsupportedOperationException");
        } catch (Exception ex) {
            Assert.assertEquals(UnsupportedOperationException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void checkNotifyContextImplemented() throws Exception {
        mockMvc.perform(post("/i/notifyContext")
                .content(json(mapping, createNotifyContextTempSensor(0)))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void checkUpdateContextImplemented() throws Exception {
        mockMvc.perform(post("/i/updateContext")
                .content(json(mapping, createUpdateContextTempSensor(0)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkRegisterContextImplemented() throws Exception {
        mockMvc.perform(post("/i/registerContext")
                .content(json(mapping, createRegisterContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkSubscribeContextImplemented() throws Exception {
        mockMvc.perform(post("/i/subscribeContext")
                .content(json(mapping, createSubscribeContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void missingParameterErrorNotify() throws Exception {

        NotifyContext notifyContext = createNotifyContextTempSensor(0);
        notifyContext.setSubscriptionId(null);

        mockMvc.perform(post("/ni/notifyContext")
                .content(json(mapping, notifyContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.detail").value(
                        "The parameter subscriptionId of type string is missing in the request"));
    }

    @Test
    public void missingParameterErrorUpdate() throws Exception {

        UpdateContext updateContext = createUpdateContextTempSensor(0);
        updateContext.setUpdateAction(null);

        mockMvc.perform(post("/ni/updateContext")
                .content(json(mapping, updateContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("The parameter updateAction of type UpdateAction is missing in the request"));
    }

    @Test
    public void missingParameterErrorRegister() throws Exception {

        RegisterContext registerContext = new RegisterContext();

        mockMvc.perform(post("/ni/registerContext")
                .content(json(mapping, registerContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.detail").value("The parameter contextRegistrations of type List<ContextRegistration> is missing in the request"));
    }

    @Test
    public void missingParameterErrorSubscribe() throws Exception {

        SubscribeContext subscribeContext = new SubscribeContext();

        mockMvc.perform(post("/ni/subscribeContext")
                .content(json(mapping, subscribeContext))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(result -> {
                    System.out.println(result.getResponse().getContentAsString());
                })
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.detail").value("The parameter entities of type List<EntityId> is missing in the request"));
    }

}



/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    private MappingJackson2HttpMessageConverter jsonConverter;

    private ObjectMapper xmlmapper = new XmlMapper();

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void checkNotifyContextNotImplemented() throws Exception {
        mockMvc.perform(
                post("/ni/notifyContext").content(json(jsonConverter, createNotifyContextTempSensor(0))).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkUpdateContextNotImplemented() throws Exception {
        mockMvc.perform(
                post("/ni/updateContext").content(json(jsonConverter, createUpdateContextTempSensor(0))).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkRegisterContextNotImplemented() throws Exception {
        mockMvc.perform(post("/ni/registerContext").content(json(jsonConverter, createRegisterContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkSubscribeContextNotImplemented() throws Exception {
        mockMvc.perform(post("/ni/subscribeContext")
                .content(json(jsonConverter, createSubscribeContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkUpdateContextSubscriptionNotImplemented() throws Exception {
        mockMvc.perform(post("/ni/updateContextSubscription")
                .content(json(jsonConverter, createUpdateContextSubscriptionTemperature()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkUnsubscribeContextNotImplemented() throws Exception {
        mockMvc.perform(post("/ni/unsubscribeContext")
                .content(json(jsonConverter, createUnsubscribeContext()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkQueryContextNotImplemented() throws Exception {
        mockMvc.perform(post("/ni/queryContext")
                .content(json(jsonConverter, createQueryContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkNotifyContextImplemented() throws Exception {
        mockMvc.perform(post("/i/notifyContext")
                .content(json(jsonConverter, createNotifyContextTempSensor(0)))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
    }

    @Test
    public void checkNotifyContextImplementedInXml() throws Exception {
        mockMvc.perform(post("/i/notifyContext")
                .content(xmlmapper.writeValueAsString(createNotifyContextTempSensor(0)))
                .contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML)).andExpect(status().isOk());
    }

    @Test
    public void checkUpdateContextImplemented() throws Exception {
        mockMvc.perform(post("/i/updateContext")
                .content(json(jsonConverter, createUpdateContextTempSensor(0)))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkUpdateContextImplementedInXml() throws Exception {
        mockMvc.perform(post("/i/updateContext")
                .content(xmlmapper.writeValueAsString(createUpdateContextTempSensor(0)))
                .contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
    }

    @Test
    public void checkRegisterContextImplemented() throws Exception {
        mockMvc.perform(post("/i/registerContext")
                .content(json(jsonConverter, createRegisterContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkRegisterContextImplementedInXml() throws Exception {
        mockMvc.perform(post("/i/registerContext")
                .content(xmlmapper.writeValueAsString(createRegisterContextTemperature()))
                .contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
    }

    @Test
    public void checkSubscribeContextImplemented() throws Exception {
        mockMvc.perform(post("/i/subscribeContext")
                .content(json(jsonConverter, createSubscribeContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkSubscribeContextImplementedInXml() throws Exception {
        mockMvc.perform(post("/i/subscribeContext")
                .content(xmlmapper.writeValueAsString(createSubscribeContextTemperature()))
                .contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
    }

    @Test
    public void checkUpdateContextSubscriptionImplemented() throws Exception {
        mockMvc.perform(post("/i/updateContextSubscription")
                .content(json(jsonConverter, createUpdateContextSubscriptionTemperature()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkUpdateContextSubscriptionImplementedInXml() throws Exception {
        mockMvc.perform(post("/i/updateContextSubscription")
                .content(xmlmapper.writeValueAsString(createUpdateContextSubscriptionTemperature()))
                .contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
    }

    @Test
    public void checkUnsubscribeContextImplemented() throws Exception {
        mockMvc.perform(post("/i/unsubscribeContext")
                .content(json(jsonConverter, createUnsubscribeContext()))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkUnsubscribeContextImplementedInXml() throws Exception {
        mockMvc.perform(post("/i/unsubscribeContext")
                .content(xmlmapper.writeValueAsString(createUnsubscribeContext()))
                .contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
    }

    @Test
    public void checkQueryContextImplemented() throws Exception {
        mockMvc.perform(
                post("/i/queryContext").content(json(jsonConverter, createQueryContextTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void checkQueryContextImplementedInXml() throws Exception {
        mockMvc.perform(
                post("/i/queryContext").content(xmlmapper.writeValueAsString(createQueryContextTemperature())).contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
    }

    @Test
    public void jsonSyntaxErrorHandling() throws Exception {

        mockMvc.perform(post("/ni/notifyContext").content("bad JSON").contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_400.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_400.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.details").value(CodeEnum.CODE_400.getLongPhrase()));
    }

    @Test
    public void xmlSyntaxErrorHandling() throws Exception {

        mockMvc.perform(post("/ni/notifyContext").content("bad xml").contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("notifyContextResponse/responseCode/code").string(CodeEnum.CODE_400.getLabel()))
                .andExpect(MockMvcResultMatchers.xpath("notifyContextResponse/responseCode/reasonPhrase").string(CodeEnum.CODE_400.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.xpath("notifyContextResponse/responseCode/details").string(CodeEnum.CODE_400.getLongPhrase()));
    }

    @Test
    public void missingParameterErrorNotify() throws Exception {

        NotifyContext notifyContext = createNotifyContextTempSensor(0);
        notifyContext.setSubscriptionId(null);

        mockMvc.perform(post("/ni/notifyContext").content(json(jsonConverter, notifyContext)).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.responseCode.details").value(
                        "The parameter subscriptionId of type string is missing in the request"));
    }

    @Test
    public void missingParameterErrorUpdate() throws Exception {

        UpdateContext updateContext = createUpdateContextTempSensor(0);
        updateContext.setUpdateAction(null);

        mockMvc.perform(post("/ni/updateContext").content(json(jsonConverter, updateContext)).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.details").value("The parameter updateAction of type string is missing in the request"));
    }

    @Test
    public void missingParameterErrorRegister() throws Exception {

        RegisterContext registerContext = new RegisterContext();

        mockMvc.perform(post("/ni/registerContext").content(json(jsonConverter, registerContext)).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.details").value("The parameter contextRegistrations of type List<ContextRegistration> is missing in the request"));
    }

    @Test
    public void missingParameterErrorSubscribe() throws Exception {

        SubscribeContext subscribeContext = new SubscribeContext();

        mockMvc.perform(post("/ni/subscribeContext").content(json(jsonConverter, subscribeContext)).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeError.errorCode.details").value("The parameter entities of type List<EntityId> is missing in the request"));
    }

    @Test
    public void missingParameterErrorUnsubscribe() throws Exception {

        UnsubscribeContext unsubscribeContext = new UnsubscribeContext();

        mockMvc.perform(post("/ni/unsubscribeContext").content(json(jsonConverter, unsubscribeContext)).contentType(MediaType.APPLICATION_JSON).header("Host","localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.details").value("The parameter subscriptionId of type String is missing in the request"));
    }

    @Test
    public void missingParameterErrorQuery() throws Exception {

        QueryContext queryContext = new QueryContext();

        mockMvc.perform(post("/ni/queryContext")
                .content(json(jsonConverter, queryContext))
                .contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.code").value(CodeEnum.CODE_471.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.reasonPhrase").value(CodeEnum.CODE_471.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.errorCode.details").value("The parameter entities of type List<EntityId> is missing in the request"));
    }
}


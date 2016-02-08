/*
 * Copyright (C) 2016 Orange
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Collections;

import static com.orange.ngsi.Util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Tests for the NGSI REST base controller.
 * This class uses the two NotImplementedRestControllerHelper and FakeRestControllerHelper classes
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@WebAppConfiguration
public class NgsiRestBaseControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonConverter;

    private ObjectMapper xmlmapper = new XmlMapper();

    @PostConstruct
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void checkAppendContextElementNotImplemented() throws Exception {
        mockMvc.perform(
                post("/rest/ni/contextEntities/test").content(json(jsonConverter, createAppendContextElementTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkUpdateContextElementNotImplemented() throws Exception {
        mockMvc.perform(
                put("/rest/ni/contextEntities/test").content(json(jsonConverter, createUpdateContextElementTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkGetContextElementNotImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/ni/contextEntities/test").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkDeleteContextElementNotImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/ni/contextEntities/test").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkAppendContextAttributeNotImplemented() throws Exception {
        mockMvc.perform(
                post("/rest/ni/contextEntities/test/attributes/temp").content(json(jsonConverter, createUpdateContextAttributeTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkUpdateContextAttributeNotImplemented() throws Exception {
        mockMvc.perform(
                put("/rest/ni/contextEntities/test/attributes/temp").content(json(jsonConverter, createUpdateContextAttributeTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkGetContextAttributeNotImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/ni/contextEntities/test/attributes/a").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkDeleteContextAttributeNotImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/ni/contextEntities/test/attributes/a").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkUpdateContextAttributeValueNotImplemented() throws Exception {
        mockMvc.perform(
                put("/rest/ni/contextEntities/test/attributes/temp/DEADBEEF").content(json(jsonConverter, createUpdateContextAttributeTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkGetContextAttributeValueNotImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/ni/contextEntities/test/attributes/temp/DEADBEEF").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkDeleteContextAttributeValueNotImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/ni/contextEntities/test/attributes/temp/DEADBEEF").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkGetContextEntitiesTypeNotImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/ni/contextEntityTypes/test").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkGetContextEntitiesTypeAttributesNotImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/ni/contextEntityTypes/test/attributes/a").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkAddSubscriptionNotImplemented() throws Exception {
        mockMvc.perform(
                post("/rest/ni/contextSubscriptions")
                        .content(json(jsonConverter, createSubscribeContextTemperature())).contentType(MediaType.APPLICATION_JSON)
                        .header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkUpdateSubscriptionNotImplemented() throws Exception {
        mockMvc.perform(
                put("/rest/ni/contextSubscriptions/12345678")
                        .content(json(jsonConverter, createUpdateContextSubscriptionTemperature())).contentType(MediaType.APPLICATION_JSON)
                        .header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void checkDeleteSubscriptionNotImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/ni/contextSubscriptions/23")
                        .header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_403.getLabel()));
    }

    @Test
    public void jsonSyntaxErrorHandling() throws Exception {

        mockMvc.perform(post("/rest/ni/contextEntities/test").content("bad JSON").contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_400.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.reasonPhrase").value(CodeEnum.CODE_400.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.details").value(CodeEnum.CODE_400.getLongPhrase()));
    }

    @Test
    public void xmlSyntaxErrorHandling() throws Exception {

        mockMvc.perform(post("/rest/ni/contextEntities/test").content("bad xml").contentType(MediaType.APPLICATION_XML).header("Host", "localhost").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.xpath("statusCode/code").string(CodeEnum.CODE_400.getLabel()))
                .andExpect(MockMvcResultMatchers.xpath("statusCode/reasonPhrase").string(CodeEnum.CODE_400.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.xpath("statusCode/details").string(CodeEnum.CODE_400.getLongPhrase()));
    }

    @Test
    public void checkUpdateSubscriptionMistmatchId() throws Exception {
        mockMvc.perform(
                put("/rest/ni/contextSubscriptions/87654321")
                        .content(json(jsonConverter, createUpdateContextSubscriptionTemperature())).contentType(MediaType.APPLICATION_JSON)
                        .header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_472.getLabel()));
    }

    @Test
    public void checkAppendContextElementImplemented() throws Exception {
        mockMvc.perform(
                post("/rest/i/contextEntities/test").content(json(jsonConverter, createAppendContextElementTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes[0].value").value("15.5"));
    }

    @Test
    public void checkUpdateContextElementImplemented() throws Exception {
        mockMvc.perform(put("/rest/i/contextEntities/test").content(json(jsonConverter, createUpdateContextElementTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].attributes[0].value").value("15.5"));
    }

    @Test
    public void checkGetContextElementImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/i/contextEntities/test").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElement.attributes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextElement.attributes[0].value").value("15.5"));
    }

    @Test
    public void checkDeleteContextElementImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/i/contextEntities/test").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));
    }

    @Test
    public void checkAppendContextAttributeImplemented() throws Exception {
        mockMvc.perform(
                post("/rest/i/contextEntities/test/attributes/temp").content(json(jsonConverter, createUpdateContextAttributeTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));
    }

    @Test
    public void checkUpdateContextAttributeImplemented() throws Exception {
        mockMvc.perform(
                put("/rest/i/contextEntities/test/attributes/temp").content(json(jsonConverter, createUpdateContextAttributeTemperature())).contentType(MediaType.APPLICATION_JSON).header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));
    }

    @Test
    public void checkGetContextAttributeImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/i/contextEntities/test/attributes/a").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].value").value("15.5"));
    }

    @Test
    public void checkDeleteContextAttributeImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/i/contextEntities/test/attributes/a").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));
    }

    @Test
    public void checkUpdateContextAttributeValueImplemented() throws Exception {
        UpdateContextAttribute updateContextAttribute = createUpdateContextAttributeTemperature();
        updateContextAttribute.getAttribute().setMetadata(Collections.singletonList(new ContextMetadata("ID", "string", "DEADBEEF")));
        mockMvc.perform(
                put("/rest/i/contextEntities/test/attributes/temp/DEADBEEF").content(json(jsonConverter, updateContextAttribute))
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));
    }

    @Test
    public void checkGetContextAttributeValueImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/i/contextEntities/test/attributes/temp/DEADBEEF").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].value").value("15.5"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].metadatas").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].metadatas[0].name").value("ID"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].metadatas[0].type").value("string"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.attributes[0].metadatas[0].value").value("DEADBEEF"));

    }

    @Test
    public void checkDeleteContextAttributeValueImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/i/contextEntities/test/attributes/temp/DEADBEEF").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));
    }


    @Test
    public void checkGetContextEntitiesTypeImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/i/contextEntityTypes/test").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.isPattern").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].value").value("15.5"));
    }

    @Test
    public void checkGetContextEntitiesTypeAttributeImplemented() throws Exception {
        mockMvc.perform(
                get("/rest/i/contextEntityTypes/test/attributes/temp").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.id").value("S1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.isPattern").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].name").value("temp"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].type").value("float"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contextResponses[0].contextElement.attributes[0].value").value("15.5"));
    }

    @Test
    public void checkAddSubscriptionImplemented() throws Exception {
        mockMvc.perform(
                post("/rest/i/contextSubscriptions")
                        .content(json(jsonConverter, createSubscribeContextTemperature())).contentType(MediaType.APPLICATION_JSON)
                        .header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.subscriptionId").value("12345678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.duration").value("P1M"));
    }

    @Test
    public void checkUpdateSubscriptionImplemented() throws Exception {
        mockMvc.perform(
                put("/rest/i/contextSubscriptions/12345678")
                        .content(json(jsonConverter, createUpdateContextSubscriptionTemperature())).contentType(MediaType.APPLICATION_JSON)
                        .header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.subscriptionId").value("12345678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscribeResponse.duration").value("P1M"));    }

    @Test
    public void checkDeleteSubscriptionImplemented() throws Exception {
        mockMvc.perform(
                delete("/rest/i/contextSubscriptions/23").header("Host", "localhost").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.code").value(CodeEnum.CODE_200.getLabel()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.reasonPhrase").value(CodeEnum.CODE_200.getShortPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode.details").value(CodeEnum.CODE_200.getLongPhrase()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.subscriptionId").value("12345678"));
    }
}


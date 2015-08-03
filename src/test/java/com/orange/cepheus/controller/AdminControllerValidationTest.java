/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.controller;

import com.orange.cepheus.Application;
import com.orange.cepheus.cep.ComplexEventProcessor;
import com.orange.cepheus.model.Attribute;
import com.orange.cepheus.model.Configuration;
import com.orange.cepheus.persistence.Persistence;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static com.orange.cepheus.util.Util.*;

/**
 * Test the Configuration validation by the Admin controller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, AdminControllerValidationTest.TestConfig.class})
@WebAppConfiguration
public class AdminControllerValidationTest {

    @SpringBootApplication
    static class TestConfig {

        @Bean
        public ComplexEventProcessor complexEventProcessor() {
            return Mockito.mock(ComplexEventProcessor.class);
        }

        @Bean
        public Persistence persistence() {
            return Mockito.mock(Persistence.class);
        }
    }

    private MockMvc mockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ComplexEventProcessor complexEventProcessor;

    @Autowired
    private Persistence persistence;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void configurationValidationHandling() throws Exception {
        Configuration configuration = getBasicConf();
        configuration.setHost(null);
        checkValidationError(configuration);

        configuration = getBasicConf();
        configuration.setEventTypeIns(null);
        checkValidationError(configuration);

        configuration = getBasicConf();
        configuration.setEventTypeOuts(null);
        checkValidationError(configuration);
    }

    @Test
    public void configurationValidationTypeIn() throws Exception {
        Configuration configuration = getBasicConf();
        configuration.getEventTypeIns().get(0).setId(null);
        checkValidationError(configuration);
        configuration.getEventTypeIns().get(0).setId("");
        checkValidationError(configuration);

        configuration = getBasicConf();
        configuration.getEventTypeIns().get(0).setType(null);
        checkValidationError(configuration);
        configuration.getEventTypeIns().get(0).setType("");
        checkValidationError(configuration);

        configuration = getBasicConf();
        configuration.getEventTypeIns().get(0).setAttributes(null);
        checkValidationError(configuration);
        configuration.getEventTypeIns().get(0).setAttributes(Collections.emptySet());
        checkValidationError(configuration);
    }

    @Test
    public void configurationValidationTypeInAttr() throws Exception {
        Configuration configuration = getBasicConf();
        Attribute testAttr = new Attribute(null, "t");
        configuration.getEventTypeIns().get(0).setAttributes(Collections.singleton(testAttr));
        checkValidationError(configuration);
        testAttr.setName("");
        checkValidationError(configuration);
        testAttr.setName("name");
        testAttr.setType(null);
        checkValidationError(configuration);
        testAttr.setType("");
        checkValidationError(configuration);
    }

    @Test
    public void configurationValidationTypeOut() throws Exception {
        Configuration configuration = getBasicConf();
        configuration.getEventTypeOuts().get(0).setId(null);
        checkValidationError(configuration);
        configuration.getEventTypeOuts().get(0).setId("");
        checkValidationError(configuration);

        configuration = getBasicConf();
        configuration.getEventTypeOuts().get(0).setType(null);
        checkValidationError(configuration);
        configuration.getEventTypeOuts().get(0).setType("");
        checkValidationError(configuration);

        configuration = getBasicConf();
        configuration.getEventTypeOuts().get(0).setAttributes(null);
        checkValidationError(configuration);
        configuration.getEventTypeOuts().get(0).setAttributes(Collections.emptySet());
        checkValidationError(configuration);
    }

    @Test
    public void configurationValidationTypeOutAttr() throws Exception {
        Configuration configuration = getBasicConf();
        Attribute testAttr = new Attribute(null, "t");
        configuration.getEventTypeOuts().get(0).setAttributes(Collections.singleton(testAttr));
        checkValidationError(configuration);
        testAttr.setName("");
        checkValidationError(configuration);
        testAttr.setName("name");
        testAttr.setType(null);
        checkValidationError(configuration);
        testAttr.setType("");
        checkValidationError(configuration);
    }

    /**
     * Helper to call the service and check it return the 400 error
     * @param configuration
     * @throws Exception
     */
    private void checkValidationError(Configuration configuration) throws Exception {
        mockMvc.perform(post("/v1/admin/config").content(json(mapping, configuration)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.reasonPhrase").value("Configuration validation error"))
                .andExpect(jsonPath("$.detail").exists());
    }
}

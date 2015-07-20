/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.persistence.Persistence;
import com.orange.espr4fastdata.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test the Admin controller
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AdminControllerTest {

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    private Util util = new Util();

    @Mock
    public Persistence persistence;

    @Autowired
    @InjectMocks
    private AdminController adminController;

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
        MockitoAnnotations.initMocks(this);
        //this.mockMvc = MockMvcBuilders.standaloneSetup(adminController).build();
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void postConfOK() throws Exception {
        Configuration configuration = util.getBasicConf();

        mockMvc.perform(post("/v1/admin/config")
                .content(this.json(configuration))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void configurationErrorHandling() throws Exception {
        Configuration configuration = util.getBasicConf();
        configuration.getStatements().add("THIS IS NOT A VALID EPL STATEMENT");

        mockMvc.perform(post("/v1/admin/config").content(this.json(configuration)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.reasonPhrase").exists())
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    public void persistenceErrorHandling() throws Exception {

        doThrow(new PersistenceException("ERROR")).when(persistence).saveConfiguration(any(Configuration.class));

        Configuration configuration = util.getBasicConf();

        mockMvc.perform(post("/v1/admin/config").content(this.json(configuration)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.reasonPhrase").value("ERROR"));
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}

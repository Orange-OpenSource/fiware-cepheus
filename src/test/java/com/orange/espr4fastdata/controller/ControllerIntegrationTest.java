/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.Application;
import com.orange.ngsi.model.NotifyContext;
import com.orange.ngsi.model.UpdateContext;
import com.orange.espr4fastdata.model.Configuration;
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
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;
import java.util.Random;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.orange.espr4fastdata.util.Util.*;

/**
 * Created by pborscia on 05/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class ControllerIntegrationTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();

        Configuration configuration = getBasicConf();
        mockMvc.perform(post("/v1/admin/config")
                .content(json(mapping, configuration))
                .contentType(contentType))
                .andExpect(status().isCreated());

    }

    @Test
    public void postConfAndNotifyContext() throws Exception {
        //Config effectué dans le setup

        Random random = new Random(15);

        for (int i=1; i<100 ; i++) {

            float value = random.nextFloat();
            NotifyContext notifyContext = createNotifyContextTempSensor(value);

            mockMvc.perform(post("/v1/notifyContext")
                    .content(json(mapping, notifyContext))
                    .contentType(contentType))
                    .andExpect(status().isOk());
        }

    }

    @Test
    public void postConfAndUpdateContext() throws Exception {
        //Config effectué dans le setup

        Random random = new Random(15);

        for (int i=1; i<100 ; i++) {

            float value = random.nextFloat();
            UpdateContext updateContext = createUpdateContextTempSensor(value);

            mockMvc.perform(post("/v1/updateContext")
                    .content(json(mapping, updateContext))
                    .contentType(contentType))
                    .andExpect(status().isOk());
        }

    }
}

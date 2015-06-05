package com.orange.newespr4fastdata.controller;

import com.orange.newespr4fastdata.Application;
import com.orange.newespr4fastdata.model.*;
import com.orange.newespr4fastdata.model.cep.*;
import com.orange.newespr4fastdata.util.Util;
import org.hamcrest.CoreMatchers;
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
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertThat;
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

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

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

    }

    @Test
    public void postNotifyContextBeforeConf() {

        NotifyContext notifyContext = null;
        try {
            notifyContext = util.createNotifyContextTempSensor(0);
        } catch (URISyntaxException e) {
            Assert.fail("Not expected URISyntaxException for postNotifyContextBeforeConf");
        }

        try {
            mockMvc.perform(post("/api/v1/ngsi/notifyContext")
                    .content(this.json(notifyContext))
                    .contentType(contentType))
                    .andExpect(status().isOk());
            Assert.fail("expected Exception for postNotifyContextBeforeConf");
        } catch (Exception e) {
            assertThat(e.getMessage(), CoreMatchers.containsString("Event type named 'TempSensor' has not been defined or is not a Map event type, the name 'TempSensor' has not been defined as an event type"));
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
            mockMvc.perform(post("/api/v1/ngsi/updateContext")
                    .content(this.json(updateContext))
                    .contentType(contentType))
                    .andExpect(status().isOk());
            Assert.fail("expected Exception for postUpdateContextBeforeConf");
        } catch (Exception e) {
            assertThat(e.getMessage(), CoreMatchers.containsString("Event type named 'TempSensor' has not been defined or is not a Map event type, the name 'TempSensor' has not been defined as an event type"));
        }
    }



    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }




}

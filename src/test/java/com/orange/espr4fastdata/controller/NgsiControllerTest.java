package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.ngsi.NotifyContext;
import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.util.Util;
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
import java.net.URISyntaxException;
import java.nio.charset.Charset;

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
        mockMvc.perform(post("/api/v1/config")
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
            mockMvc.perform(post("/api/v1/ngsi/notifyContext")
                    .content(this.json(notifyContext))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            Assert.fail("Not expected URISyntaxException for postNotifyContextBeforeConf");
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

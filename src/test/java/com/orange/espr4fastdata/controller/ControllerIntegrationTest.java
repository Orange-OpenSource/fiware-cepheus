package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.NotifyContext;
import com.orange.espr4fastdata.model.UpdateContext;
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
import java.nio.charset.Charset;
import java.util.Random;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
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
                .contentType(contentType))
                .andExpect(status().isCreated());

    }

    @Test
    public void postConfAndNotifyContext() throws Exception {
        //Config effectué dans le setup

        Random random = new Random(15);

        for (int i=1; i<100 ; i++) {

            float value = random.nextFloat();
            NotifyContext notifyContext = util.createNotifyContextTempSensor(value);

            mockMvc.perform(post("/api/v1/ngsi/notifyContext")
                    .content(this.json(notifyContext))
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
            UpdateContext updateContext = util.createUpdateContextTempSensor(value);

            mockMvc.perform(post("/api/v1/ngsi/updateContext")
                    .content(this.json(updateContext))
                    .contentType(contentType))
                    .andExpect(status().isOk());
        }

    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}

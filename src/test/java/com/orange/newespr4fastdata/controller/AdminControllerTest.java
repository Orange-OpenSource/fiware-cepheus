package com.orange.newespr4fastdata.controller;

import com.orange.newespr4fastdata.Application;
import com.orange.newespr4fastdata.model.cep.Attribute;
import com.orange.newespr4fastdata.model.cep.Conf;
import com.orange.newespr4fastdata.model.cep.EventTypeIn;
import com.orange.newespr4fastdata.model.cep.EventTypeOut;
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
import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by pborscia on 03/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class AdminControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;


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
    public void postConfOK() throws Exception {
        Conf conf = new Conf();
        conf.setHost("http://localhost:8080");
        //eventIN
        List<EventTypeIn> eventTypeIns = new ArrayList<EventTypeIn>();
        conf.setEventTypeIns(eventTypeIns);
        EventTypeIn eventTypeIn = new EventTypeIn();
        eventTypeIns.add(eventTypeIn);
        eventTypeIn.setProvider("http://iotAgent");
        eventTypeIn.setId("S.*");
        eventTypeIn.setType("TempSensor");
        eventTypeIn.setIsPattern(true);
        List<Attribute> attributes = new ArrayList<Attribute>();
        Attribute attributeTemp = new Attribute();
        attributeTemp.setName("temp");
        attributeTemp.setType("float");
        attributes.add(attributeTemp);
        eventTypeIn.setAttributes(attributes);
        //eventOUT
        List<EventTypeOut> eventTypeOuts = new ArrayList<EventTypeOut>();
        conf.setEventTypeOuts(eventTypeOuts);
        EventTypeOut eventTypeOut = new EventTypeOut();
        eventTypeOuts.add(eventTypeOut);
        eventTypeOut.setBroker("http://orion");
        eventTypeOut.setId("OUT1");
        eventTypeOut.setType("TempSensorAvg");
        eventTypeOut.setIsPattern(false);
        List<Attribute> outAttributes = new ArrayList<Attribute>();
        Attribute attributeAvgTemp = new Attribute();
        attributeAvgTemp.setName("avgTemp");
        attributeAvgTemp.setType("float");
        outAttributes.add(attributeAvgTemp);
        eventTypeOut.setAttributes(outAttributes);

        //rules
        List<String> rules = new ArrayList<String>();
        rules.add("INSERT INTO TempSensorAvg SELECT 'OUT1' as id, avg(TempSensor.temp) as avgTemp FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1' ");
        conf.setRules(rules);

        mockMvc.perform(post("/api/v1/config")
                .content(this.json(conf))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}

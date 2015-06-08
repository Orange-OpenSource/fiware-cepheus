package com.orange.newespr4fastdata.cep;

import com.orange.newespr4fastdata.model.ContextElement;
import com.orange.newespr4fastdata.model.UpdateContext;
import com.orange.newespr4fastdata.util.Util;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Created by pborscia on 08/06/2015.
 */
public class SenderTest {


    private MockRestServiceServer mockServer;
    private RestTemplate restTemplate;

    private Util util = new Util();

    @Before
    public void setup() {
        this.restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(this.restTemplate);
    }

    @Test
    public void performPost() throws Exception {
        //String responseBody = "{\"name\" : \"Ludwig van Beethoven\", \"someDouble\" : \"1.6035\"}";
        //Resource responseBody = new ClassPathResource("ludwig.json", this.getClass());
        String responseBody = util.createUpdateContextResponseTempSensor().toString();

        this.mockServer.expect(requestTo("/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
        //@SuppressWarnings("unused")

        Sender sender = new Sender();
        sender.postMessage(util.createUpdateContextTempSensor(0),"/updateContext");

    }




}

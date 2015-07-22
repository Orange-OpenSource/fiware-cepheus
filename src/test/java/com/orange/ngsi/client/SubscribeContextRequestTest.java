package com.orange.ngsi.client;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.exception.SubscribeContextRequestException;
import com.orange.espr4fastdata.util.Util;

import com.orange.ngsi.client.SubscribeContextRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Created by pborscia on 22/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubscribeContextRequestTest {

    private MockRestServiceServer mockServer;

    @Autowired
    SubscribeContextRequest subscribeContextRequest;

    @Inject
    RestTemplate restTemplate;

    private Util util = new Util();


    @Before
    public void setup() {
        this.mockServer = MockRestServiceServer.createServer(restTemplate);

    }

    @Test
    public void postSubscribeContextRequestWith500(){

        this.mockServer.expect(requestTo("http://localhost/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        try {
            subscribeContextRequest.postSubscribeContextRequest(util.createSubscribeContextTemperature(),"http://localhost/subscribeContext");
            this.mockServer.verify();
        } catch (URISyntaxException e) {
            Assert.fail("Not expected URISyntaxException");
        } catch (SubscribeContextRequestException e) {
            assertEquals("Failed Response received: 500 Internal Server Error", e.getMessage());
        }

    }

    @Test
    public void postSubscribeContextRequestWith404(){

        this.mockServer.expect(requestTo("http://localhost/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        try {
            subscribeContextRequest.postSubscribeContextRequest(util.createSubscribeContextTemperature(),"http://localhost/subscribeContext");
            this.mockServer.verify();
        } catch (URISyntaxException e) {
            Assert.fail("Not expected URISyntaxException");
        } catch (SubscribeContextRequestException e) {
            assertEquals("Failed Response received: 404 Not Found", e.getMessage());
        }

    }

    public void postSubscribeContextRequestOK() throws Exception {

        String responseBody = this.json(util.createSubscribeContextResponseTemperature());


        this.mockServer.expect(requestTo("http://localhost/subscribeContext"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));


        subscribeContextRequest.postSubscribeContextRequest(util.createSubscribeContextTemperature(),"http://localhost/subscribeContext");
        this.mockServer.verify();


    }

    protected String json(Object o) throws IOException {
        HttpMessageConverter mappingJackson2HttpMessageConverter = null;

        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();

        for(HttpMessageConverter hmc : restTemplate.getMessageConverters()) {
            if (hmc instanceof MappingJackson2HttpMessageConverter) {
                mappingJackson2HttpMessageConverter = hmc;
            }
        }

        Assert.assertNotNull("the JSON message converter must not be null",
                mappingJackson2HttpMessageConverter);

        mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);

        return mockHttpOutputMessage.getBodyAsString();
    }
}

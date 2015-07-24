package com.orange.ngsi.client;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.exception.SubscribeContextRequestException;
import com.orange.espr4fastdata.util.Util;

import com.orange.ngsi.model.SubscribeError;
import com.orange.ngsi.model.SubscribeResponse;
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
import org.springframework.web.client.AsyncRestTemplate;

import javax.inject.Inject;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.*;

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
    private AsyncRestTemplate asyncRestTemplate;

    private Util util = new Util();

    @Before
    public void setup() {
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);

    }

    @Test
    public void postSubscribeContextRequestWith500() throws SubscribeContextRequestException, URISyntaxException {

        this.mockServer.expect(requestTo("http://localhost/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        subscribeContextRequest.postSubscribeContextRequest(util.createSubscribeContextTemperature(),"http://localhost/subscribeContext", new SubscribeContextRequest.SubscribeContextResponseListener() {
                @Override
                public void onError(SubscribeError subscribeError) {
                    Assert.fail("Not expected Subscrive Error");
                }

                @Override
                public void onSuccess(SubscribeResponse subscribeResponse) {
                    Assert.fail("Not expected Success");
                }
        });
        this.mockServer.verify();

    }

    @Test
    public void postSubscribeContextRequestWith404() throws URISyntaxException, SubscribeContextRequestException {

        this.mockServer.expect(requestTo("http://localhost/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        subscribeContextRequest.postSubscribeContextRequest(util.createSubscribeContextTemperature(),"http://localhost/subscribeContext", new SubscribeContextRequest.SubscribeContextResponseListener() {
            @Override
            public void onError(SubscribeError subscribeError) {
                Assert.fail("Not expected Subscrive Error");
            }

            @Override
            public void onSuccess(SubscribeResponse subscribeResponse) {
                Assert.fail("Not expected Success");
            }
        });
        this.mockServer.verify();

    }

    @Test
    public void postSubscribeContextRequestOK() throws Exception {

        String responseBody = this.json(util.createSubscribeContextResponseTemperature());

        this.mockServer.expect(requestTo("http://localhost/subscribeContext"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.entities[*]", hasSize(1)))
                .andExpect(jsonPath("$.entities[0].id").value("Room1"))
                .andExpect(jsonPath("$.entities[0].type").value("Room"))
                .andExpect(jsonPath("$.entities[0].isPattern").value(false))
                .andExpect(jsonPath("$.attributes[*]", hasSize(1)))
                .andExpect(jsonPath("$.attributes[0]").value("temperature"))
                .andExpect(jsonPath("$.reference").value("http://localhost:1028/accumulate"))
                .andExpect(jsonPath("$.duration").value("P1M"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        subscribeContextRequest.postSubscribeContextRequest(util.createSubscribeContextTemperature(), "http://localhost/subscribeContext", new SubscribeContextRequest.SubscribeContextResponseListener() {
            @Override
            public void onError(SubscribeError subscribeError) {
                Assert.fail("Not expected Subscrive Error");
            }

            @Override
            public void onSuccess(SubscribeResponse subscribeResponse) {
                assertTrue(true);
            }
        });
        this.mockServer.verify();
    }

    protected String json(Object o) throws IOException {
        HttpMessageConverter mappingJackson2HttpMessageConverter = null;

        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();

        for(HttpMessageConverter hmc : asyncRestTemplate.getMessageConverters()) {
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

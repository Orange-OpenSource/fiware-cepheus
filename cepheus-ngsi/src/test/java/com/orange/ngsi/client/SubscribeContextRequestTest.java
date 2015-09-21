/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.inject.Inject;

import java.util.function.Consumer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static com.orange.ngsi.Util.*;

/**
 * Test for the NGSI SubscribeContext request
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class SubscribeContextRequestTest {

    private static String baseUrl = "http://localhost:8080";

    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonConverter;

    @Autowired
    NgsiClient ngsiClient;

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    private Consumer<SubscribeContextResponse> onSuccess = Mockito.mock(Consumer.class);

    private Consumer<Throwable> onFailure = Mockito.mock(Consumer.class);

    @Before
    public void tearUp() {
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);
    }

    @After
    public void tearDown() {
        reset(onSuccess);
        reset(onFailure);
    }

    @Test(expected = HttpServerErrorException.class)
    public void subscribeContextRequestWith500() throws Exception {

        mockServer.expect(requestTo(baseUrl + "/ngsi10/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ngsiClient.subscribeContext(baseUrl, null, createSubscribeContextTemperature()).get();
    }

    @Test(expected = HttpClientErrorException.class)
    public void subscribeContextRequestWith404() throws Exception {

        this.mockServer.expect(requestTo(baseUrl + "/ngsi10/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ngsiClient.subscribeContext(baseUrl, null, createSubscribeContextTemperature()).get();
    }

    @Test
    public void subscribeContextRequestOK() throws Exception {

        ngsiClient.dispatcher.registerHost(baseUrl, MediaType.APPLICATION_JSON_VALUE, true);
        String responseBody = json(jsonConverter, createSubscribeContextResponseTemperature());

        this.mockServer.expect(requestTo(baseUrl + "/ngsi10/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.entities[*]", hasSize(1))).andExpect(jsonPath("$.entities[0].id").value("Room1"))
                .andExpect(jsonPath("$.entities[0].type").value("Room")).andExpect(jsonPath("$.entities[0].isPattern").value("false"))
                .andExpect(jsonPath("$.attributes[*]", hasSize(1))).andExpect(jsonPath("$.attributes[0]").value("temperature"))
                .andExpect(jsonPath("$.reference").value("http://localhost:1028/accumulate")).andExpect(jsonPath("$.duration").value("P1M"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        SubscribeContextResponse response = ngsiClient.subscribeContext(baseUrl, null, createSubscribeContextTemperature()).get();
        this.mockServer.verify();

        Assert.assertNull(response.getSubscribeError());
        Assert.assertEquals("12345678", response.getSubscribeResponse().getSubscriptionId());
        Assert.assertEquals("P1M", response.getSubscribeResponse().getDuration());
    }
}

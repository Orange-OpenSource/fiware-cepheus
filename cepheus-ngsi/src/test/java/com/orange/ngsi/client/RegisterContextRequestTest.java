/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.model.RegisterContextResponse;
import com.orange.ngsi.model.SubscribeContextResponse;
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

import static com.orange.ngsi.Util.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Test for the NGSI RegisterContext request
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class RegisterContextRequestTest {
    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Autowired
    NgsiClient ngsiClient;

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    private Consumer<RegisterContextResponse> onSuccess = Mockito.mock(Consumer.class);

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
    public void registerContextRequestWith500() throws Exception {

        mockServer.expect(requestTo("http://localhost/registerContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ngsiClient.registerContext("http://localhost/registerContext", null, createRegisterContextTemperature()).get();
    }

    @Test(expected = HttpClientErrorException.class)
    public void subscribeContextRequestWith404() throws Exception {

        this.mockServer.expect(requestTo("http://localhost/registerContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ngsiClient.registerContext("http://localhost/registerContext", null, createRegisterContextTemperature()).get();
    }
    @Test
    public void registerContextRequestOK() throws Exception {

        String responseBody = json(mapping, createRegisterContextResponseTemperature());

        this.mockServer.expect(requestTo("http://localhost/registerContext")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.contextRegistrations[*]", hasSize(1)))
                .andExpect(jsonPath("$.contextRegistrations[0].providingApplication").value("http://localhost:1028/accumulate"))
                .andExpect(jsonPath("$.contextRegistrations[0].entities[*]", hasSize(1))).andExpect(jsonPath("$.contextRegistrations[0].entities[0].id").value("Room*"))
                .andExpect(jsonPath("$.contextRegistrations[0].entities[0].type").value("Room")).andExpect(jsonPath("$.contextRegistrations[0].entities[0].isPattern").value(true))
                .andExpect(jsonPath("$.contextRegistrations[0].attributes[*]", hasSize(1))).andExpect(jsonPath("$.contextRegistrations[0].attributes[0].name").value("temperature"))
                .andExpect(jsonPath("$.contextRegistrations[0].attributes[0].type").value("float"))
                .andExpect(jsonPath("$.contextRegistrations[0].attributes[0].isDomain").value(false))
                .andExpect(jsonPath("$.duration").value("PT10S"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        RegisterContextResponse response = ngsiClient.registerContext("http://localhost/registerContext", null, createRegisterContextTemperature()).get();
        this.mockServer.verify();

        Assert.assertNull(response.getErrorCode());
        Assert.assertEquals("123456789", response.getRegistrationId());
        Assert.assertEquals("PT10S", response.getDuration());
    }
}

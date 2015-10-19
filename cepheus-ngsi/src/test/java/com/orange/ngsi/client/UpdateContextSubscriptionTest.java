/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.model.SubscribeContextResponse;
import com.orange.ngsi.model.UpdateContextSubscriptionResponse;
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
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
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
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Test for the NGSI UpdateContextSubscription request
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class UpdateContextSubscriptionTest {

    private static String baseUrl = "http://localhost:8080";

    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonConverter;

    @Autowired
    private MappingJackson2XmlHttpMessageConverter xmlConverter;

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

        mockServer.expect(requestTo(baseUrl + "/ngsi10/updateContextSubscription")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ngsiClient.updateContextSubscription(baseUrl, null, createUpdateContextSubscriptionTemperature()).get();
    }

    @Test(expected = HttpClientErrorException.class)
    public void subscribeContextRequestWith404() throws Exception {

        this.mockServer.expect(requestTo(baseUrl + "/ngsi10/updateContextSubscription")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ngsiClient.updateContextSubscription(baseUrl, null, createUpdateContextSubscriptionTemperature()).get();
    }

    @Test
    public void updateContextSubscriptionRequestOK() throws Exception {

        ngsiClient.protocolRegistry.registerHost(baseUrl, true);
        String responseBody = json(jsonConverter, createUpdateContextSubscriptionResponseTemperature());

        this.mockServer.expect(requestTo(baseUrl + "/ngsi10/updateContextSubscription"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.subscriptionId").value("12345678"))
                .andExpect(jsonPath("$.duration").value("P1M"))
                .andExpect(jsonPath("$.throttling").value("PT1S"))
                .andExpect(jsonPath("$.restriction.attributeExpression").value("xpath/expression"))
                .andExpect(jsonPath("$.restriction.scopes[0].type").value("type"))
                .andExpect(jsonPath("$.restriction.scopes[0].value").value("value"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        UpdateContextSubscriptionResponse response = ngsiClient.updateContextSubscription(baseUrl, null, createUpdateContextSubscriptionTemperature()).get();
        this.mockServer.verify();

        Assert.assertNull(response.getSubscribeError());
        Assert.assertEquals("12345678", response.getSubscribeResponse().getSubscriptionId());
        Assert.assertEquals("P1M", response.getSubscribeResponse().getDuration());
    }

    @Test
    public void updateContextSubscriptionRequestOK_XML() throws Exception {

        ngsiClient.protocolRegistry.unregisterHost(baseUrl);
        String responseBody = xml(xmlConverter, createUpdateContextSubscriptionResponseTemperature());

        this.mockServer.expect(requestTo(baseUrl + "/ngsi10/updateContextSubscription"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Content-Type", MediaType.APPLICATION_XML_VALUE))
                .andExpect(header("Accept", MediaType.APPLICATION_XML_VALUE))
                .andExpect(xpath("updateContextSubscriptionRequest/subscriptionId").string("12345678"))
                .andExpect(xpath("updateContextSubscriptionRequest/duration").string("P1M"))
                .andExpect(xpath("updateContextSubscriptionRequest/throttling").string("PT1S"))
                .andExpect(xpath("updateContextSubscriptionRequest/restriction/attributeExpression").string("xpath/expression"))
                .andExpect(xpath("updateContextSubscriptionRequest/restriction/scope/operationScope/scopeType").string("type"))
                .andExpect(xpath("updateContextSubscriptionRequest/restriction/scope/operationScope/scopeValue").string("value"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_XML));

        UpdateContextSubscriptionResponse response = ngsiClient.updateContextSubscription(baseUrl, null, createUpdateContextSubscriptionTemperature()).get();
        this.mockServer.verify();

        Assert.assertNull(response.getSubscribeError());
        Assert.assertEquals("12345678", response.getSubscribeResponse().getSubscriptionId());
        Assert.assertEquals("P1M", response.getSubscribeResponse().getDuration());
    }
}

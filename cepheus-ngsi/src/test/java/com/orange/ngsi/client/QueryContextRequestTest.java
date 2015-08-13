/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.model.CodeEnum;
import com.orange.ngsi.model.NotifyContextResponse;
import com.orange.ngsi.model.QueryContextResponse;
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
 * Test for the NGSI QueryContext request
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class QueryContextRequestTest {

    private static String baseUrl = "http://localhost:8080";

    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Autowired
    NgsiClient ngsiClient;

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    private Consumer<QueryContextResponse> onSuccess = Mockito.mock(Consumer.class);

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
    public void queryContextRequestWith500() throws Exception {

        mockServer.expect(requestTo(baseUrl + "/ngsi10/queryContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ngsiClient.queryContext(baseUrl, null, createQueryContextTemperature()).get();
    }

    @Test(expected = HttpClientErrorException.class)
    public void queryContextRequestWith404() throws Exception {

        mockServer.expect(requestTo(baseUrl + "/ngsi10/queryContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ngsiClient.queryContext(baseUrl, null, createQueryContextTemperature()).get();
    }

    @Test
    public void queryContextRequestOK() throws Exception {

        String responseBody = json(mapping, createQueryContextResponseTemperature());

        this.mockServer.expect(requestTo(baseUrl + "/ngsi10/queryContext")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.entities[*]", hasSize(1)))
                .andExpect(jsonPath("$.entities[0].id").value("S*"))
                .andExpect(jsonPath("$.entities[0].type").value("TempSensor"))
                .andExpect(jsonPath("$.entities[0].isPattern").value(true))
                .andExpect(jsonPath("$.attributes[*]", hasSize(1)))
                .andExpect(jsonPath("$.attributes[0]").value("temp"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        QueryContextResponse response = ngsiClient.queryContext(baseUrl, null, createQueryContextTemperature()).get();
        this.mockServer.verify();

        Assert.assertEquals(1, response.getContextElementResponses().size());
        Assert.assertEquals(CodeEnum.CODE_200.getLabel(), response.getContextElementResponses().get(0).getStatusCode().getCode());
        Assert.assertEquals("S1", response.getContextElementResponses().get(0).getContextElement().getEntityId().getId());
        Assert.assertEquals("TempSensor", response.getContextElementResponses().get(0).getContextElement().getEntityId().getType());
        Assert.assertEquals(false, response.getContextElementResponses().get(0).getContextElement().getEntityId().getIsPattern());
        Assert.assertEquals(1, response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().size());
        Assert.assertEquals("temp", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getName());
        Assert.assertEquals("float", response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getType());
        Assert.assertEquals(15.5, response.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getValue());
    }

}

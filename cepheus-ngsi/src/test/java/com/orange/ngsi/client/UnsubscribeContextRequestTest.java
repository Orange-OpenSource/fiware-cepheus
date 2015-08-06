package com.orange.ngsi.client;

import com.orange.ngsi.TestConfiguration;
import com.orange.ngsi.model.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
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

import java.net.URISyntaxException;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.hasToString;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.mockito.Mockito.*;
import static com.orange.ngsi.Util.*;

/**
 * Test for the NGSI UnsubscribeContext request
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class UnsubscribeContextRequestTest {

    private final String providerURL = "http://localhost/unsubscribeContext";

    private final String subscriptionID = "SLJLSKDM%LSKDM%LKDS";

    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Autowired
    NgsiClient ngsiClient;

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    private Consumer<UnsubscribeContextResponse> onSuccess = Mockito.mock(Consumer.class);

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
    public void unsubscribeContextRequestWith500() throws Exception {

        this.mockServer.expect(requestTo(providerURL)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ngsiClient.unsubscribeContext(providerURL, null, subscriptionID).get();
    }

    @Test(expected = HttpClientErrorException.class)
    public void unsubscribeContextRequestWith404() throws Exception {

        this.mockServer.expect(requestTo(providerURL)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        ngsiClient.unsubscribeContext(providerURL, null, subscriptionID).get();
    }

    @Test
    public void unsubscribeContextRequestOK() throws Exception {

        String responseBody = json(mapping, createUnsubscribeContextResponse(CodeEnum.CODE_200, subscriptionID));

        this.mockServer.expect(requestTo(providerURL)).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.subscriptionId", hasToString(subscriptionID))).andRespond(
                withSuccess(responseBody, MediaType.APPLICATION_JSON));

        UnsubscribeContextResponse response = ngsiClient.unsubscribeContext(providerURL, null, subscriptionID).get();

        this.mockServer.verify();

        Assert.assertEquals(subscriptionID, response.getSubscriptionId());
        Assert.assertEquals(CodeEnum.CODE_200.getLabel(), response.getStatusCode().getCode());
    }

    private UnsubscribeContextResponse createUnsubscribeContextResponse(CodeEnum code, String subcriptionID) {
        return new UnsubscribeContextResponse(new StatusCode(code), subcriptionID);
    }
}

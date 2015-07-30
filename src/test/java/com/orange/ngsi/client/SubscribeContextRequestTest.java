package com.orange.ngsi.client;

import com.orange.espr4fastdata.Application;

import com.orange.ngsi.model.SubscribeError;
import com.orange.ngsi.model.SubscribeResponse;
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

import javax.inject.Inject;

import java.net.URISyntaxException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static com.orange.espr4fastdata.util.Util.*;


/**
 * Created by pborscia on 22/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubscribeContextRequestTest {

    private MockRestServiceServer mockServer;

    @Autowired private MappingJackson2HttpMessageConverter mapping;

    @Autowired SubscribeContextRequest subscribeContextRequest;

    @Inject private AsyncRestTemplate asyncRestTemplate;

    @Before public void setup() {
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);

    }

    @Test public void postSubscribeContextRequestWith500() throws URISyntaxException {

        SubscribeContextRequest.SubscribeContextResponseListener listener = Mockito.mock(SubscribeContextRequest.SubscribeContextResponseListener.class);

        this.mockServer.expect(requestTo("http://localhost/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        subscribeContextRequest
                .postSubscribeContextRequest(createSubscribeContextTemperature(), "http://localhost/subscribeContext", listener);
        this.mockServer.verify();

        verify(listener).onError(isNull(SubscribeError.class), any(HttpClientErrorException.class));
        verify(listener, never()).onSuccess(anyObject());
    }

    @Test public void postSubscribeContextRequestWith404() throws URISyntaxException {

        SubscribeContextRequest.SubscribeContextResponseListener listener = Mockito.mock(SubscribeContextRequest.SubscribeContextResponseListener.class);

        this.mockServer.expect(requestTo("http://localhost/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        subscribeContextRequest
                .postSubscribeContextRequest(createSubscribeContextTemperature(), "http://localhost/subscribeContext", listener);

        this.mockServer.verify();

        verify(listener).onError(isNull(SubscribeError.class), any(HttpClientErrorException.class));
        verify(listener, never()).onSuccess(anyObject());
    }

    @Test public void postSubscribeContextRequestOK() throws Exception {

        SubscribeContextRequest.SubscribeContextResponseListener listener = Mockito.mock(SubscribeContextRequest.SubscribeContextResponseListener.class);

        String responseBody = json(mapping, createSubscribeContextResponseTemperature());

        this.mockServer.expect(requestTo("http://localhost/subscribeContext")).andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.entities[*]", hasSize(1))).andExpect(jsonPath("$.entities[0].id").value("Room1"))
                .andExpect(jsonPath("$.entities[0].type").value("Room")).andExpect(jsonPath("$.entities[0].isPattern").value(false))
                .andExpect(jsonPath("$.attributes[*]", hasSize(1))).andExpect(jsonPath("$.attributes[0]").value("temperature"))
                .andExpect(jsonPath("$.reference").value("http://localhost:1028/accumulate")).andExpect(jsonPath("$.duration").value("P1M"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        subscribeContextRequest
                .postSubscribeContextRequest(createSubscribeContextTemperature(), "http://localhost/subscribeContext", listener);
        this.mockServer.verify();

        verify(listener, never()).onError(any(SubscribeError.class), any(Throwable.class));

        ArgumentCaptor<SubscribeResponse> responseArg = ArgumentCaptor.forClass(SubscribeResponse.class);
        verify(listener).onSuccess(responseArg.capture());

        SubscribeResponse response = responseArg.getValue();
        Assert.assertEquals("12345678", response.getSubscriptionId());
        Assert.assertEquals("P1M", response.getDuration());
    }
}

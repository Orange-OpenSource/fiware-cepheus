/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.Broker;
import com.orange.ngsi.model.UpdateAction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.web.client.AsyncRestTemplate;

import java.io.IOException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;
import static com.orange.espr4fastdata.util.Util.*;

import javax.inject.Inject;

/**
 * Created by pborscia on 08/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class UpdateContextRequestTest {

    private MockRestServiceServer mockServer;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Autowired
    private UpdateContextRequest updateContextRequest;

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    @Before
    public void setup() {
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);

    }

    @Test
    public void performPostWith200() throws Exception {

        String responseBody = json(mapping, createUpdateContextResponseTempSensor());

        this.mockServer.expect(requestTo(getBroker().getUrl())).andExpect(method(HttpMethod.POST))
                .andExpect(header("Fiware-Service", getBroker().getServiceName()))
                .andExpect(header("Fiware-ServicePath", getBroker().getServicePath()))
                .andExpect(jsonPath("$.updateAction").value(UpdateAction.UPDATE.getLabel()))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        updateContextRequest.postUpdateContextRequest(createUpdateContextTempSensor(0), getBroker());

        this.mockServer.verify();

    }
    //{"contextElements":[{"id":"S1","type":"TempSensor","isPattern":false,"attributeDomainName":null,"contextAttributeList":[{"name":"temp","type":"float","contextValue":"15.5","metadata":null}],"contextMetadataList":null}],"updateAction":"UPDATE"}

    @Test
    public void performPostWith404() throws Exception {

        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        updateContextRequest.postUpdateContextRequest(createUpdateContextTempSensor(0), getBroker());

        this.mockServer.verify();

    }

    @Test
    public void performPostWith500() throws Exception {

        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        updateContextRequest.postUpdateContextRequest(createUpdateContextTempSensor(0), getBroker());

        this.mockServer.verify();

    }

    @Test
    public void performPostWithTimeout() throws Exception {

        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(TimeoutResponseCreator.withTimeout());

        updateContextRequest.postUpdateContextRequest(createUpdateContextTempSensor(0), getBroker());

    }

    private Broker getBroker() {
        Broker broker = new Broker("http://localhost/updateContext",false);
        broker.setServiceName("My tenant");
        broker.setServicePath("/*");

        return broker;
    }


    public static class TimeoutResponseCreator implements ResponseCreator {

        @Override
        public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        public static TimeoutResponseCreator withTimeout() {
            return new TimeoutResponseCreator();
        }
    }

}

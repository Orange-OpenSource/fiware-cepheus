/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.cep.Broker;
import com.orange.espr4fastdata.model.ngsi.UpdateAction;
import com.orange.espr4fastdata.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

import javax.inject.Inject;

/**
 * Created by pborscia on 08/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SenderTest {


    private MockRestServiceServer mockServer;

    @Autowired
    private Sender sender;

    @Inject
    private AsyncRestTemplate asyncRestTemplate;

    private Util util = new Util();


    @Before
    public void setup() {
        this.mockServer = MockRestServiceServer.createServer(asyncRestTemplate);

    }

    @Test
    public void performPostWith200() throws Exception {

        String responseBody = this.json(util.createUpdateContextResponseTempSensor());

        this.mockServer.expect(requestTo(getBroker().getUrl())).andExpect(method(HttpMethod.POST))
                .andExpect(header("Fiware-Service", getBroker().getServiceName()))
                .andExpect(header("Fiware-ServicePath", getBroker().getServicePath()))
                .andExpect(jsonPath("$.updateAction").value(UpdateAction.UPDATE.getLabel()))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));
        //@SuppressWarnings("unused")

        sender.postMessage(util.createUpdateContextTempSensor(0),getBroker());

        this.mockServer.verify();

    }
    //{"contextElements":[{"id":"S1","type":"TempSensor","isPattern":false,"attributeDomainName":null,"contextAttributeList":[{"name":"temp","type":"float","contextValue":"15.5","metadata":null}],"contextMetadataList":null}],"updateAction":"UPDATE"}

    @Test
    public void performPostWith404() throws Exception {

        String responseBody = this.json(util.createUpdateContextResponseTempSensor());

        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        //@SuppressWarnings("unused")

        sender.postMessage(util.createUpdateContextTempSensor(0),getBroker());

        this.mockServer.verify();

    }

    @Test
    public void performPostWith500() throws Exception {

        String responseBody = this.json(util.createUpdateContextResponseTempSensor());

        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));
        //@SuppressWarnings("unused")

        sender.postMessage(util.createUpdateContextTempSensor(0),getBroker());

        this.mockServer.verify();

    }

    @Test
    public void performPostWithTimeout() throws Exception {

        this.mockServer.expect(requestTo("http://localhost/updateContext")).andExpect(method(HttpMethod.POST))
                .andRespond(TimeoutResponseCreator.withTimeout());

        sender.postMessage(util.createUpdateContextTempSensor(0),getBroker());


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
                Thread.sleep(3000);
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

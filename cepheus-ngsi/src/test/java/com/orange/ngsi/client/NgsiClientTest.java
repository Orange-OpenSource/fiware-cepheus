/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.client;

import com.orange.ngsi.ProtocolRegistry;
import com.orange.ngsi.TestConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Created by pborscia on 21/09/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class NgsiClientTest {

    @Mock
    public ProtocolRegistry protocolRegistry;

    @Autowired
    @InjectMocks
    public NgsiClient ngsiClient;

    @Before
    public void setup() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() {
        reset(protocolRegistry);
    }

    @Test
    public void getRequestHeadersWithoutUrl() {
        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders();

        assertEquals(MediaType.APPLICATION_XML, httpHeaders.getContentType());
        assertTrue(httpHeaders.getAccept().contains(MediaType.APPLICATION_XML));
    }

    @Test
    public void getRequestHeadersWithNullUrl() {
        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders(null);

        assertEquals(MediaType.APPLICATION_XML, httpHeaders.getContentType());
        assertTrue(httpHeaders.getAccept().contains(MediaType.APPLICATION_XML));
    }

    @Test
    public void getRequestHeadersWithUrlXml() {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(true);
        when(protocolRegistry.supportV1Json(any())).thenReturn(false);

        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders("localhost");

        assertEquals(MediaType.APPLICATION_XML, httpHeaders.getContentType());
        assertTrue(httpHeaders.getAccept().contains(MediaType.APPLICATION_XML));
    }

    @Test
    public void getRequestHeadersWithUrlV1Json() {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(false);
        when(protocolRegistry.supportV1Json(any())).thenReturn(true);

        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders("localhost");

        assertEquals(MediaType.APPLICATION_JSON, httpHeaders.getContentType());
        assertTrue(httpHeaders.getAccept().contains(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getRequestHeadersWithUrlV2Json() {

        // prepare mock
        when(protocolRegistry.supportXml(any())).thenReturn(false);
        when(protocolRegistry.supportV1Json(any())).thenReturn(false);

        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders("localhost");

        assertEquals(MediaType.APPLICATION_JSON, httpHeaders.getContentType());
        assertTrue(httpHeaders.getAccept().contains(MediaType.APPLICATION_JSON));
    }

}

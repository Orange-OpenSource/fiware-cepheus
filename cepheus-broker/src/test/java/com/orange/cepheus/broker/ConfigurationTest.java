/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.cepheus.broker.exception.MissingRemoteBrokerException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests for Configuration management. Warning : it is necessary to keep order tests
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource(locations="classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ConfigurationTest {

    @Autowired
    Configuration configuration;

    @Test
    public void checkPropertiesValues() throws MissingRemoteBrokerException {
        assertEquals("http://localhost:8081", configuration.getLocalUrl());
        assertEquals("http://10.25.12.123:8081", configuration.getRemoteUrl());
        assertEquals("gateway", configuration.getRemoteServiceName());
        assertEquals("gateway1", configuration.getRemoteServicePath());
        assertEquals("XXXXXXXXX", configuration.getRemoteAuthToken());
        assertEquals(true, configuration.isRemoteForwardUpdateContext());
    }

    @Test
    public void getHeadersForBroker(){
        configuration.setRemoteServiceName("SERVICE");
        configuration.setRemoteServicePath("PATH");
        configuration.setRemoteAuthToken("TOKEN");
        configuration.setRemoteForwardUpdateContext(false);

        HttpHeaders httpHeaders = new HttpHeaders();
        configuration.addRemoteHeaders(httpHeaders);

        assertEquals("SERVICE", httpHeaders.getFirst("Fiware-Service"));
        assertEquals("PATH", httpHeaders.getFirst("Fiware-ServicePath"));
        assertEquals("TOKEN", httpHeaders.getFirst("X-Auth-Token"));
    }

    @Test
    public void getHeadersForBrokerWithNullParameters(){
        configuration.setRemoteServiceName(null);
        configuration.setRemoteServicePath(null);
        configuration.setRemoteAuthToken(null);

        HttpHeaders httpHeaders = new HttpHeaders();
        configuration.addRemoteHeaders(httpHeaders);

        assertNull(httpHeaders.getFirst("Fiware-Service"));
        assertNull(httpHeaders.getFirst("Fiware-ServicePath"));
        assertNull(httpHeaders.getFirst("X-Auth-Token"));
    }
}

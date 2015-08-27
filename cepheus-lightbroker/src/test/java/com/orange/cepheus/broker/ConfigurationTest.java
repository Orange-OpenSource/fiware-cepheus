/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpHeaders;
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
public class ConfigurationTest {

    @Autowired
    Configuration configuration;

    @Test
    public void acheckPropertiesValues() {
        assertEquals("http://localhost:8081", configuration.getLocalBroker());
        assertEquals("http://10.25.12.123:8081", configuration.getRemoteBroker().getUrl());
        assertEquals("gateway", configuration.getRemoteBroker().getServiceName());
        assertEquals("gateway1", configuration.getRemoteBroker().getServicePath());
        assertEquals("XXXXXXXXX", configuration.getRemoteBroker().getAuthToken());
    }

    @Test
    public void bgetHeadersForBroker(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders = configuration.getHeadersForBroker(httpHeaders);
        assertEquals("gateway", httpHeaders.getFirst("Fiware-Service"));
        assertEquals("gateway1", httpHeaders.getFirst("Fiware-ServicePath"));
        assertEquals("XXXXXXXXX", httpHeaders.getFirst("X-Auth-Token"));
    }

    @Test
    public void cgetHeadersForBrokerWithNullParameters(){
        configuration.setRemoteBroker(new Configuration.RemoteBroker());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders = configuration.getHeadersForBroker(httpHeaders);
        assertNull(httpHeaders.getFirst("Fiware-Service"));
        assertNull(httpHeaders.getFirst("Fiware-ServicePath"));
        assertNull(httpHeaders.getFirst("X-Auth-Token"));
    }
}

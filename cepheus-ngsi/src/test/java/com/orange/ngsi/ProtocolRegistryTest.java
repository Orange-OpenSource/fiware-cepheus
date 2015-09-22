/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi;

import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * handles tests of the ProtocolRegistry class
 */
public class ProtocolRegistryTest {

    ProtocolRegistry protocolRegistry = new ProtocolRegistry();

    @Test
    public void supportV1JsonTest() throws URISyntaxException {
        protocolRegistry.registerHost("http://localhost:8080", true);
        assertEquals(true, protocolRegistry.supportV1Json("http://localhost:8080"));
        assertEquals(false, protocolRegistry.supportV2Json("http://localhost:8080"));
        assertEquals(false, protocolRegistry.supportXml("http://localhost:8080"));
    }

    @Test
    public void supportV2JsonTest() throws URISyntaxException {
        protocolRegistry.registerHost("http://localhost:8081", false);
        assertEquals(false, protocolRegistry.supportV1Json("http://localhost:8081"));
        assertEquals(true, protocolRegistry.supportV2Json("http://localhost:8081"));
        assertEquals(false, protocolRegistry.supportXml("http://localhost:8081"));
    }

    @Test
    public void supportXmlTest() throws URISyntaxException {
        assertEquals(false, protocolRegistry.supportV1Json("http://localhost:8082"));
        assertEquals(false, protocolRegistry.supportV2Json("http://localhost:8082"));
        assertEquals(true, protocolRegistry.supportXml("http://localhost:8082"));
    }

    @Test
    public void registerHostWithURIExceptionTest() {
        protocolRegistry.registerHost("http://localhost :8082", true);
    }

    @Test
    public void supportV1JsonWithURIExceptionTest() throws URISyntaxException {
        assertEquals(false, protocolRegistry.supportV1Json("http://localhost :8082"));
    }

    @Test
    public void supportV2JsonWithURIExceptionTest() throws URISyntaxException {
        assertEquals(false, protocolRegistry.supportV2Json("http://localhost :8082"));
    }

    @Test
    public void supportXmlWithURIExceptionTest() throws URISyntaxException {
        assertEquals(true, protocolRegistry.supportXml("http://localhost :8082"));
    }
}

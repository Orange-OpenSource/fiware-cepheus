/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi;

import org.junit.Test;
import org.springframework.http.MediaType;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * handles tests of the Dispatcher class
 */
public class DispatcherTest {

    Dispatcher dispatcher = new Dispatcher();

    @Test
    public void supportV1JsonTest() throws URISyntaxException {
        dispatcher.registerHost("http://localhost:8080", true);
        assertEquals(true, dispatcher.supportV1Json("http://localhost:8080"));
        assertEquals(false, dispatcher.supportV2Json("http://localhost:8080"));
        assertEquals(false, dispatcher.supportXml("http://localhost:8080"));
    }

    @Test
    public void supportV2JsonTest() throws URISyntaxException {
        dispatcher.registerHost("http://localhost:8081", false);
        assertEquals(false, dispatcher.supportV1Json("http://localhost:8081"));
        assertEquals(true, dispatcher.supportV2Json("http://localhost:8081"));
        assertEquals(false, dispatcher.supportXml("http://localhost:8081"));
    }

    @Test
    public void supportXmlTest() throws URISyntaxException {
        assertEquals(false, dispatcher.supportV1Json("http://localhost:8082"));
        assertEquals(false, dispatcher.supportV2Json("http://localhost:8082"));
        assertEquals(true, dispatcher.supportXml("http://localhost:8082"));
    }

    @Test
    public void registerHostWithURIExceptionTest() {
        dispatcher.registerHost("http://localhost :8082", true);
    }

    @Test
    public void supportV1JsonWithURIExceptionTest() throws URISyntaxException {
        assertEquals(false, dispatcher.supportV1Json("http://localhost :8082"));
    }

    @Test
    public void supportV2JsonWithURIExceptionTest() throws URISyntaxException {
        assertEquals(false, dispatcher.supportV2Json("http://localhost :8082"));
    }

    @Test
    public void supportXmlWithURIExceptionTest() throws URISyntaxException {
        assertEquals(true, dispatcher.supportXml("http://localhost :8082"));
    }
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import org.junit.Test;
import org.springframework.http.HttpHeaders;
import static org.junit.Assert.assertEquals;

/**
 * FiwareHeaders tests
 */
public class FiwareHeadersTest {

    @Test
    public void checkAddToHttpHeaders() {
        FiwareHeaders fiwareHeaders = new FiwareHeaders("service", "path", "XXX");
        HttpHeaders httpHeaders = new HttpHeaders();
        fiwareHeaders.addToHttpHeaders(httpHeaders);
        assertEquals("service", httpHeaders.get("Fiware-Service").get(0));
        assertEquals("path", httpHeaders.get("Fiware-ServicePath").get(0));
        assertEquals("XXX", httpHeaders.get("X-Auth-Token").get(0));
    }

    @Test
    public void checkToString() {
        FiwareHeaders fiwareHeaders = new FiwareHeaders("service", "path", "XXX");
        assertEquals("FiwareHeaders{fiwareService='service', fiwarePath='path', fiwareToken='XXX'}", fiwareHeaders.toString());
    }
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * GeoPoint tests
 */
public class GeoPointTest {

    @Test
    public void checkParsing() {
        GeoPoint geoPoint = GeoPoint.parse("46.434,1.3434");
        assertEquals(46.434, geoPoint.getLatitude(), 0.001);
        assertEquals(1.3434, geoPoint.getLongitude(), 0.001);
    }

    @Test
    public void checkParsingSpaces() {
        GeoPoint geoPoint = GeoPoint.parse("46.434   ,    1.3434");
        assertEquals(46.434, geoPoint.getLatitude(), 0.001);
        assertEquals(1.3434, geoPoint.getLongitude(), 0.001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkParsingMissingCoord() {
        GeoPoint.parse("46.434,");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkParsingMissingComma() {
        GeoPoint.parse("46.434 12.23");
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkParsingBadFormat() {
        GeoPoint.parse("A, B");
    }

    @Test
    public void checkSetterGetters() {
        GeoPoint geoPoint = new GeoPoint(46.434, 1.3434);
        assertEquals(46.434, geoPoint.getLatitude(), 0.001);
        assertEquals(1.3434, geoPoint.getLongitude(), 0.001);
        geoPoint.setLatitude(1);
        geoPoint.setLongitude(2);
        assertEquals(1, geoPoint.getLatitude(), 0.001);
        assertEquals(2, geoPoint.getLongitude(), 0.001);
    }

    @Test
    public void checkNGSIFormat() {
        GeoPoint geoPoint = new GeoPoint(46.434, 1.3434);
        assertEquals("46.434, 1.3434", geoPoint.toNGSIString());
    }
}

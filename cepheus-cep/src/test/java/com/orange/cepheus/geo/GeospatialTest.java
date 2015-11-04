/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.geo;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.orange.cepheus.cep.Application;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

/**
 * Test the Geospatial features for esper
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GeospatialTest {

    private EPServiceProvider epService;

    /**
     * Inject Geospatial features in esper's configuration
     */
    @Before
    public void setUp() {
        com.espertech.esper.client.Configuration configuration = new com.espertech.esper.client.Configuration();
        Geospatial.registerConfiguration(configuration);
        epService = EPServiceProviderManager.getDefaultProvider(configuration);
    }

    @Test
    public void testGeometryTypeVariable() {
        epService.getEPAdministrator().createEPL("create variable Geometry test");
    }

    @Test
    public void testPointMethod() {
        epService.getEPAdministrator().createEPL("create variable Geometry pointVar = point(1.1, 2.2)");
        Object value = epService.getEPRuntime().getVariableValue("pointVar");
        assertNotNull(value);
        assertThat(value, instanceOf(Point.class));

        Point point = (Point)value;
        assertEquals(1.1, point.getCoordinate().getOrdinate(Coordinate.X), 0.001);
        assertEquals(2.2, point.getCoordinate().getOrdinate(Coordinate.Y), 0.001);
    }

    @Test
    public void testPolyMethod() {
        epService.getEPAdministrator().createEPL("create variable Geometry polyVar = polygon({point(0, 0), point(0, 50), point(50, 50), point(50, 0), point(0, 0)})");
        Object value = epService.getEPRuntime().getVariableValue("polyVar");
        assertNotNull(value);
        assertThat(value, instanceOf(Polygon.class));

        Polygon poly = (Polygon)value;
        assertEquals(0, poly.getCoordinates()[0].getOrdinate(Coordinate.X), 0.001);
        assertEquals(0, poly.getCoordinates()[0].getOrdinate(Coordinate.Y), 0.001);
        assertEquals(0, poly.getCoordinates()[1].getOrdinate(Coordinate.X), 0.001);
        assertEquals(50, poly.getCoordinates()[1].getOrdinate(Coordinate.Y), 0.001);
        assertEquals(50, poly.getCoordinates()[2].getOrdinate(Coordinate.X), 0.001);
        assertEquals(50, poly.getCoordinates()[2].getOrdinate(Coordinate.Y), 0.001);
        assertEquals(50, poly.getCoordinates()[3].getOrdinate(Coordinate.X), 0.001);
        assertEquals(0, poly.getCoordinates()[3].getOrdinate(Coordinate.Y), 0.001);
    }

    @Test
    public void testGeometryMethod() {
        epService.getEPAdministrator().createEPL("create variable Geometry geoVar = geometry(\"POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))\")");
        Object value = epService.getEPRuntime().getVariableValue("geoVar");
        assertNotNull(value);
        assertThat(value, instanceOf(Polygon.class));

        Polygon poly = (Polygon)value;
        assertEquals(20, poly.getCoordinates()[0].getOrdinate(Coordinate.X), 0.001);
        assertEquals(10, poly.getCoordinates()[0].getOrdinate(Coordinate.Y), 0.001);
        assertEquals(30, poly.getCoordinates()[1].getOrdinate(Coordinate.X), 0.001);
        assertEquals(0, poly.getCoordinates()[1].getOrdinate(Coordinate.Y), 0.001);
        assertEquals(40, poly.getCoordinates()[2].getOrdinate(Coordinate.X), 0.001);
        assertEquals(10, poly.getCoordinates()[2].getOrdinate(Coordinate.Y), 0.001);
        assertEquals(30, poly.getCoordinates()[3].getOrdinate(Coordinate.X), 0.001);
        assertEquals(20, poly.getCoordinates()[3].getOrdinate(Coordinate.Y), 0.001);
        assertEquals(20, poly.getCoordinates()[4].getOrdinate(Coordinate.X), 0.001);
        assertEquals(10, poly.getCoordinates()[4].getOrdinate(Coordinate.Y), 0.001);
    }
}

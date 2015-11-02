/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.geo;

import com.orange.cepheus.cep.Application;
import com.orange.ngsi.model.GeoPoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Created by pborscia on 02/11/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class GeometryTest {

    @Autowired
    Geometry geometry;

    @Test
    public void createNullCoordinate() {
        GeoPoint geoPoint = new GeoPoint(0.0,0.0);
        Coordinate coordinate = geometry.createCoordinate(geoPoint);
        assertEquals(0.0, coordinate.distance(geometry.createCoordinate(geoPoint)), 0.0);
    }

    @Test
    public void createCoordinate() {
        GeoPoint geoPoint = GeoPoint.parse("46.434,1.3434");
        Coordinate coordinate = geometry.createCoordinate(geoPoint);
        assertEquals(46.434, coordinate.getOrdinate(Coordinate.X), 0.001);
        assertEquals(1.3434, coordinate.getOrdinate(Coordinate.Y), 0.001);
    }

    @Test
    public void createNullPointTest() {
        GeoPoint geoPoint00 = new GeoPoint(0, 0);
        Point point0 = geometry.createPoint(geoPoint00);
        assertFalse(point0.isEmpty());
    }

    @Test
    public void createPointTest() {
        GeoPoint geoPoint = GeoPoint.parse("46.434,1.3434");
        Point point = geometry.createPoint(geoPoint);
        point.getGeometryType().compareTo("Point");
    }

    @Test
    public void createPolygonTest() {
        GeoPoint geoPoint1 = GeoPoint.parse("46.434,1.3434");
        GeoPoint geoPoint2 = GeoPoint.parse("56.434,1.3434");
        GeoPoint geoPoint3 = GeoPoint.parse("56.434,2.3434");
        GeoPoint geoPoint4 = GeoPoint.parse("46.434,2.3434");
        GeoPoint[] geoPoints= new GeoPoint[]{geoPoint1, geoPoint2, geoPoint3, geoPoint4, geoPoint1};
        Polygon polygon= geometry.createPolygon(geoPoints);
        assertTrue(polygon.isRectangle());
    }
}

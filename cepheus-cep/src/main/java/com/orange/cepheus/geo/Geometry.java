/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.geo;

import com.orange.ngsi.model.GeoPoint;
import com.vividsolutions.jts.geom.*;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.springframework.stereotype.Component;

/**
 * handle geometry objects with Geotools http://docs.geotools.org/latest/userguide/library/jts/geometry.html
 */
@Component
public class Geometry {

    private GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();

    /**
     * Create a Coordinate at the geotools format
     * @param geoPoint
     * @return a point
     */
    public Coordinate createCoordinate(GeoPoint geoPoint) {
        return new Coordinate(geoPoint.getLatitude(), geoPoint.getLongitude());
    }

    /**
     * Create a Point at the geotools format
     * @param geoPoint
     * @return a point
     */
    public Point createPoint(GeoPoint geoPoint) {
        return geometryFactory.createPoint(createCoordinate(geoPoint));
    }

    /**
     * Create a Polygon at the geotools format
     * @param geoPoints of points of the Polygon
     * @return a polygon
     */
    public Polygon createPolygon(GeoPoint[] geoPoints) {
        Coordinate[] coordinates  = new Coordinate[geoPoints.length];
        for (int i = 0; i < geoPoints.length; i++) {
            coordinates[i] = createCoordinate(geoPoints[i]);
        }
        LinearRing ring = geometryFactory.createLinearRing( coordinates );
        LinearRing holes[] = null; // use LinearRing[] to represent holes
        return geometryFactory.createPolygon(ring, holes );
    }
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.geo;

import com.espertech.esper.client.Configuration;
import com.vividsolutions.jts.geom.*;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;

/**
 * Expose geospatial features from JTS Geometry to Esper's engine.
 * http://docs.geotools.org/latest/userguide/library/jts/geometry.html
 */
public class Geospatial {

    private static GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
    private static WKTReader2 geometryReader = new WKTReader2(geometryFactory);

    /**
     * Inject in Esper Configuration geospatial methods and classes
     * @param configuration
     */
    public static void registerConfiguration(Configuration configuration) {

        // Inject methods to create Geometry variables
        configuration.addPlugInSingleRowFunction("polygon",  Geospatial.class.getName(), "createPolygon");
        configuration.addPlugInSingleRowFunction("point", Geospatial.class.getName(), "createPoint");
        configuration.addPlugInSingleRowFunction("geometry", Geospatial.class.getName(), "readGeometry");

        // Register Geometry class
        configuration.addImport(Geometry.class.getName());
    }

    /**
     * Create a Point at the geotools format
     * @param latitude
     * @param longitude
     * @return a point
     */
    public static Point createPoint(double longitude, double latitude) {
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    /**
     * Create a Polygon at the geotools format
     * @param points points of the Polygon
     * @return a polygon
     */
    public static Polygon createPolygon(Geometry[] points) {
        Coordinate[] coordinates = new Coordinate[points.length];
        for (int i = 0; i < points.length; i++) {
            coordinates[i] = points[i].getCoordinate();
        }
        return geometryFactory.createPolygon(coordinates);
    }

    /**
     * Read a WTK geometry description
     * @param wellKnownText WTK description
     * @return a Geometry
     * @throws ParseException
     */
    public static Geometry readGeometry(String wellKnownText) throws ParseException {
        return geometryReader.read(wellKnownText);
    }
}

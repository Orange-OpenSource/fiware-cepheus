package com.orange.cepheus.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

/**
 * Serialize and deserialize to an from NGSI geo:point string to a JST Point.
 */
public class GeoUtil {

    /**
     * Parse NGSI location coordinates (a two comma separated double numbers using point as decimal separator).
     * First number is latitude, second is longitude. Ex: "49.2323, 1.334".
     * @param coordinates the coordinates string to parse
     * @return a Geometry point
     * @throws IllegalArgumentException if the coordinates hava a bad format.
     */
    public static Geometry parseNGSIString(String coordinates) throws IllegalArgumentException {
        String[] coords = coordinates.split("\\s*,\\s*");
        if (coords.length != 2) {
            throw new IllegalArgumentException("bad coordinate format: "+coordinates);
        }
        try {
            double latitude = Double.parseDouble(coords[0]);
            double longitude = Double.parseDouble(coords[1]);
            return Geospatial.createPoint(latitude, longitude);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("bad number formatting: "+coordinates);
        }
    }

    /**
     * Transform a Geometry to NGSI location coordinates (two comma separated double numbers using point as decimal separator).
     * First number is latitude, second is longitude. Ex: "49.2323, 1.334".
     * @param geometry
     * @return
     */
    public static String toNGSIString(Geometry geometry) throws IllegalArgumentException {
        if (geometry instanceof Point) {
            Coordinate coordinate = geometry.getCoordinate();
            return coordinate.getOrdinate(Coordinate.Y) + ", " + coordinate.getOrdinate(Coordinate.X);
        }
        throw new IllegalArgumentException("cannot output geometry, only Point is supported");
    }
}

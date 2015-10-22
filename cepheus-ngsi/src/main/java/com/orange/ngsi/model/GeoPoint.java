/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

/**
 * A simple GeoPoint class that represent location coordinates
 */
public class GeoPoint {

    private double latitude;

    private double longitude;

    /**
     * Parse a two comma separated double numbers (using point as decimal separator).
     * First number is latitude, second is longitude. Ex: "49.2323, 1.334".
     * @param coordinates the coordinates string to parse
     * @return the GeoPoint
     * @throws IllegalArgumentException if the coordinates hava a bad format.
     */
    public static GeoPoint parse(String coordinates) throws IllegalArgumentException {
        String[] coords = coordinates.split("\\s*,\\s*");
        if (coords.length != 2) {
            throw new IllegalArgumentException("bad coordinate format: "+coordinates);
        }
        try {
            double latitude = Double.parseDouble(coords[0]);
            double longitude = Double.parseDouble(coords[1]);
            return new GeoPoint(latitude, longitude);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("bad number formatting: "+coordinates);
        }
    }

    public GeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String toNGSIString() {
        return latitude + ", " + longitude;
    }

    @Override
    public String toString() {
        return "GeoPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}

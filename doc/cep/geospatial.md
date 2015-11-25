# Geospatial types

The EPL language has been extended to support additional Geospatial features.
All these geospacial features use the WGS84 reference. The types and methods come from the Geotools framework.

## Types

### `Geometry` type

This is the base type of all the other geometry shapes.

### `Point` type

Current NGSI v1 protocol only support defining a single location, the `geo:point` attribute type, that is internally converted to a Geotools `Point` class.

### `Polygon` type

For handling geofencing features, the CEP also handles the `Polygon` type which is defined as a list of `Point` elements defining a closed shape (with identical first and last `Point`).

The `Polygon` type cannot be converted to and from the NGSI protocol (yet). It can only be defined from the EPL statements (using `create variable` statements) during the configuration of the CEP.

## Constructors

### `Point point(x, y)`

This method initializes a new Point with two coordinates.

Example:

    CREATE VARIABLE Geometry centerPoint = point(4.2, 46.2)

### `Polygon poly([Point])`

This methods takes an array of Point and intializes a Polygon. The first and last point must be identical to close the shape.

Example:

    CREATE VARIABLE Geometry fence = polygon({point(0, 0), point(0,50), point(50,50), point(50, 0), point(0, 0)})

Note: the syntax to declare an array of element in EPL is `{ elem1, elem2, ...}`.

### `Geometry read(string)`

The `read` method allows to create any `Geometry` supported by Geotools using the WKT format.

Example:

    CREATE VARIABLE Geometry fence2 = geometry("POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))")

## `Geometry` methods

Several methods apply to the `Geometry` type (and therefore to `Point`and `Polygon` subtypes).

### `double distance(Geometry)`

Compute the distance (expressed in meter) between two `Geometry` shapes.

Example: Let's track all sensor's location witch are less than 20m far from a `center` location over the last 10 min:

    CREATE VARIABLE Geometry center = point(4.2, 46.2)
    INSERT INTO NearbySensors SELECT id, location.distance(center) as distance FROM Sensors.win:time(10 min) WHERE location.distance(center) < 20

The `location` method can also be used to compute the distance of a `Point` to a `Polygon`.

### `boolean contains(Geometry)`

Return true if the `Geometry` is contained inside another one.

Example: Let's track when a sensor location is inside a given geofence.

    CREATE VARIABLE Geometry fence = polygon({point(0, 0), point(0,50), point(50,50), point(50, 0), point(0, 0)})
    INSERT INTO InsideFence SELECT * FROM Sensors WHERE fence.contains(location)



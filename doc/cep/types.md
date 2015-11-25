# Supported types

The CEP can handle several special types that can be processed in EPL.
These types can be used both in attributes and metadata.

## Primitive types

The primitive types are : int, float, double.

<table>
<tr><th>Type</th><th>Format</th><th>Examples</th></tr>
<tr><td>int</td><td>integer</td><td>1232</td></tr>
<tr><td>float</td><td>float</td><td>1.232</td></tr>
<tr><td>double</td><td>double</td><td>123232.232332</td></tr>
</table>

## Date type

The `date` type identifies dates in the [ISO8601](https://en.wikipedia.org/wiki/ISO_8601) format.

Example:

```
  {
    "name" : "timestamp"
    "value": "2017-06-17T07:21:24.238Z",
    "type: "date"
  }
```

See the Date-Time section of the Esper EPL documentation for all date and time methods available
to filter or manipulate this type from the EPL language.

## Geo:point type

The `geo:point` identifies locations using two numbers separated by a comma
using the [WGS84 reference](https://en.wikipedia.org/wiki/World_Geodetic_System#WGS84).

Example:

```
  "location": {
    "value": "41.3763726, 2.1864475,14",
    "type": "geo:point"
  }
```

The `geo:point` type is converted internally to a Point which support Geospatial operations (see (Geospatial types)[geospatial.md]).

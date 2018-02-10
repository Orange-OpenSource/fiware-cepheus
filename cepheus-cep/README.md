# Cepheus-CEP

Cepheus-CEP is a CEP (Complex Event Processor), it uses the [Esper](http://www.espertech.com/esper/) engine.
This engine provides a lot of features (aggregation, filters, rate limiting, correlation, partitionning...) with a nice [SQL like syntax](http://esper.espertech.com/release-5.2.0/esper-reference/html/epl_clauses.html).

Goal:

* process basic Context Entities from NGSI sensors
* generate higher levels of abstraction (Room, Floor, Building)
* at the gateway level (runs on Raspberry Pi)

## Administrator guide

The administrator guide, with complete instruction for building, installing and running the application, can be found [here](../doc/admin/cep.md)

## User guide

The user & programming manual can be found [here](../doc/cep/README.md)

## License

Cepheus-CEP is licensed under the [GNU General Public License Version 2](../LICENCE.txt).

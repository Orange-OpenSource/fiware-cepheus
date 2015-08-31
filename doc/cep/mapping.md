# Mapping Context Entities to Events

The CEP engine works by processing incoming events and generating outgoing events.
These concepts can be mapped quite easily to updates of Context Entities in the NGSI data model.

When the CEP engine is addressed an update to a Context Entity using the NGSI 10 /updateContext request,
it will fire the corresponding events and process the related EPL statements.

If one or more outgoing events are fired by the CEP engine, the corresponding /updateContext requests
will be called to notify Context Brokers of the updates to the mapped Context Entities.

## Restrictions

The Esper CEP engine imposes several restrictions to the Context Entities.

### Type is mandatory

All events must have a given type (an Event type) that define a strict set of typed attributes.
Event types must be declared before any event can be processed by the CEP engine.

Based on this strong requirement, the fiware-cepheus implementation **requires** the updates made by
the Context Providers to provide a mandatory `type` field for all Context Entities.
Updates to Context Elements missing this type information will be discarded.

### Id is a reserved attribute key

Another consequence is that the `id` field is handled by CEP engine as just another attribute of the event.
Therefore no Context Attribute is allowed to be named `id`.

### Simple attribute types

The current version of the CEP engine only handle simple attribute types (like string, int, float, ...)
and cannot execute statements for complex types like objects or arrays because of the strongly typed nature of the CEP engine.

Note: A later version might support complex values by flattening values using [JSON path](http://goessner.net/articles/JsonPath/) expressions.

### Accessing attributes metadata

An attribute metadata can be access from the CEP rules by joining the key of the attribute
and the key of the metadata separated by an underscore character: `Attribute Key` _ `Metadata key`.

Example: `temperature_unit` will give access to the `unit` metdata of the `temperature` attribute.

Note: the same type limitations (previous section) of attributes apply to metadata types.

## A more visual mapping

It is possible to visualize a stream of events as a SQL database table.
For each type, we define a table with columns for the `id` and each attribute. Each event is represented by a single row.

For example, the following Context Entities :

```
  {
    "id": "SENSOR1",
    "type":"RoomSensors",
    "attributes": [
      { "name": "T°", "type": "float", "value": "21" },
      { "name": "Pressure", "type": "integer", "value": "560" }
    ]
  },
  {
    "id": "SENSOR2",
    "type":"RoomSensors",
    "attribtues": [
      { "name": "T°", "type": "float", "value": "30" },
      { "name": "Pressure", "type": "integer", "value": "1342" }
    ]
  }
```

can be seen as this table (or event stream) named "RoomSensors":

<table>
<tr><th>ID</th><th>T°</th><th>Pressure</th></tr>
<tr><td>SENSOR1</td><td>21</td><td>560</td></tr>
<tr><td>SENSOR2</td><td>30</td><td>1342</td></tr>
<tr><td>...</td><td>...</td><td>...</td></tr>
</table>

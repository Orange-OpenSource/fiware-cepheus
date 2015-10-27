This example shows the use of Date and Geo:Point special attributes within Cepheus-CEP.

It is completely based on the [Rooms and Floors example](../1_RoomsAndFoors/index.md).

It illustrates how to define `date` and `geo:point` attributes and metadata.

## Setup

Let us suppose the sensors add a `location` metadata to send their position and
an `time` metadata on the `temperature` attribute:

     {
         "id": "Room31", // Room 1 on floor 3 (could be anything else)
         "type":"Room",  // all sensors must use the same "Room" type
         "attributes": [
            {
                "name":"temperature",
                "type":"double",
                "value":"21",
                "metadatas": [
                    { "name":"time", "type":"date", "value":"2015-10-26T22:47:09Z" },
                    { "name":"location", "type":"geo:point", "value":"45.2334, 1.4233" }
                ]
            },
            { "name":"floor", "type":"string", "value":"3" }, // the room is on the third foor
         ]
     }


## Configuring the CEP

Translated to the configuration format of the Cepheus-CEP, we get the following "in" section for accepting Room temperature as input :

    "in": [
        {
            "id":"Room1",
            "type":"Room",
            "attributes":[
                {
                    "name":"temperature",
                    "type":"double",
                    "metadata": [
                      { "name":"time", "type":"date" },
                      { "name":"location", "type":"geo:point"}
                    ]
                },
                { "name":"floor", "type":"string" }
            ]
        }
    ]

The "out" section is also similar to the NGSI Context Entity of a Floor:

    "out":[
        {
            "id":"Floor1",
            "type":"Floor",
            "attributes":[
                {
                    "name":"temperature",
                    "type":"double",
                    "metadata": [
                      { "name":"time", "type":"date" },
                      { "name":"location", "type":"geo:point"}
                    ]
                }
            ]
        }
    ]

To transmit the `time` and `location` metadata of the `temperature`, we need to
tell the EPL rule to transmit `temperature_time` and `temperature_location` "as is" :

    INSERT INTO Floor
    SELECT floor as id, avg(temperature) as temperature, temperature_time, temperature_location
    FROM Room.win:time(10 minutes)
    GROUP BY floor
    OUTPUT LAST EVERY 1 min

See more information about metadata naming in EPL in the [CEP/Mapping](../../cep/mapping.md) section.

The [config.json](config.json) has the complete configuration setup.

## Testing the setup

You can run the [run.sh](run.sh) file in a terminal while checking the logs of Cepheus CEP
to see the Rooms temperature sent to the CEP and the CEP reacting to the events.

In a first terminal, launch Cepheus-CEP:

    cd cepheus-cep
    mvn spring-boot:run

Default configuration should launch it on port :8080 on your machine.

Now in another terminal, trigger the [run.sh](run.sh) script:

    cd doc/examples/8_DateAndGeoPoint
    sh run.sh

The script first sends the [config.json](config.json) file to Cepheus-CEP, then it starts
sending temperatures updates.

Go back to the terminal where you launched the CEP. You should see temperatures as "EventIn" being logged.

After a few seconds, the "EventOut" logs will show the CEP triggering the average temperature for each floor.


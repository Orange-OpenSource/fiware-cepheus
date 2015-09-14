
This example shows a more complex event processing.

Let us consider we have:
 - a NGSI-capable temperature sensor in each room
 - a NGSI-controlled shutter in each room which open and close to cool or heat the room.

We would like to close the shutters when there is too much sunlight heating the room.
The system should close the shutters when the room temperature is over > 26°C
and then open back the shutters when temperature drops under 24°C.

## Setup

We configure our NGSI compatible sensors to output their values as the following Context Entity:

     {
         "id": "Room1",  # Room 1 (could be anything else)
         "type":"Room",  # all sensors must use the same "Room" type
         "attributes": [
            { "name":"temperature", "type":"double", "value":"21" },  # this is the value of the sensors
            { "name":"shutter", "type":"string", "value":"shutter1" } # the shutter associted to the room
         ]
     }

We can configure the CEP to accept these updates and then trigger the update of
a Context Entity that would be based on the shutters :

    {
        "id": "Shutter1", # uniquely identifies a shutter (coud be anything else)
        "type":"Shutter", # all shutters must use the same "Shutter" type
        "attributes": [
            { "name":"status", "type":"string", "value":"closed" }, # this will trigger the close command
        ]
    }

The `status` attributes can be `opened` or `closed`.

The EPL rules to trigger the shutter commands based on the previous model are :

    INSERT INTO Shutter
    SELECT R.r.shutter as id, 'closed' as status
    FROM pattern [ every r=Room(temperature > 26.0) -> (timer:interval(10 sec) and not Room(temperature < 26.0 and id=r.id))] as R unidirectional
    LEFT OUTER JOIN Shutter.std:groupwin(id).std:lastevent() as S
    ON R.r.shutter = S.id
    WHERE S is null OR S.status = 'opened'

and

    INSERT INTO Shutter
    SELECT R.r.shutter as id, 'opened' as status
    FROM pattern [ every r=Room(temperature < 24.0) -> (timer:interval(10 sec) and not Room(temperature > 24.0 and id=r.id))] as R unidirectional
    LEFT OUTER JOIN Shutter.std:groupwin(id).std:lastevent() as S
    ON R.r.shutter = S.id
    WHERE S is null OR S.status = 'closed'

Please refer to Esper EPL manual for details about the syntax of these rules.

We are using the `shutter` attribute of the Room as the `id` of the Shutter.
The `status` is set to `closed` or `opened` for Shutter if the pattern is matched.
The patterns trigger when the temperature keeps above 26 or under 24 uninterrupted for 10 secs.
The left outer join on the Shutter window allows to only trigger the command if the current status is different (unknown or opposite).
The unidirectional instruction restrict to evaluate the join only when the pattern is matched (not when the Shutter window is updated).

Note: an optimization could be to use a "named window" to prevent having two filtered Shutter windows `Shutter.std:groupwin(id).std:lastevent()`.
This is left as an exercise to the reader.

## Configuring the CEP

Translated to the configuration format of the Cepheus-CEP, we get the folowing "in" section for accepting Room temperature as input :

    "in": [
        {
            "id":"Room.*",     # Pattern is used to subscribe to provider to all Room1, Room2, ..., RoomN
            "type":"Room",     # The type to subscribe
            "isPattern":true,  # Pattern match the id
            "attributes":[
                { "name":"temperature", "type":"double" },
                { "name":"shutter", "type":"string" }      # Id of the Shutter associated to the Room
            ]
        }
    ]

The "out" section is also similar to the NGSI Context Entity of a Shutter:

    "out":[
        {
            "id":"ShutterX",
            "type":"Shutter",
            "attributes":[
                { "name":"status", "type":"string" }
            ]
        }
    ]

The [config.json](config.json) has the complete configuration setup.

## Testing the setup

You can run the [run.sh](run.sh) file in a terminal while checking the logs of Cepheus CEP
to see the Rooms temperature sent to the CEP and the CEP reacting to the events.

In a first terminal, launch Cepheus-CEP:

    cd cepheus-cep
    mvn spring-boot:run

Default configuration should launch it on port :8080 on your machine.

Now in another terminal, trigger the [run.sh](run.sh) script:

    cd doc/examples/2_CloseShutters
    sh run.sh

The script first sends the [config.json](config.json) file to Cepheus-CEP, then it starts
sending temperatures updates.

Go back to the first terminal where you launched the CEP. You should see temperatures as "EventIn" beeing logged.

After a few seconds, the "EventOut" logs will show the CEP triggering the status for each shutter.

## Next step

You can now learn how to make Cepheus-CEP work with Cepheus-Broker in the next example.

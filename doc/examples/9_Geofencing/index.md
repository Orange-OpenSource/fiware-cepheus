This example shows the use Geo:Point special attributes within Cepheus-CEP.

Let's demonstrate a Geofencing alert that is triggered when a Tracker
enters or exists a geofenced zone.

## Setup

Let us suppose the trackers have `location` and `time` attributes:
They will send `updateContext` requests of this type:

     {
         "id": "Tracker1",
         "type":"Tracker",
         "attributes": [
            { "name":"time", "type":"date", "value":"2015-10-26T22:47:09Z" },
            { "name":"location", "type":"geo:point", "value":"45.2334, 1.4233" }
         ]
     }

The goal is to have the CEP trigger an `updateContext` request like this one:


     {
         "id": "Tracker1", // Id of the tracker that triggered the alert
         "type":"Alert",
         "attributes": [
            { "name":"time", "type":"date", "value":"2015-10-26T22:47:09Z" },
            { "name":"location", "type":"geo:point", "value":"45.2334, 1.4233" }
            { "name":"inside", "type":"boolean", "value":"true" }
         ]
     }

each time a tracker enters or exit the geofenced zone.


## Configuring the CEP

Translated to the configuration format of the Cepheus-CEP, we get the following "in" section for accepting Tracker as input :

   "in":[
       {
         "id":"Tracker1",
         "type":"Tracker",
         "attributes":[
           { "name":"time", "type":"date" },
           { "name":"location", "type":"geo:point" }
         ]
       }
    ]


The "out" section is also similar to the NGSI Context Entity of the given Alert definition:

    "out":[
           {
             "id":"Fence1",
             "type":"Alert",
             "attributes":[
               { "name":"time", "type":"date" },
               { "name":"location", "type":"geo:point" },
               { "name":"inside", "type":"boolean" }
             ]
           }
    ]


We first need to define the fence (our geofenced zone) using a polygon:

    CREATE VARIABLE Geometry fence = polygon({point(0, 0), point(0,50), point(50,50), point(50, 0), point(0, 0)})

Then we create a window that will keep the state of each Tracker modelled after the event type `Alert`:

    CREATE WINDOW TrackerState.std:unique(id) as Alert

We then define a new Event Type to compute if the tracker is in the fence each time a tracker updates its location:

    INSERT INTO TrackerInside SELECT *, fence.contains(location) as inside FROM Tracker

Then we define an FenceCross event when a tracker is detected in (or out) the zone and that no other opposite update occured during a given period:

    INSERT INTO FenceCross SELECT a.* FROM pattern [ every a=TrackerInside -> (timer:interval(4 sec) and not TrackerInside(id=a.id, inside!=a.inside)) ]

This allows us to handle cases where a tracker can stay on the edge for a given time.

Then the TrackerState is updated only if the state is different (or that not state is yet defined):

    ON FenceCross fc MERGE TrackerState ts
    WHERE fc.id = ts.id
       WHEN NOT MATCHED THEN INSERT SELECT id, time, location, inside
       WHEN MATCHED AND fc.inside != ts.inside THEN UPDATE SET inside = fc.inside

Finaly, all events of the TrackerState window are sent as Alert:

    INSERT INTO Alert SELECT * FROM TrackerState"

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
sending location updates.

Go back to the terminal where you launched the CEP. You should see location as "EventIn" being logged
and some `Alert` "EventOut" beeing fired 3 times:

- the initial alert where the tracker is defined as outside of the zone,
- the next alert where the tracker is define as inside the zone,
- a final alert where the tracker exists the zone.

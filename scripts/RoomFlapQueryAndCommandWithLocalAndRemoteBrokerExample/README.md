# Rooms Flaps Commands example

Lets consider we have :
 - a set of temperature sensors accross multiple rooms
 - a set of flap sensors which control opening and closing windows shutters
 - multiple rooms over a set of Floors

We would like to query the temperature of a particular room or all rooms
We would like to query the status of a particular window shutter
We would like to update the opening or closing to window shutter

## Setup
No setup.

## Testing the setup

You can run the [run.sh](run.sh) file in a terminal while checking the logs of Cepheus CEP
to see the Rooms temperature sent to the CEP and the CEP reacting to the events.

In a first terminal, launch mock-orion (default on port :8082 on your machine)
    cd scripts
    java -jar mock-orion-1.0-SNAPSHOT.jar

In a second terminal, launch Cepheus-lb:

    cd cepheus-lb
    mvn spring-boot:run

Default configuration should launch it on port :8081 on your machine.

Then in a third terminal, launch mock-iotagent:

    cd scripts
    java -jar mock-iotagent-1.0-SNAPSHOT.jar

Default configuration should launch it on port :8083 on your machine.

Now in another terminal, trigger the [run.sh](run.sh) script:

    cd scripts/RoomFlapQueryAndCommandWithLocalAndRemoteBrokerExample
    sh run.sh

The script first sends the [config.json](config.json) file to Cepheus-CEP.
The mock-iotagent send the register requests for Room and Flap entities to cepheus-broker. Cepheus-broker forward the register to mock-orion.
The mock-orion request the temperature of all rooms to cepheus-broker. Cepheus-broker forward the query request to mock-iotagent.
The mock-iotagent send query response to Cepheus-broker. Cepheus-broker forward the response to mock-orion which print the values.

The same for flap status.

The mock-orion send the update of the status of flap for flap21 to cepheus-broker. Cepheus-broker forward the update request to mock-iotagent.
The mock-iotagent respond ok to Cepheus-broker. Cepheus-broker forward the response to mock-orion which print the response.



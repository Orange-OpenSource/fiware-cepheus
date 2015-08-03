package com.orange.cepheus;

import com.orange.cepheus.model.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Helpers for tests
 */
public class Util {

    static public Configuration getBasicConf() {
        Configuration configuration = new Configuration();
        try {
            configuration.setHost(new URI("http://localhost:8080"));
        } catch (URISyntaxException e) {
        }

        // eventIN 1
        EventTypeIn eventTypeIn = new EventTypeIn("S.*", "TempSensor", true);
        eventTypeIn.addProvider("http://iotAgent");
        Attribute attr = new Attribute("temp", "double");
        attr.setMetadata(Collections.singleton(new Metadata("unit", "string")));
        eventTypeIn.addAttribute(attr);
        configuration.setEventTypeIns(Collections.singletonList(eventTypeIn));

        //eventOUT
        EventTypeOut eventTypeOut = new EventTypeOut("OUT1", "TempSensorAvg", false);
        eventTypeOut.addBroker(new Broker("http://orion", false));
        Attribute outAttribute = new Attribute("avgTemp", "double");
        outAttribute.setMetadata(Collections.singleton(new Metadata("unit", "string")));
        eventTypeOut.addAttribute(outAttribute);
        configuration.setEventTypeOuts(Collections.singletonList(eventTypeOut));

        //rules
        List<String> rules = new ArrayList<>();
        rules.add("INSERT INTO TempSensorAvg SELECT 'OUT1' as id, avg(temp) as avgTemp, temp_unit as avgTemp_unit FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1' ");
        configuration.setStatements(rules);

        return configuration;
    }

    static public Event buildBasicEvent(Object value) {
        Event e = new Event("TempSensor");
        e.addValue("id", "S1");
        e.addValue("temp", value);
        e.addValue("temp_unit", "celcius");
        return e;
    }
}

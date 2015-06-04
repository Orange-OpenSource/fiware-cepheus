package com.orange.newespr4fastdata.cep;

import com.orange.newespr4fastdata.Application;
import com.orange.newespr4fastdata.exception.EventTypeNotFoundException;
import com.orange.newespr4fastdata.model.cep.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by pborscia on 03/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ComplexEventProcessingTest {


    private ComplexEventProcessing complexEventProcessing = new ComplexEventProcessing();

    @Test
    public void reInitBasicConfOK(){

        complexEventProcessing.reInitConf(getBasicConf());


        try {
            assertEquals(1,complexEventProcessing.getEventTypeAttributes("TempSensor").size());
            assertEquals("temp",complexEventProcessing.getEventTypeAttributes("TempSensor").get(0).getName());
            assertEquals("float",complexEventProcessing.getEventTypeAttributes("TempSensor").get(0).getType());

            assertEquals(1,complexEventProcessing.getEventTypeAttributes("TempSensorAvg").size());
            assertEquals("avgTemp",complexEventProcessing.getEventTypeAttributes("TempSensorAvg").get(0).getName());
            assertEquals("double",complexEventProcessing.getEventTypeAttributes("TempSensorAvg").get(0).getType());

        } catch (EventTypeNotFoundException e) {
            Assert.fail("Not expected EventTypeNotFoundException");
        }

        this.sendXtemperature();


    }


    private Conf getBasicConf(){
        Conf conf = new Conf();
        conf.setHost("http://localhost:8080");
        //eventIN
        List<EventTypeIn> eventTypeIns = new ArrayList<EventTypeIn>();
        conf.setEventTypeIns(eventTypeIns);
        EventTypeIn eventTypeIn = new EventTypeIn();
        eventTypeIns.add(eventTypeIn);
        eventTypeIn.setProvider("http://iotAgent");
        eventTypeIn.setId("S.*");
        eventTypeIn.setType("TempSensor");
        eventTypeIn.setIsPattern(true);
        List<Attribute> attributes = new ArrayList<Attribute>();
        Attribute attributeTemp = new Attribute();
        attributeTemp.setName("temp");
        attributeTemp.setType("float");
        attributes.add(attributeTemp);
        eventTypeIn.setAttributes(attributes);
        //eventOUT
        List<EventTypeOut> eventTypeOuts = new ArrayList<EventTypeOut>();
        conf.setEventTypeOuts(eventTypeOuts);
        EventTypeOut eventTypeOut = new EventTypeOut();
        eventTypeOuts.add(eventTypeOut);
        eventTypeOut.setBroker("http://orion");
        eventTypeOut.setId("OUT1");
        eventTypeOut.setType("TempSensorAvg");
        eventTypeOut.setIsPattern(false);
        List<Attribute> outAttributes = new ArrayList<Attribute>();
        Attribute attributeAvgTemp = new Attribute();
        attributeAvgTemp.setName("avgTemp");
        attributeAvgTemp.setType("double");
        outAttributes.add(attributeAvgTemp);
        eventTypeOut.setAttributes(outAttributes);

        //rules
        List<String> rules = new ArrayList<String>();
        rules.add("INSERT INTO TempSensorAvg SELECT 'OUT1' as id, avg(TempSensor.temp) as avgTemp FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1' ");
        conf.setRules(rules);

        return conf;
    }

    private void sendXtemperature() {

        Random random = new Random(15);

        for (int i=1; i<100 ; i++) {

            EventIn eventIn = new EventIn();
            HashMap<String, Object> attributesMap = new HashMap<String, Object>();
            float value = (float) (15.5 + random.nextFloat());
            attributesMap.put("id","S1");
            attributesMap.put("temp",value);
            eventIn.setAttributesMap(attributesMap);
            eventIn.setEventTypeName("TempSensor");

            complexEventProcessing.sendEventInEsper(eventIn);

        }


    }


}

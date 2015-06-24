package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.exception.EventTypeNotFoundException;
import com.orange.espr4fastdata.model.cep.*;
import com.orange.espr4fastdata.util.Util;
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

    private Util util = new Util();

    @Test
    public void reInitBasicConfOK(){

        complexEventProcessing.setConfiguration(util.getBasicConf());


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

            complexEventProcessing.processEvent(eventIn);

        }


    }


}

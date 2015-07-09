/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.exception.EventTypeNotFoundException;
import com.orange.espr4fastdata.model.Event;
import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import com.orange.espr4fastdata.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by pborscia on 03/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class EsperEventProcessorTest {


    @Autowired
    private EsperEventProcessor esperEventProcessor;

    private Util util = new Util();

    @Test
    public void reInitBasicConfOK(){


            esperEventProcessor.setConfiguration(util.getBasicConf());



        try {
            assertEquals(1, esperEventProcessor.getEventTypeAttributes("TempSensor").size());
            assertEquals("temp", esperEventProcessor.getEventTypeAttributes("TempSensor").get(0).getName());
            assertEquals("float", esperEventProcessor.getEventTypeAttributes("TempSensor").get(0).getType());

            assertEquals(1, esperEventProcessor.getEventTypeAttributes("TempSensorAvg").size());
            assertEquals("avgTemp", esperEventProcessor.getEventTypeAttributes("TempSensorAvg").get(0).getName());
            assertEquals("double", esperEventProcessor.getEventTypeAttributes("TempSensorAvg").get(0).getType());

        } catch (EventTypeNotFoundException e) {
            Assert.fail("Not expected EventTypeNotFoundException");
        }

        this.sendXtemperature();


    }

    @Test
    public void typeExistsInConfigurationTest(){

        esperEventProcessor.setConfiguration(util.getBasicConf());

        Assert.assertTrue(esperEventProcessor.typeExistsInConfiguration("TempSensor"));
    }

    @Test
    public void typeNotExistsInConfigurationTest(){

        esperEventProcessor.setConfiguration(util.getBasicConf());

        Assert.assertFalse(esperEventProcessor.typeExistsInConfiguration("PressureSensor"));
    }


    private void sendXtemperature() {

        Random random = new Random(15);

        for (int i=1; i<100 ; i++) {

            Event event = new Event();
            HashMap<String, Object> attributesMap = new HashMap<String, Object>();
            float value = (float) (15.5 + random.nextFloat());
            attributesMap.put("id","S1");
            attributesMap.put("temp",value);
            event.setAttributes(attributesMap);
            event.setType("TempSensor");


            try {
                esperEventProcessor.processEvent(event);
            } catch (EventProcessingException e) {
                Assert.fail("Not expected EventProcessingException");
            }


        }


    }


}

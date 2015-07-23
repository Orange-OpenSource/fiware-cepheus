/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.exception.EventTypeNotFoundException;
import com.orange.espr4fastdata.model.cep.Attribute;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.util.Util;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.*;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;

/**
 * Test for the Esper complex event processor
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class EsperEventProcessorTest {

    @Mock
    EventSinkListener eventSinkListener;

    @Autowired
    @InjectMocks
    private EsperEventProcessor esperEventProcessor;

    private Util util = new Util();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkBasicConf() throws ConfigurationException, EventTypeNotFoundException {
        esperEventProcessor.setConfiguration(util.getBasicConf());

        esperEventProcessor.setConfiguration(util.getBasicConf());

        assertEquals(1, esperEventProcessor.getEventTypeAttributes("TempSensor").size());
        assertEquals("temp", esperEventProcessor.getEventTypeAttributes("TempSensor").get(0).getName());
        assertEquals("float", esperEventProcessor.getEventTypeAttributes("TempSensor").get(0).getType());

        assertEquals(1, esperEventProcessor.getEventTypeAttributes("TempSensorAvg").size());
        assertEquals("avgTemp", esperEventProcessor.getEventTypeAttributes("TempSensorAvg").get(0).getName());
        assertEquals("double", esperEventProcessor.getEventTypeAttributes("TempSensorAvg").get(0).getType());
    }

    /**
     * Check that a Configuration exception is thrown when an unknown property 'BAD' is added to a statement
     * @throws ConfigurationException
     */
    @Test(expected = ConfigurationException.class)
    public void checkUndefinedProperty() throws ConfigurationException {
        Configuration configuration = util.getBasicConf();
        configuration.getStatements().set(0,
                "INSERT INTO TempSensorAvg SELECT BAD, avg(TempSensor.temp) as avgTemp FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1'");

        esperEventProcessor.setConfiguration(configuration);
    }


    /**
     * Check that a Configuration exception is thrown when an unknown event type 'BAD' is referenced in a statement
     * @throws ConfigurationException
     */
    @Test(expected = ConfigurationException.class)
    public void checkMissingEventIn() throws ConfigurationException {
        Configuration configuration = util.getBasicConf();
        configuration.getStatements().set(0,
                "INSERT INTO TempSensorAvg SELECT avg(BAD.temp) as avgTemp FROM BAD.win:time(2 seconds) WHERE BAD.id = 'S1'");

        esperEventProcessor.setConfiguration(configuration);
    }

    /**
     * Check that when a empty configuration is submitted, event types of previous configuration were removed
     * @throws ConfigurationException
     */
    @Test
    public void checkEventTypeRemoval() throws ConfigurationException {
        esperEventProcessor.setConfiguration(util.getBasicConf());

        Configuration emptyConfiguration = new Configuration();
        emptyConfiguration.setEventTypeIns(Collections.emptyList());
        emptyConfiguration.setEventTypeOuts(Collections.emptyList());
        emptyConfiguration.setStatements(Collections.emptyList());
        esperEventProcessor.setConfiguration(emptyConfiguration);

        try {
            esperEventProcessor.getEventTypeAttributes("TempSensor");
            fail("TempSensor event type should be removed !");
        } catch (EventTypeNotFoundException e) {
            // ok
        }

        try {
            esperEventProcessor.getEventTypeAttributes("TempSensorAvg");
            fail("TempSensorAvg event type should be removed !");
        } catch (EventTypeNotFoundException e) {
            // ok
        }
    }

    /**
     * Check that when a bad configuration is submitted, previous configuration is reset
     * @throws ConfigurationException
     * @throws EventTypeNotFoundException
     */
    @Test
    public void checkPreviousConfigurationRestoration() throws ConfigurationException, EventTypeNotFoundException {
        Configuration configuration = util.getBasicConf();
        esperEventProcessor.setConfiguration(configuration);

        Configuration badConfiguration = new Configuration();
        badConfiguration.setEventTypeIns(Collections.emptyList());
        badConfiguration.setEventTypeOuts(Collections.emptyList());
        badConfiguration.setStatements(Collections.singletonList("BAD STATEMENT"));

        try {
            esperEventProcessor.setConfiguration(badConfiguration);
            fail("bad configuration should throw ConfigurationException");
        } catch (ConfigurationException e) {
            // ok
        }

        List<Attribute> attributes = esperEventProcessor.getEventTypeAttributes("TempSensor");
        assertEquals(1, attributes.size());
        assertEquals("temp", attributes.get(0).getName());
        assertEquals("float", attributes.get(0).getType());

        attributes = esperEventProcessor.getEventTypeAttributes("TempSensorAvg");
        assertEquals(1, attributes.size());
        assertEquals("avgTemp", attributes.get(0).getName());
        assertEquals("double", attributes.get(0).getType());

        assertEquals(1, esperEventProcessor.getStatements().size());
        assertEquals(configuration.getStatements().get(0), esperEventProcessor.getStatements().get(0));
    }


    /**
     * Check that the update listener is called on when an event is generated by the Esper CEP engine
     * @throws ConfigurationException
     * @throws EventProcessingException
     */
    @Test
    public void checkUpdateListenerUpdated() throws ConfigurationException, EventProcessingException {

        esperEventProcessor.setConfiguration(util.getBasicConf());

        esperEventProcessor.processEvent(util.buildBasicEvent(5.0));

        ArgumentCaptor<EventBean[]> eventsArg = ArgumentCaptor.forClass(EventBean[].class);
        verify(eventSinkListener).update(eventsArg.capture(), eq(null), any(EPStatement.class), any(EPServiceProvider.class));

        EventBean[] events = eventsArg.getValue();
        assertEquals(1, events.length);
        assertEquals(events[0].get("id"), "OUT1");
        assertEquals(5.0, events[0].get("avgTemp"));
    }

    private void sendXtemperature() {
        Random random = new Random(15);
        for (int i=1; i<100 ; i++) {
            try {
                esperEventProcessor.processEvent(util.buildBasicEvent((float) (15.5 + random.nextFloat())));
            } catch (EventProcessingException e) {
                Assert.fail("Not expected EventProcessingException");
            }
        }
    }
}

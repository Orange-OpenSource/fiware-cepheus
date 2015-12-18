/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.EventProcessingException;
import com.orange.cepheus.cep.exception.EventTypeNotFoundException;
import com.orange.cepheus.cep.model.Attribute;
import com.orange.cepheus.cep.model.Configuration;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static com.orange.cepheus.cep.Util.*;


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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void resetEmptyConfiguration() throws ConfigurationException {
        Configuration emptyConfiguration = new Configuration();
        emptyConfiguration.setEventTypeIns(Collections.emptyList());
        emptyConfiguration.setEventTypeOuts(Collections.emptyList());
        emptyConfiguration.setStatements(Collections.emptyList());
        esperEventProcessor.setConfiguration(emptyConfiguration);
    }

    @Test
    public void checkBasicConf() throws ConfigurationException, EventTypeNotFoundException {
        esperEventProcessor.setConfiguration(getBasicConf());

        esperEventProcessor.setConfiguration(getBasicConf());

        assertEquals(2, esperEventProcessor.getEventTypeAttributes("TempSensor").size());
        assertEquals("double", esperEventProcessor.getEventTypeAttributes("TempSensor").get("temp").getType());
        assertEquals("string", esperEventProcessor.getEventTypeAttributes("TempSensor").get("temp_unit").getType());

        assertEquals(2, esperEventProcessor.getEventTypeAttributes("TempSensorAvg").size());
        assertEquals("double", esperEventProcessor.getEventTypeAttributes("TempSensorAvg").get("avgTemp").getType());
        assertEquals("string", esperEventProcessor.getEventTypeAttributes("TempSensorAvg").get("avgTemp_unit").getType());
    }

    /**
     * Check that a TestConfiguration exception is thrown when an unknown property 'BAD' is added to a statement
     * @throws ConfigurationException
     */
    @Test(expected = ConfigurationException.class)
    public void checkUndefinedProperty() throws ConfigurationException {
        Configuration configuration = getBasicConf();
        configuration.getStatements().set(0,
                "INSERT INTO TempSensorAvg SELECT BAD, avg(TempSensor.temp) as avgTemp FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1'");

        esperEventProcessor.setConfiguration(configuration);
    }

    /**
     * Check that a TestConfiguration exception is thrown when an unknown event type 'BAD' is referenced in a statement
     * @throws ConfigurationException
     */
    @Test(expected = ConfigurationException.class)
    public void checkMissingEventIn() throws ConfigurationException {
        Configuration configuration = getBasicConf();
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
        esperEventProcessor.setConfiguration(getBasicConf());

        resetEmptyConfiguration();

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

        assertEquals(0, esperEventProcessor.getStatements().size());
    }

    /**
     * Check that when a bad configuration is submitted, previous configuration is reset
     * @throws ConfigurationException
     * @throws EventTypeNotFoundException
     */
    @Test
    public void checkPreviousConfigurationRestoration() throws ConfigurationException, EventTypeNotFoundException {
        Configuration configuration = getBasicConf();
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
        assertEquals(true, esperEventProcessor.restoreConfiguration(configuration));

        Map<String, Attribute> attributes = esperEventProcessor.getEventTypeAttributes("TempSensor");
        assertEquals(2, attributes.size());
        assertEquals("double", attributes.get("temp").getType());
        assertEquals("string", attributes.get("temp_unit").getType());

        attributes = esperEventProcessor.getEventTypeAttributes("TempSensorAvg");
        assertEquals(2, attributes.size());
        assertEquals("double", attributes.get("avgTemp").getType());
        assertEquals("string", attributes.get("avgTemp_unit").getType());

        assertEquals(1, esperEventProcessor.getStatements().size());
        assertEquals(configuration.getStatements().get(0), esperEventProcessor.getStatements().get(0));
    }

    /**
     * Check that a bad configuration reset returns false
     * @throws ConfigurationException
     * @throws EventTypeNotFoundException
     */
    @Test
    public void checkFailPreviousConfigurationRestoration() throws ConfigurationException, EventTypeNotFoundException {
        Configuration configuration = getBasicConf();
        esperEventProcessor.setConfiguration(configuration);

        Configuration badConfiguration = new Configuration();
        badConfiguration.setEventTypeIns(Collections.emptyList());
        badConfiguration.setEventTypeOuts(Collections.emptyList());
        badConfiguration.setStatements(Collections.singletonList("BAD STATEMENT"));

        assertEquals(false, esperEventProcessor.restoreConfiguration(badConfiguration));
    }

    /**
     * Check that we correctly handle updating variable in configuration
     * @throws Exception
     */
    @Test
    public void checkVariableUpdate() throws Exception {
        Configuration configuration = emptyConfiguration();
        List<String> statements = new ArrayList<>();
        statements.add("create variable int i = 1");
        statements.add("create variable int j = i");
        configuration.setStatements(statements);
        esperEventProcessor.setConfiguration(configuration);

        Configuration configuration2 = emptyConfiguration();
        List<String> statements2 = new ArrayList<>();
        statements2.add("create variable int i = 2");
        statements2.add("create variable int j = i");
        configuration2.setStatements(statements2);
        esperEventProcessor.setConfiguration(configuration2);

        assertEquals(2, esperEventProcessor.getStatements().size());
        for (String statement : esperEventProcessor.getStatements()) {
            assertNotEquals("create variable int i = 1", statement);
        }
    }

    /**
     * Check that the update listener is called on when an event is generated by the Esper CEP engine
     * @throws ConfigurationException
     * @throws EventProcessingException
     */
    @Test
    public void checkUpdateListenerUpdated() throws ConfigurationException, EventProcessingException {

        esperEventProcessor.setConfiguration(getBasicConf());

        esperEventProcessor.processEvent(buildBasicEvent((double) 5.0));

        ArgumentCaptor<EventBean[]> eventsArg = ArgumentCaptor.forClass(EventBean[].class);
        verify(eventSinkListener).update(eventsArg.capture(), eq(null), any(EPStatement.class), any(EPServiceProvider.class));

        EventBean[] events = eventsArg.getValue();
        assertEquals(1, events.length);
        assertEquals(events[0].get("id"), "OUT1");
        assertEquals(5.0, events[0].get("avgTemp"));
        assertEquals("celcius", events[0].get("avgTemp_unit"));
    }

    private void sendXtemperature() {
        Random random = new Random(15);
        for (int i=1; i<100 ; i++) {
            try {
                esperEventProcessor.processEvent(buildBasicEvent((double) (15.5 + random.nextFloat())));
            } catch (EventProcessingException e) {
                Assert.fail("Not expected EventProcessingException");
            }
        }
    }

    @Test
    public void checkResetAndRestoreConfiguration() throws ConfigurationException, EventProcessingException {
        Configuration configuration = getBasicConf();
        esperEventProcessor.setConfiguration(configuration);
        esperEventProcessor.reset();
        assertNull(esperEventProcessor.getConfiguration());

        esperEventProcessor.setConfiguration(configuration);
        esperEventProcessor.processEvent(buildBasicEvent(5.0d));

        verify(eventSinkListener).update(any(), eq(null), any(EPStatement.class), any(EPServiceProvider.class));
    }

    @Test(expected = EventProcessingException.class)
    public void checkResetConfiguration() throws ConfigurationException, EventProcessingException {
        esperEventProcessor.setConfiguration(getBasicConf());

        esperEventProcessor.reset();

        assertNull(esperEventProcessor.getConfiguration());

        // Handling an event after reset must throw an EventProcessingException
        esperEventProcessor.processEvent(buildBasicEvent(5.0d));
    }
}

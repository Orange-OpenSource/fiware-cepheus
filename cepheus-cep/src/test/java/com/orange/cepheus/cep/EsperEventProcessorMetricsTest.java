/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.espertech.esper.client.*;
import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.EventTypeNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.orange.cepheus.cep.Util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


/**
 * Test for the Esper complex event processor with metrics
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource("classpath:application-metrics.properties") // enabled metrics configuration
public class EsperEventProcessorMetricsTest {

    @Mock
    EventSinkListener eventSinkListener;

    @Mock
    GaugeService gaugeService;

    @Autowired
    @InjectMocks
    private EsperEventProcessor esperEventProcessor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Check that esper metrics are enabled (esper statement inserted).
     * Check that listener will call Spring Boot gauge service.
     * @throws ConfigurationException
     * @throws EventTypeNotFoundException
     */
    @Test
    public void checkMetrics() throws ConfigurationException, EventTypeNotFoundException {
        esperEventProcessor.setConfiguration(getBasicConf());
        EventBean[] mockBeans = {mockMetricsBean};
        // Check that metric statement is inserted !
        for (EPStatement epStatement : esperEventProcessor.getEPStatements()) {
            if (epStatement.getName().equals("STATEMENT_METRIC")) {
                // Mock Esper metric event
                assertTrue(epStatement.getUpdateListeners().hasNext());
                epStatement.getUpdateListeners().next().update(mockBeans, null);
                // Check Gauge service is notified one for each metric
                verify(gaugeService, times(4)).submit(anyString(), anyDouble());
                return;
            }
        }
        fail("metric statement not found");
    }

    /**
     * Mock metric bean for statement listenner
     */
    private EventBean mockMetricsBean = new EventBean() {
        @Override public EventType getEventType() {
            return null;
        }

        @Override public Object get(String s) throws PropertyAccessException {
            if (s.equals("statementName")) {
                return "MockMetric";
            } else if (s.equals("engineURI")) {
                return "default";
            }
            return 1l;
        }

        @Override public Object getUnderlying() {
            return "";
        }

        @Override public Object getFragment(String s) throws PropertyAccessException {
            return "";
        }
    };
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata;

import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.cep.SubscriptionManager;
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.persistence.Persistence;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

/**
 * Test the Init bean.
 */
@RunWith(MockitoJUnitRunner.class)
public class InitTest {

    static Configuration configuration = new Configuration();

    @Mock
    public ComplexEventProcessor complexEventProcessor;

    @Mock
    public Persistence persistence;

    @Mock
    public SubscriptionManager subscriptionManager;

    /**
     * Check that CEP engine is called when configuration avail during Init initialization
     */
    @Test
    public void checkConfOk() throws ConfigurationException, PersistenceException {
        when(persistence.checkConfigurationDirectory()).thenReturn(true);
        when(persistence.loadConfiguration()).thenReturn(configuration);

        new Init(complexEventProcessor, persistence, subscriptionManager);

        verify(complexEventProcessor).setConfiguration(eq(configuration));
        verify(subscriptionManager).setConfiguration(eq(configuration));
    }

    /**
     * Check that the CEP engine is not called when no configuration exist on initialization
     */
    public void checkNoConf() throws ConfigurationException, PersistenceException {
        when(persistence.checkConfigurationDirectory()).thenReturn(false);
        when(persistence.loadConfiguration()).thenReturn(null);

        new Init(complexEventProcessor, persistence, subscriptionManager);

        verify(complexEventProcessor, never()).setConfiguration(anyObject());
        verify(subscriptionManager, never()).setConfiguration(anyObject());
    }

    @After
    public void resetMock() {
        reset(complexEventProcessor);
        reset(persistence);
        reset(subscriptionManager);
    }
}

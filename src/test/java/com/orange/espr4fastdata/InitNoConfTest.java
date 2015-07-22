/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata;

import com.orange.espr4fastdata.cep.ComplexEventProcessor;
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.persistence.Persistence;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

import static com.orange.espr4fastdata.InitNoConfTest.TestConfig;

/**
 * Test the Init bean.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, TestConfig.class})
public class InitNoConfTest {

    @org.springframework.context.annotation.Configuration
    static class TestConfig {

        @Bean
        public ComplexEventProcessor complexEventProcessor() {
            return Mockito.mock(ComplexEventProcessor.class);
        }

        @Bean
        public Persistence persistence() throws PersistenceException {
            Persistence p = Mockito.mock(Persistence.class);
            when(p.checkConfigurationDirectory()).thenReturn(false);
            when(p.loadConfiguration()).thenReturn(null);
            return p;
        }
    }

    @Autowired public Init init;

    @Autowired public ComplexEventProcessor complexEventProcessor;

    /**
     * Check that CEP engine is not called when no configuration avail
     */
    @Test public void initTest() throws ConfigurationException {
        verify(complexEventProcessor, never()).setConfiguration(anyObject());
    }
}

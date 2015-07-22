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
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.persistence.Persistence;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.mockito.Mockito.*;

/**
 * Test the Init bean.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, InitTest.TestConfig.class})
public class InitTest {

    static Configuration configuration = new Configuration();

    @SpringBootApplication
    static class TestConfig {

        @Bean
        public ComplexEventProcessor complexEventProcessor() {
            return Mockito.mock(ComplexEventProcessor.class);
        };

        @Bean
        public Persistence persistence() {
            Persistence p = Mockito.mock(Persistence.class);
            when(p.checkConfigurationDirectory()).thenReturn(true);
            try {
                when(p.loadConfiguration()).thenReturn(configuration);
            } catch (PersistenceException e) {
            }
            return p;
        }
    }

    @Autowired
    public Init init;

    @Autowired
    public ComplexEventProcessor complexEventProcessor;

    @Autowired
    public Persistence persistence;

    /**
     * Check that CEP engine is called when configuration avail during Init initialization
     */
    @Test
    public void checkConfOk() throws ConfigurationException {
        verify(complexEventProcessor).setConfiguration(eq(configuration));
    }

    @After
    public void resetMock() {
        reset(complexEventProcessor);
        reset(persistence);
    }
}

package com.orange.cepheus.broker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Created by pborscia on 06/08/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource(locations="classpath:test.properties")
public class ConfigurationTest {

    @Autowired
    Configuration configuration;


    @Test
    public void checkPropertiesValues(){
        assertEquals("10.25.12.123", configuration.getRemoteHost());
        assertEquals(8081, configuration.getRemotePort());
    }

}

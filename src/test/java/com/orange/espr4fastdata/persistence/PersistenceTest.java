/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.persistence;

import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.cep.EsperEventProcessor;
import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.persistence.JsonPersistence;
import com.orange.espr4fastdata.persistence.Persistence;
import com.orange.espr4fastdata.util.Util;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by pborscia on 30/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PersistenceTest {


    @Autowired
    private Persistence persistence;

    private Util util = new Util();

    @Test
    public void checkFileReturnFalse(){

        util.clearPersistedConfiguration();

        assertFalse(persistence.checkConfigurationDirectory());
    }

    @Test
    public void checkFileReturnTrue() throws PersistenceException {

        File confFile = new File("target/esper4fastdata.json");
        if (!confFile.exists()) {
            persistence.saveConfiguration(util.getBasicConf());
        }

        assertTrue(persistence.checkConfigurationDirectory());
    }


    @Test
    public void saveloadConfiguration() throws PersistenceException {

        util.clearPersistedConfiguration();

        persistence.saveConfiguration(util.getBasicConf());

        assertEquals(true, new File("target/esper4fastdata.json").exists());

        persistence.loadConfiguration();
    }

    @Test(expected=PersistenceException.class)
    public void loadConfigurationThrowException() throws PersistenceException {

        util.clearPersistedConfiguration();

        persistence.loadConfiguration();
    }
}

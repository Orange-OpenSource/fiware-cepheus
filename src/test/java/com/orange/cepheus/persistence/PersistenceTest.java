/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.persistence;

import com.orange.cepheus.Application;
import com.orange.cepheus.exception.PersistenceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.orange.cepheus.Util.*;


/**
 * Created by pborscia on 30/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PersistenceTest {

    private String path = "/tmp/esper4fastdata.json";

    @Autowired
    private Persistence persistence;

    @Test
    public void checkFileReturnFalse(){

        clearPersistedConfiguration();

        assertFalse(persistence.checkConfigurationDirectory());
    }

    @Test
    public void checkFileReturnTrue() throws PersistenceException {

        File confFile = new File(path);
        if (!confFile.exists()) {
            persistence.saveConfiguration(getBasicConf());
        }

        assertTrue(persistence.checkConfigurationDirectory());
    }


    @Test
    public void saveloadConfiguration() throws PersistenceException {

        clearPersistedConfiguration();

        persistence.saveConfiguration(getBasicConf());

        assertEquals(true, new File(path).exists());

        persistence.loadConfiguration();
    }

    @Test(expected=PersistenceException.class)
    public void loadConfigurationThrowException() throws PersistenceException {

        clearPersistedConfiguration();

        persistence.loadConfiguration();
    }

    private void clearPersistedConfiguration() {
        File confFile = new File(path);
        if (confFile.exists()) {
            confFile.delete();
        }
    }
}

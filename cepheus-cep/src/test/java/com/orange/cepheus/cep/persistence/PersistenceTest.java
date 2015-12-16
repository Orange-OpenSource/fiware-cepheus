/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.persistence;

import com.orange.cepheus.cep.Application;
import com.orange.cepheus.cep.exception.PersistenceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static com.orange.cepheus.cep.Util.*;


/**
 * Created by pborscia on 30/06/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class PersistenceTest {

    private static String configurationId = "smartcity/team1";

    @Value("${data.path}cep-smartcity-team1.json")
    private String path;

    @Autowired
    private Persistence persistence;

    @Test
    public void checkFileReturnFalse() {
        clearPersistedConfiguration();
        assertFalse(persistence.configurationExists(configurationId));
    }

    @Test
    public void checkFileReturnTrue() throws PersistenceException {
        File confFile = new File(path);
        if (!confFile.exists()) {
            persistence.saveConfiguration(configurationId, getBasicConf());
        }
        assertTrue(persistence.configurationExists(configurationId));
    }


    @Test
    public void saveloadConfiguration() throws PersistenceException {

        clearPersistedConfiguration();
        persistence.saveConfiguration(configurationId, getBasicConf());
        assertEquals(true, new File(path).exists());
        persistence.loadConfiguration(configurationId);
    }

    @Test
    public void deleteConfiguration() throws PersistenceException {
        // Save the configuration
        persistence.saveConfiguration(configurationId, getBasicConf());
        assertEquals(true, new File(path).exists());
        // Check that deleting configuration realy removes the file
        persistence.deleteConfiguration(configurationId);
        assertEquals(false, new File(path).exists());
        // Check that deleting an already removed configuration does not trigger any failure
        persistence.deleteConfiguration(configurationId);
    }

    @Test(expected=PersistenceException.class)
    public void loadConfigurationThrowException() throws PersistenceException {
        clearPersistedConfiguration();
        persistence.loadConfiguration(configurationId);
    }

    private void clearPersistedConfiguration() {
        File confFile = new File(path);
        if (confFile.exists()) {
            confFile.delete();
        }
    }
}

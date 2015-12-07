/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.persistence;


import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.cepheus.cep.model.Configuration;

import java.util.Collection;

/**
 * Created by pborscia on 30/06/2015.
 */
public interface Persistence {

    /**
     * check configuration file
     * @param id the configuration ID
     * @return true if configuration file exists else false
     */
    boolean configurationExists(String id);

    /**
     * Load persited configuration to the CEP
     * @param id the configuration ID
     * @throws PersistenceException when the configuration could not be loaded successfully
     * @return configuration
     */
    Configuration loadConfiguration(String id) throws PersistenceException;

    /**
     * Save configuration
     * @param id the configuration ID
     * @param configuration cep configuration to persist
     * @throws PersistenceException when the configuration could not be saved successfully
     */
    void saveConfiguration(String id, Configuration configuration) throws PersistenceException;

    /**
     * Retreive the list of all the configuration IDs.
     * @return the configurations IDs
     */
    Collection<String> listConfigurations();
}

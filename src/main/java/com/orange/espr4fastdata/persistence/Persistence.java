package com.orange.espr4fastdata.persistence;


import com.orange.espr4fastdata.exception.PersistenceException;
import com.orange.espr4fastdata.model.cep.Configuration;

/**
 * Created by pborscia on 30/06/2015.
 */
public interface Persistence {

    /**
     * check configuration file
     * @return true if configuration file exists else false
     */
    public Boolean checkConfigurationDirectory();

    /**
     * Load persited configuration to the CEP
     * @throws PersistenceException when the configuration could not be loaded successfully
     */
    public Configuration loadConfiguration() throws PersistenceException;

    /**
     * Save configuration
     * @param configuration
     * @throws PersistenceException when the configuration could not be saved successfully
     */
    public void saveConfiguration(Configuration configuration) throws PersistenceException;
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.EventProcessingException;
import com.orange.cepheus.cep.model.Event;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.cepheus.cep.model.Statement;

import java.util.List;

/**
 * Represents a Complex Event Processor (CEP)
 */
public interface ComplexEventProcessor {

    /**
     * Apply a new configuration to the CEP
     * @param configuration the new configuration to apply
     * @throws ConfigurationException when the configuration could not be applied successfully, restoreConfiguration can be attempted
     */
    void setConfiguration(Configuration configuration) throws ConfigurationException;

    /**
     * Try to restore a previous valid configuration after a ConfigurationException
     * @param previousConfiguration
     * @return true if previous configuration could be restored
     */
    boolean restoreConfiguration(Configuration previousConfiguration);

    /**
     * Reset the CEP engine (removing configuration)
     */
    void reset();

    /**
     * @return the active configuration or null
     */
    Configuration getConfiguration();

    /**
     * @return the list of the statements
     */
    List<Statement> getStatements();

    /**
     * Supply an event to the CEP
     * @param event
     * @throws EventProcessingException when the event could not be processed successfully
     */
    void processEvent(Event event) throws EventProcessingException;

}

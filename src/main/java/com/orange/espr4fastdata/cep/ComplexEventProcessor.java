package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.model.Event;
import com.orange.espr4fastdata.model.cep.Configuration;

/**
 * Represents a Complex Event Processor (CEP)
 */
public interface ComplexEventProcessor {

    /**
     * Apply a new configuration to the CEP
     * @param configuration the new configuration to apply
     * @throws ConfigurationException when the configuration could not be applied successfully
     */
    void setConfiguration(Configuration configuration) throws ConfigurationException;

    /**
     * Supply an event to the CEP
     * @param event
     * @throws EventProcessingException when the event could not be processed successfully
     */
    void processEvent(Event event) throws EventProcessingException;
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.*;
import com.orange.espr4fastdata.exception.ConfigurationException;
import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.exception.EventTypeNotFoundException;
import com.orange.espr4fastdata.model.cep.Attribute;
import com.orange.espr4fastdata.model.cep.EventType;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * ComplexEventProcessor implementation using EsperTech Esper CEP
 */
@Component
public class EsperEventProcessor implements ComplexEventProcessor {

    private static Logger logger = LoggerFactory.getLogger(EsperEventProcessor.class);

    private final EPServiceProvider epServiceProvider;
    private Configuration configuration;

    @Autowired
    public EventSinkListener eventSinkListener;

    public EsperEventProcessor() {
        epServiceProvider = EPServiceProviderManager.getDefaultProvider(new com.espertech.esper.client.Configuration());
    }

    /**
     * Apply a new configuration to the Esper CEP
     * @param configuration the new configuration to apply
     */
    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        Configuration previousConfiguration = this.configuration;
        ConfigurationOperations operations = epServiceProvider.getEPAdministrator().getConfiguration();
        try {
            Collection<? extends  EventType> previousEventTypes = new LinkedList<>();

            // Update incoming event types
            if (previousConfiguration != null) {
                previousEventTypes = previousConfiguration.getEventTypeIns();
            }
            this.updateEventTypes(previousEventTypes, configuration.getEventTypeIns(), operations);

            // Update outgoing event types
            if (previousConfiguration != null) {
                previousEventTypes = previousConfiguration.getEventTypeOuts();
            }
            this.updateEventTypes(previousEventTypes, configuration.getEventTypeOuts(), operations);

            // Update the statements
            this.updateStatements(configuration.getStatements());

            this.configuration = configuration;
            eventSinkListener.setConfiguration(configuration);

        } catch (Exception e) {
            // TODO reset all esper internal state, reset previous configuration
            throw new ConfigurationException("Failed to apply new configuration", e);
        }
    }

    /**
     * Make Esper process an event
     * @param event
     * @throws EventProcessingException
     */
    public void processEvent(Event event) throws EventProcessingException {
        logger.debug("Event sent to Esper {}", event.toString());

        try {
            this.epServiceProvider.getEPRuntime().sendEvent(event.getAttributes(), event.getType());
        } catch (com.espertech.esper.client.EPException e) {
            throw new EventProcessingException(e.getMessage());
        }
    }

    /**
     * Return a list of Attribute for a given even type. This is mainly usefull for testing.
     * @param eventTypeName
     * @return
     * @throws EventTypeNotFoundException
     */
    public List<Attribute> getEventTypeAttributes(String eventTypeName) throws EventTypeNotFoundException {
        List<Attribute> attributes = new ArrayList<Attribute>();

        com.espertech.esper.client.EventType eventType = epServiceProvider.getEPAdministrator().getConfiguration().getEventType(eventTypeName);
        if (eventType != null){
            for (String name : eventType.getPropertyNames()) {
                if (!("id".equals(name))) {
                    String type = eventType.getPropertyType(name).getSimpleName().toLowerCase();
                    attributes.add(new Attribute(name, type));
                }
            }
        } else {
            throw new EventTypeNotFoundException("The event type does not exist.");
        }

        return attributes;
    }

    /**
     * Update the CEP event types by adding new types and removing the older ones.
     *
     * @param oldList the previous list of event types
     * @param newList the new list of event types
     * @param operations the CEP configuration
     */
    private void updateEventTypes(Collection<? extends EventType> oldList, Collection<? extends EventType> newList, ConfigurationOperations operations) {
        List<? extends EventType> eventTypesToRemove = new LinkedList<>(oldList);
        eventTypesToRemove.removeAll(newList);

        List<? extends  EventType> eventTypesToAdd = new LinkedList<>(newList);
        eventTypesToAdd.removeAll(oldList);

        // List all statements depending on the event types to remove
        Set<String> statementsToDelete = new HashSet<>();
        for (EventType eventType : eventTypesToRemove) {
            statementsToDelete.addAll(operations.getEventTypeNameUsedBy(eventType.getType()));
        }
        // Delete all the statements depending on the event types to remove
        for (String statementName : statementsToDelete) {
            EPStatement statement = epServiceProvider.getEPAdministrator().getStatement(statementName);
            if (statement != null) {
                statement.stop();
                statement.destroy();
            }
        }
        // Finally remove the event types
        for (EventType eventType : eventTypesToRemove) {
            operations.removeEventType(eventType.getType(), false);
        }

        for (EventType eventType : eventTypesToAdd) {
            String eventTypeName = eventType.getType();
            // Add all event type properties, plus the reserved id attribute
            Properties properties = new Properties();
            properties.setProperty("id", "string");
            for (Attribute attribute : eventType.getAttributes()) {
                properties.setProperty(attribute.getName(), attribute.getType());
            }
            // Add event type
            operations.addEventType(eventTypeName, properties);
        }
    }

    /**
     * Update the EPL statements by adding new statements, and removing unused statements
     * @param statements
     * @throws NoSuchAlgorithmException
     */
    private void updateStatements(Collection<String> statements) throws NoSuchAlgorithmException {
        // Keep a list of MD5 hash of all added statements
        Set<String> hashes = new HashSet<>();

        // Update EPL statements
        for (String eplStatement : statements) {
            String hash = MD5(eplStatement);
            hashes.add(hash);

            // Create statement if does not already exist
            EPStatement statement = epServiceProvider.getEPAdministrator().getStatement(hash);
            if (statement == null) {
                statement = epServiceProvider.getEPAdministrator().createEPL(eplStatement, hash);
                statement.addListener(eventSinkListener);
            }

        }

        // Removed unused statements
        for (String hash : epServiceProvider.getEPAdministrator().getStatementNames()) {
            if (!hashes.contains(hash)) {
                EPStatement statement = epServiceProvider.getEPAdministrator().getStatement(hash);
                if (statement != null) {
                    statement.destroy();
                }
            }
        }
    }

    /**
     * Generate the MD5 hash of a message
     * @param message
     * @return the hash
     * @throws NoSuchAlgorithmException
     */
    private String MD5(String message) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] array = md.digest(message.getBytes());
        return new BigInteger(1, array).toString(16);
        /*StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
        }
        return sb.toString();*/
    }
}

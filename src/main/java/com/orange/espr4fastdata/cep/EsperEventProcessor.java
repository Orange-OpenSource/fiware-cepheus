package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.*;
import com.orange.espr4fastdata.exception.EventTypeNotFoundException;
import com.orange.espr4fastdata.model.cep.Attribute;
import com.orange.espr4fastdata.model.cep.EventType;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.model.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import java.util.*;

/**
 * ComplexEventProcessor implementation using EsperTech Esper CEP
 */
@ComponentScan
public class EsperEventProcessor implements ComplexEventProcessor {

    private static Logger logger = LoggerFactory.getLogger(EsperEventProcessor.class);

    private EPServiceProvider epServiceProvider;
    private Configuration configuration;

    public EsperEventProcessor() {
        epServiceProvider = EPServiceProviderManager.getDefaultProvider(new com.espertech.esper.client.Configuration());
    }

    public void setConfiguration(Configuration configuration) {
        Configuration previousConfiguration = this.configuration;
        ConfigurationOperations operations = epServiceProvider.getEPAdministrator().getConfiguration();
        try {
            List<? extends  EventType> previousEventTypes = new LinkedList<>();

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

            // Update EPL statements
            for (String eplStatement : configuration.getStatements()) {
                EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(eplStatement);
                EventSinkListener eventSinkListener = new EventSinkListener();
                statement.addListener(eventSinkListener);
            }

            this.configuration = configuration;
        } catch (Exception e) {
            // TODO reset all esper internal state, reset previous configuration
            throw new ConfigurationException("Failed to apply new configuration", e);
        }
    }

    public void processEvent(Event event) {
        logger.info("Event sent to Esper {}", event.toString());
        this.epServiceProvider.getEPRuntime().sendEvent(event.getAttributes(), event.getType());
    }


    public List<Attribute> getEventTypeAttributes(String eventTypeName) throws EventTypeNotFoundException {
        List<Attribute> attributes = new ArrayList<Attribute>();

        com.espertech.esper.client.EventType eventType = this.getEpServiceProvider().getEPAdministrator().getConfiguration().getEventType(eventTypeName);
        if (eventType != null){
            for (String name : eventType.getPropertyNames()) {
                if (!("id".equals(name))) {
                    Attribute attribute = new Attribute();

                    attribute.setName(name);
                    attribute.setType(eventType.getPropertyType(name).getSimpleName().toLowerCase());

                    attributes.add(attribute);

                }
            }
        } else {
            throw new EventTypeNotFoundException("The event type does not exist.");
        }

        return attributes;
    }

    public EPServiceProvider getEpServiceProvider() {
        return epServiceProvider;
    }

    /**
     * Update the CEP event types by adding new types and removing the older ones.
     *
     * @param oldList the previous list of event types
     * @param newList the new list of event types
     * @param operations the CEP configuration
     */
    private void updateEventTypes(List<? extends EventType> oldList, List<? extends EventType> newList, ConfigurationOperations operations) {
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
}

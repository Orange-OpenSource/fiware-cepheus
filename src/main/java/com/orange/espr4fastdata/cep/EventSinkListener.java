/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.*;
import com.orange.espr4fastdata.model.cep.*;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.model.ngsi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * An update listener that triggers NGSI /updateContext requests on outgoing events
 */
@Component
public class EventSinkListener implements StatementAwareUpdateListener {

    private static Logger logger = LoggerFactory.getLogger(EventSinkListener.class);

    @Autowired
    private Sender sender;

    /**
     * All outgoing outgoingEvents accessible by type
     */
    private Map<String, EventTypeOut> outgoingEvents;

    /**
     * Called by Esper CEP engine when a new event is added or removed from a statement.
     * This will trigger asynchronous updateContext request to broker for corresponding ContextElements
     */
    @Override
    public void update(EventBean[] added, EventBean[] removed, EPStatement epStatement, EPServiceProvider epServiceProvider) {

        // ignore updates for removed events
        if (added == null) {
            return;
        }

        logger.debug("UPDATE for {} ({})", epStatement.getText(), added.length);

        for (EventBean eventBean : added) {

            // Debug some information about the event
            if (logger.isDebugEnabled()) {
                logger.debug("EventType {} eventBean {}", eventBean.getEventType().getName(), eventBean.toString());
                for (String propertyName : eventBean.getEventType().getPropertyNames()) {
                    logger.debug("property {} value {} ", propertyName, eventBean.get(propertyName));
                }
            }

            // Send updateContext requests to each broker
            final String type = eventBean.getEventType().getName();
            final EventTypeOut eventTypeOut = getEventTypeOut(type);
            if (eventTypeOut == null) {
                logger.warn("EventTypeOut {} doesn't exist in Configuration, updateContext not sent", type);
            } else {
                UpdateContext updateContext = buildUpdateContextRequest(eventBean, eventTypeOut);
                if (updateContext != null) {
                    for (Broker broker : eventTypeOut.getBrokers()) {
                        sender.postMessage(updateContext, broker);
                    }
                }
            }
        }
    }

    /**
     * Configuration access the type of outgoing events
     */
    public void setConfiguration(Configuration configuration) {
        // Store outgoing events in an hashmap using type as key (faster random access)
        Map<String, EventTypeOut> events = new HashMap<>();
        for (EventTypeOut event : configuration.getEventTypeOuts()) {
            events.put(event.getType(), event);
        }
        this.outgoingEvents = events;
    }

    /**
     * Find an outgoing event based on it type
     *
     * @return the event or null if not found
     */
    private EventTypeOut getEventTypeOut(String eventBeanType) {
        return outgoingEvents.get(eventBeanType);
    }

    /**
     * Build an updateContext request from an event
     * @return An updateContext request, or null if no attributes are updated
     */
    private UpdateContext buildUpdateContextRequest(EventBean eventBean, EventTypeOut eventTypeOut) {

        // When id is undefined or empty in the event, reuse the one defined in the configuration
        String id = (String)eventBean.get("id");
        if (id == null || "".equals(id)) {
            id = eventTypeOut.getId();
        }

        // Add each attribute as a context attribute
        List<ContextAttribute> contextAttributes = new LinkedList<>();
        for (Attribute attribute : eventTypeOut.getAttributes()) {
            String name = attribute.getName();
            Object value = eventBean.get(name);
            if (value != null) {
                contextAttributes.add(new ContextAttribute(name, attribute.getType(), value));
            }
        }

        // When no attributes was updated (?!), there is no point to trigger a request
        if (contextAttributes.size() == 0) {
            return null;
        }

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(id, eventTypeOut.getType(), eventTypeOut.isPattern()));
        contextElement.setContextAttributeList(contextAttributes);

        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        updateContext.setContextElements(Collections.singletonList(contextElement));
        return updateContext;
    }
}

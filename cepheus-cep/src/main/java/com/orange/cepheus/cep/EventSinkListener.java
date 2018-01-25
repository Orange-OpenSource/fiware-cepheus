/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;
import com.espertech.esper.client.*;
import com.orange.cepheus.cep.model.*;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * An update listener that triggers NGSI /updateContext requests on outgoing events
 */
@Component
public class EventSinkListener implements StatementAwareUpdateListener {

    private static Logger logger = LoggerFactory.getLogger(EventSinkListener.class);

    @Autowired
    private NgsiClient ngsiClient;

    @Autowired
    private EventMapper eventMapper;

    private Configuration configuration;
   

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
        
           /* contribution by CAMPIE project developers at Cairo university
              Ahmad Essam
              Mahmoud Samir
              Karem Fathy
           */
      
           //getting the provider URI containing the service and the servicePath through EPServiceProvider interface
           //looping to get the ServicePath from the URI
           String[] tenant = epServiceProvider.getURI().split("/");
           String service=tenant[0];
           String servicePath="";
           for(int i=1;i<tenant.length;i++){
           servicePath += "/"+tenant[i];

           }
       

        // ignore updates for removed events           
        if (added == null) {
            return;
        }

        for (EventBean eventBean : added) {
            
            // Debug some information about the event
            if (logger.isInfoEnabled()) {
                StringBuilder sb = new StringBuilder();
                for (String propertyName : eventBean.getEventType().getPropertyNames()) {
                    sb.append(" / ").append(propertyName).append(':').append(eventBean.get(propertyName));
                }
            
                logger.info("EventOut: {}{} from {}", eventBean.getEventType().getName(), sb.toString(), epStatement.getText());
                


             }

            // Send updateContext requests to each broker
            final String type = eventBean.getEventType().getName();
            final EventTypeOut eventTypeOut = getEventTypeOut(type);
            if (eventTypeOut == null) {
                logger.debug("EventTypeOut {} doesn't exist in Configuration, updateContext not sent", type);
            } else {
                UpdateContext updateContext = buildUpdateContextRequest(eventBean, eventTypeOut);
                if (updateContext != null) {
                    for (Broker broker : eventTypeOut.getBrokers()) {
                        assert broker != null;
                        // HttpHeaders httpHeaders = getHeadersForBroker(broker);
                        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders(broker.getUrl());
                        
                        //adding the service and servicePath to the headers of the out event request
                        httpHeaders.add("Fiware-Service",service);
                        httpHeaders.add("Fiware-ServicePath",servicePath);
                        ngsiClient.updateContext(broker.getUrl(), httpHeaders, updateContext).addCallback(
                                updateContextResponse ->
                                    logger.debug("UpdateContext completed for {}", broker.getUrl()),
                                throwable ->
                                    logger.warn("UpdateContext failed for {}", broker.getUrl(), throwable)
                        );
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
        this.configuration = configuration;
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
        ContextElement contextElement = eventMapper.contextElementFromEvent(eventBean, eventTypeOut);
        if (contextElement == null) {
            return null;
        }
        UpdateContext updateContext = new UpdateContext(UpdateAction.APPEND);
        updateContext.setContextElements(Collections.singletonList(contextElement));
        return updateContext;
    }

    /**
     * Set custom headers for Brokers either from the Broker section or from the actual tenant configuration
     */
    private HttpHeaders getHeadersForBroker(Broker broker) {
        HttpHeaders httpHeaders = ngsiClient.getRequestHeaders(broker.getUrl());

        if (broker.getServiceName() != null) {
            httpHeaders.add("Fiware-Service", broker.getServiceName());
        } else if (configuration.getService() != null) {
            httpHeaders.add("Fiware-Service", configuration.getService());
        }
        if (broker.getServicePath() != null) {
            httpHeaders.add("Fiware-ServicePath", broker.getServicePath());
        } else if (configuration.getServicePath() != null) {
            httpHeaders.add("Fiware-ServicePath", configuration.getServicePath());
        }
        if (broker.getAuthToken() != null) {
            httpHeaders.add("X-Auth-Token", broker.getAuthToken());
        }
        return httpHeaders;
    }
}

package com.orange.newespr4fastdata.cep;

import com.espertech.esper.client.*;
import com.orange.newespr4fastdata.exception.EventTypeNotFoundException;
import com.orange.newespr4fastdata.model.cep.Attribute;
import com.orange.newespr4fastdata.model.cep.EventType;
import com.orange.newespr4fastdata.model.cep.Configuration;
import com.orange.newespr4fastdata.model.cep.EventIn;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by pborscia on 03/06/2015.
 */
@ComponentScan
public class ComplexEventProcessing {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(ComplexEventProcessing.class);

    private EPServiceProvider epServiceProvider;
    private Configuration configuration;

    public ComplexEventProcessing() {
        epServiceProvider = EPServiceProviderManager.getDefaultProvider(new com.espertech.esper.client.Configuration());
    }

    public void setConfiguration(Configuration configuration) {

        this.configuration = configuration;

        ConfigurationOperations configurationOperations = epServiceProvider.getEPAdministrator().getConfiguration();

        this.createEventType(configuration.getEventTypeIns(), configurationOperations);
        this.createEventType(configuration.getEventTypeOuts(), configurationOperations);

        for (String rule: configuration.getRules()) {
            EPStatement statement = epServiceProvider.getEPAdministrator().createEPL(rule);
            EventSinkListener eventSinkListener = new EventSinkListener();
            statement.addListener(eventSinkListener);
        }
    }

    public void sendEventInEsper(EventIn eventIn) {
        logger.info("Event In sended to Esper {}", eventIn.toString());
        this.epServiceProvider.getEPRuntime().sendEvent(eventIn.getAttributesMap(),eventIn.getEventTypeName());
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

    private void createEventType(List<? extends EventType> eventTypes, ConfigurationOperations configurationOperations){

        for (EventType eventType : eventTypes) {
            String eventTypeName = eventType.getType();
            if (configurationOperations.getEventType(eventTypeName) == null) {
                Properties properties = new Properties();
                //ad Id as property
                properties.setProperty("id", "string");
                if (eventType.getAttributes() != null) {
                    for (Attribute attribute : eventType.getAttributes()) {
                        properties.setProperty(attribute.getName(), attribute.getType());
                    }
                }
                configurationOperations.addEventType(eventTypeName, properties);
            }
        }
    }


}

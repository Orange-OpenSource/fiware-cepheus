package com.orange.cepheus.cep;

import com.jayway.jsonpath.*;
import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.EventProcessingException;
import com.orange.cepheus.cep.exception.TypeNotFoundException;
import com.orange.cepheus.cep.model.*;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.ngsi.model.ContextAttribute;
import com.orange.ngsi.model.ContextElement;
import com.orange.ngsi.model.ContextMetadata;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Map a NGSI ContextElement to an CEP event
 */
@Component
public class EventMapper {

    private Map<String, JsonPath> jsonpaths = new HashMap<>();

    /**
     * Compile JSON paths from Attributes and Metadata of the new configuration
     * @param configuration the new configuration
     */
    public void setConfiguration(Configuration configuration) throws ConfigurationException {
        Map<String, JsonPath> jsonpaths = new HashMap<>();

        for (EventTypeIn eventTypeIn : configuration.getEventTypeIns()) {
            for (Attribute attribute : eventTypeIn.getAttributes()) {
                String jsonpath = attribute.getJsonpath();
                if (jsonpath != null) {
                    // JsonPath caches paths internally, no need to reuse them from one configuration to another.
                    // Aggregate path by event type / attribute name
                    try {
                        jsonpaths.put(eventTypeIn.getType() + "/" + attribute.getName(), JsonPath.compile(jsonpath));
                    } catch (IllegalArgumentException|InvalidPathException e) {
                        throw new ConfigurationException("invalid jsonpath expression for attribute "+attribute.getName(), e);
                    }
                }

                for (Metadata metadata : attribute.getMetadata()) {
                    jsonpath = metadata.getJsonpath();
                    if (jsonpath != null) {
                        // Same as attribute but with metadata name
                        try {
                            jsonpaths.put(eventTypeIn.getType() + "/" + attribute.getName() + "/" + metadata.getName(), JsonPath.compile(jsonpath));
                        } catch (IllegalArgumentException|InvalidPathException e) {
                            throw new ConfigurationException("invalid jsonpath expression for metadata "+attribute.getName()+"/"+metadata.getName(), e);
                        }
                    }
                }
            }
        }

        this.jsonpaths = jsonpaths;
    }

    /**
     * Map an EventType go an Esper event type.
     * All properties (metadata, attributes and id) are defined at the same level.
     * Collisions might then occur, but id and then attributes will override metadata properties.
     * @param eventType the Configuration event type
     * @return a map of types
     */
    public Map<String, Object> esperTypeFromEventType(EventType eventType) {
        // Add all metadata
        Map<String, Object> properties = new HashMap<>();
        for (Attribute attribute : eventType.getAttributes()) {
            // For metadata, join with attribute name using a '_'
            for (Metadata meta : attribute.getMetadata()) {
                properties.put(attribute.getName() + "_" + meta.getName(), classForType(meta.getType()));
            }
        }
        // Override with event type properties, plus the reserved id attribute
        for (Attribute attribute : eventType.getAttributes()) {
            properties.put(attribute.getName(), classForType(attribute.getType()));
        }
        properties.put("id", String.class);
        return properties;
    }

    /**
     * Convert a NGSI Context Element to an Esper event.
     * All properties (metadata, attributes and id) are defined at the same level.
     * Collisions might then occur, but id and then attributes will override metadata properties.
     * @param contextElement the NGSI Context Element
     * @return an event to process
     * @throws EventProcessingException if the conversion fails
     */
    public Event eventFromContextElement(ContextElement contextElement) throws EventProcessingException, TypeNotFoundException {
        String eventId = contextElement.getEntityId().getId();
        String eventType = contextElement.getEntityId().getType();

        Event event = new Event(eventType);

        // Add metadata values first
        for(ContextAttribute contextAttribute : contextElement.getContextAttributeList()) {
            String attrName = contextAttribute.getName();
            for (ContextMetadata contextMetada : contextAttribute.getMetadata()) {
                String name = contextMetada.getName();
                String type = contextMetada.getType();

                Object value = contextMetada.getValue();

                // Extract value from jsonpath if any
                JsonPath jsonPath = jsonpaths.get(eventType + "/" + attrName + "/" + name);
                if (jsonPath != null) {
                    value = jsonPath.read(value);
                }

                value = valueForType(value, type, name);
                event.addValue(attrName + "_" + name, value);
            }
        }

        // Override with attributes values
        for(ContextAttribute contextAttribute : contextElement.getContextAttributeList()) {
            String name = contextAttribute.getName();
            String type = contextAttribute.getType();

            Object value = contextAttribute.getValue();

            // Extract value from jsonpath if any
            JsonPath jsonPath = jsonpaths.get(eventType + "/" + name);
            if (jsonPath != null) {
                value = jsonPath.read(value);
            }

            value = valueForType(value, type, name);
            if (value == null) {
                throw new EventProcessingException("Value cannot be null for attribute "+name);
            }

            event.addValue(name, value);
        }

        // Override with id
        event.addValue("id", eventId);

        return event;
    }

    private Class classForType(String attributeType) {
        switch (attributeType) {
            case "string":
                return String.class;
            case "int":
                return int.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "boolean":
                return boolean.class;
            default:
                return Object.class;
        }
    }

    /**
     * @param value the value to convert
     * @param type NGSI type
     * @param name used for error handling
     * @return a Java Object for given value
     * @throws EventProcessingException if the conversion fails
     */
    private Object valueForType(Object value, String type, String name) throws EventProcessingException {
        // when type is not defined, handle as string
        if (type == null) {
            return value;
        }
        if (value instanceof String) {
            return valueForString((String) value, type, name);
        }
        return value;
    }

    /**
     * @param value the value to convert
     * @param type NGSI type
     * @param name used for error handling
     * @return a Java Object for given value
     * @throws EventProcessingException if the conversion fails
     */
    private Object valueForString(String value, String type, String name) throws EventProcessingException {
        // when type is not defined, handle as string
        if (type == null) {
            return value;
        }
        try {
            switch (type) {
                case "string":
                    return value;
                case "boolean":
                    return value;
                case "int":
                    return Integer.valueOf(value);
                case "float":
                    return Float.valueOf(value);
                case "double":
                    return Double.valueOf(value);
                default:
                    return value;
            }
        } catch (NumberFormatException e) {
            throw new EventProcessingException("Failed to parse value "+value+" for attribute "+name);
        }
    }
}

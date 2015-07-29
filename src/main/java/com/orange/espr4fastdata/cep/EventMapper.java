package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.exception.TypeNotFoundException;
import com.orange.espr4fastdata.model.Event;
import com.orange.espr4fastdata.model.cep.Attribute;
import com.orange.espr4fastdata.model.cep.EventType;
import com.orange.espr4fastdata.model.cep.Metadata;
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
        String id = contextElement.getEntityId().getId();
        String type = contextElement.getEntityId().getType();

        Event event = new Event(type);

        // Add metadata values first
        for(ContextAttribute contextAttribute : contextElement.getContextAttributeList()) {
            String attrName = contextAttribute.getName();
            for (ContextMetadata contextMetada : contextAttribute.getMetadata()) {
                event.addValue(attrName + "_" + contextMetada.getName(), contextMetada.getValue());
            }
        }

        // Override with attributes values
        for(ContextAttribute contextAttribute : contextElement.getContextAttributeList()) {
            String name = contextAttribute.getName();

            Object value = contextAttribute.getValue();
            if (value == null) {
                throw new EventProcessingException("Value cannot be null for attribute "+name);
            }

            event.addValue(name, value);
        }

        // Override with id
        event.addValue("id", id);

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
    private Object valueForType(String value, String type, String name) throws EventProcessingException {
        // when type is not defined, handle as string
        if (type == null) {
            return value;
        }
        try {
            switch (type) {
                case "string":
                    return value;
                case "boolean":
                    return new Boolean(value);
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

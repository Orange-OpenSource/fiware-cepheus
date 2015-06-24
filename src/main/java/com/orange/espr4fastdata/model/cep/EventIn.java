package com.orange.espr4fastdata.model.cep;

import java.util.Map;

/**
 * Created by pborscia on 04/06/2015.
 */
public class EventIn {
    Map attributesMap;
    String eventTypeName;

    public EventIn() {
    }

    public Map getAttributesMap() {
        return attributesMap;
    }

    public void setAttributesMap(Map attributesMap) {
        this.attributesMap = attributesMap;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    @Override
    public String toString() {
        return "EventIn{" +
                "attributesMap=" + attributesMap +
                ", eventTypeName='" + eventTypeName + '\'' +
                '}';
    }
}

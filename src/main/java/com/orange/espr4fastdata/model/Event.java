package com.orange.espr4fastdata.model;

import java.util.Map;

/**
 * Created by pborscia on 04/06/2015.
 */
public class Event {
    Map attributes;
    String type;

    public Event() {
    }

    public Event(String type, Map attributes) {
        this.type = type;
        this.attributes = attributes;
    }

    public Map getAttributes() { return attributes; }

    public void setAttributes(Map attributes) { this.attributes = attributes; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type;}

    @Override
    public String toString() {
        return "Event{" +
                "attributes=" + attributes +
                ", type='" + type + '\'' +
                '}';
    }
}

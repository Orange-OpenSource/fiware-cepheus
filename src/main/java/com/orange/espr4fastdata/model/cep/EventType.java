package com.orange.espr4fastdata.model.cep;

import java.util.*;

public class EventType {

    private String id;
    private String type;
    private boolean isPattern;
    private Set<Attribute> attributes; // using a set to handle equals on unordered attributes

    public EventType() {
    }

    public EventType(String id, String type, boolean isPattern) {
        this.id = id;
        this.type = type;
        this.isPattern = isPattern;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public Set<Attribute> getAttributes() {
        if (attributes == null)
        {
            attributes = new HashSet<>();
        }
        return attributes;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPattern() {
        return isPattern;
    }

    public void setIsPattern(Boolean isPattern) {
        this.isPattern = isPattern;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getAttributesMap() {
        Map<String, String> attributes = new HashMap<String, String>();
        for (Attribute attribute : this.attributes) {
            attributes.put(attribute.getName(), attribute.getType());
        }
        return attributes;
    }

    public boolean equals(EventType e) {
        return (type.equals(e.type) && isPattern == e.isPattern && id.equals(e.id) && attributes.equals(e.attributes));
    }

    public void addAttribute(Attribute attribute) {
        if (attributes == null) {
            attributes = new HashSet<>();
        }
        attributes.add(attribute);
    }
}

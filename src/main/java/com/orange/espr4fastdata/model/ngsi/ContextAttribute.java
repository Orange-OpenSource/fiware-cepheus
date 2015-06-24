package com.orange.espr4fastdata.model.ngsi;

import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
public class ContextAttribute {

    private String name;
    private String type;
    private String contextValue;
    private List<ContextMetadata> metadata;

    public ContextAttribute() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContextValue() {
        return contextValue;
    }

    public void setContextValue(String contextValue) {
        this.contextValue = contextValue;
    }

    public List<ContextMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<ContextMetadata> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "ContextAttribute{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", contextValue='" + contextValue + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}

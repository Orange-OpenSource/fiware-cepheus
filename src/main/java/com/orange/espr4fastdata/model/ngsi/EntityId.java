package com.orange.espr4fastdata.model.ngsi;

/**
 * Created by pborscia on 04/06/2015.
 */
public class EntityId {

    private String id;
    private String type;
    private Boolean isPattern;

    public EntityId() {
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

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getIsPattern() {
        return isPattern;
    }

    public void setIsPattern(Boolean isPattern) {
        this.isPattern = isPattern;
    }

    @Override
    public String toString() {
        return "EntityId{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", isPattern=" + isPattern +
                '}';
    }
}

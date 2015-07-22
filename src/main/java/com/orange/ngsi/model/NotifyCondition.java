package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by pborscia on 17/07/2015.
 */
public class NotifyCondition {

    NotifyConditionEnum type;

    List<String> condValues;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String restriction;


    public NotifyCondition() {
    }

    public NotifyCondition(NotifyConditionEnum type, List<String> condValues) {
        this.type = type;
        this.condValues = condValues;
    }

    public NotifyConditionEnum getType() {
        return type;
    }

    public void setType(NotifyConditionEnum type) {
        this.type = type;
    }

    public List<String> getCondValues() {
        return condValues;
    }

    public void setCondValues(List<String> condValues) {
        this.condValues = condValues;
    }

    public String getRestriction() {
        return restriction;
    }

    public void setRestriction(String restriction) {
        this.restriction = restriction;
    }
}

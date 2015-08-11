/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by pborscia on 11/08/2015.
 */
public class QueryContext {

    @JsonProperty(value = "entities", required = true)
    private List<EntityId> entityIdList;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(value = "attributes")
    private List<String> attributList;

    private Restriction restriction;

    public QueryContext() {
    }

    public QueryContext(List<EntityId> entityIdList) {
        this.entityIdList = entityIdList;
    }

    public List<EntityId> getEntityIdList() {
        return entityIdList;
    }

    public void setEntityIdList(List<EntityId> entityIdList) {
        this.entityIdList = entityIdList;
    }

    public List<String> getAttributList() {
        return attributList;
    }

    public void setAttributList(List<String> attributList) {
        this.attributList = attributList;
    }

    public Restriction getRestriction() {
        return restriction;
    }

    public void setRestriction(Restriction restriction) {
        this.restriction = restriction;
    }

    @Override
    public String toString() {
        return "QueryContext{" +
                "entityIdList=" + entityIdList +
                ", attributList=" + attributList +
                ", restriction=" + restriction +
                '}';
    }
}

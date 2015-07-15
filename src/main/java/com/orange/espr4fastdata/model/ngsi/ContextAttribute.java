/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.ngsi;

import com.fasterxml.jackson.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pborscia on 04/06/2015.
 */
public class ContextAttribute {


    private String name;

    private String type;

    private Map<String,Object> value = new HashMap<String,Object>();

    // Could alternatively add setters, but since these are mandatory
    @JsonCreator
    public ContextAttribute(@JsonProperty("name") String name, @JsonProperty("type") String type)
    {
        this.name = name;
        this.type = type;
    }

    /**
     *
     * @return
     * The name
     */

    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    public Object get(String name) {
        return value.get(name);
    }

    // "any getter" needed for serialization
    @JsonAnyGetter
    public Map<String,Object> getValue() {
        return value;
    }

    @JsonAnySetter
    public void set(String name, Object value1) {
        value.put(name, value1);
    }
}

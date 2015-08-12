/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Event sent to the Esper CEP engine.
 * An event is a collection of properties defined by a type.
 */
public class Event {

    private Map<String, Object> values;
    private String type;

    public Event() {
    }

    public Event(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addValue(String name, Object value) {
        if (values == null) {
            values = new HashMap<>();
        }
        values.put(name, value);
    }

    public Map<String, Object> getValues() {
        return values;
    }
}

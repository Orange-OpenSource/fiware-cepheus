/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

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

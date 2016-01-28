/*
 * Copyright (C) 2016 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

/**
 * Request for POST/PUT /contextEntities/{entityID}/attributes/{attributeName}
 */
@JacksonXmlRootElement(localName = "updateContextAttributeRequest")
public class UpdateContextAttribute {

    @JsonUnwrapped
    ContextAttribute attribute;

    public UpdateContextAttribute() {
    }

    public ContextAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(ContextAttribute attribute) {
        this.attribute = attribute;
    }

    @Override
    public String toString() {
        return "UpdateContextAttribute{" +
                "attribute=" + attribute +
                '}';
    }
}

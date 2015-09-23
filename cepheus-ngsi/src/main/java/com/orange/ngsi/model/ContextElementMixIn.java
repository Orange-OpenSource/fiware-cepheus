package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.Collections;
import java.util.List;

/**
 * Mixin annotations for the JSON (de)serialization for ContextElement
 */
public class ContextElementMixIn {

    @JsonUnwrapped
    EntityId entityId;

    @JsonProperty("attributes")
    List<ContextAttribute> contextAttributeList;
}

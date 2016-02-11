package com.orange.ngsi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import java.util.Collections;
import java.util.List;

/**
 * Mixin annotation for the JSON (de)serialization for ContextElement/AppendContextElementResponse
 */
public class EntityIdMixIn {

    // Mix EntityId properties in its parent class for JSON only
    @JsonUnwrapped
    EntityId entityId;
}

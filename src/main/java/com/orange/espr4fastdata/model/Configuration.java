/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

/**
 * Configuration is exposed though a REST enpoint and defines the complete behavior of the CEP engine
 */
public class Configuration {

    @NotNull(message = "Configuration.host must not be empty")
    private URI host;

    @Valid
    @NotNull(message = "Configuration.in must contain a list of incoming events")
    @JsonProperty("in")
    private List<EventTypeIn> eventTypeIns;

    @Valid
    @NotNull(message = "Configuration.out must contain a list of outgoing events")
    @JsonProperty("out")
    private List<EventTypeOut> eventTypeOuts;

    @NotNull(message = "Configuration.statements must contain a list of EPL statements")
    private List<String> statements;

    public Configuration() {
    }

    public URI getHost() {
        return host;
    }

    public void setHost(URI host) {
        this.host = host;
    }

    public List<EventTypeIn> getEventTypeIns() {
        return eventTypeIns;
    }

    public void setEventTypeIns(List<EventTypeIn> eventTypeIns) {
        this.eventTypeIns = eventTypeIns;
    }

    public List<EventTypeOut> getEventTypeOuts() {
        return eventTypeOuts;
    }

    public void setEventTypeOuts(List<EventTypeOut> eventTypeOuts) {
        this.eventTypeOuts = eventTypeOuts;
    }

    public List<String> getStatements() {
        return statements;
    }

    public void setStatements(List<String> statements) {
        this.statements = statements;
    }
}

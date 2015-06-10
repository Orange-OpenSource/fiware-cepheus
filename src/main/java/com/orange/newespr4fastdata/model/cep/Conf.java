package com.orange.newespr4fastdata.model.cep;

import java.util.List;

/**
 * Created by pborscia on 03/06/2015.
 */
public class Conf {

    private String host;
    private List<EventTypeIn> eventTypeIns;
    private List<EventTypeOut> eventTypeOuts;
    private List<String> rules;

    public Conf() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
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

    public List<String> getRules() {
        return rules;
    }

    public void setRules(List<String> rules) {
        this.rules = rules;
    }
}

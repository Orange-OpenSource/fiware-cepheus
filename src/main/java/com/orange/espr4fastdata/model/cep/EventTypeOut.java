package com.orange.espr4fastdata.model.cep;

/**
 * Created by pborscia on 03/06/2015.
 */
public class EventTypeOut extends EventType {
    private String broker;

    public EventTypeOut() {
        super();
    }

    public EventTypeOut(String id, String type, boolean isPattern) {
        super(id, type, isPattern);
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }
}

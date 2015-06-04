package com.orange.newespr4fastdata.model.cep;

/**
 * Created by pborscia on 03/06/2015.
 */
public class EventTypeIn extends EventType {
    private String provider;

    public EventTypeIn() {
        super();
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}

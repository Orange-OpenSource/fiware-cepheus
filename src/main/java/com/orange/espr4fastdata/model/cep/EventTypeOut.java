/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model.cep;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pborscia on 03/06/2015.
 */
public class EventTypeOut extends EventType {
    private Set<Broker> brokers;

    public EventTypeOut() {
        super();
    }

    public EventTypeOut(String id, String type, boolean isPattern) {
        super(id, type, isPattern);
    }

    public Set<Broker> getBrokers() {
        return brokers;
    }

    public void setBrokers(Set<Broker> brokers) {
        this.brokers = brokers;
    }

    public void addBroker(Broker broker) {
        if (brokers == null) {
            brokers = new HashSet<>();
        }
        brokers.add(broker);
    }
}

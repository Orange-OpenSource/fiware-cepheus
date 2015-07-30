/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.model;

import java.util.HashSet;
import java.util.Set;

/**
 * An incoming event
 */
public class EventTypeIn extends EventType {

    private Set<Provider> providers;

    public EventTypeIn() {
        super();
    }

    public EventTypeIn(String id, String type, boolean isPattern) {
        super(id, type, isPattern);
    }

    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
    }

    public void addProvider(String providerUrl) {
        if (providers == null) {
            providers = new HashSet<>();
        }
        providers.add(new Provider(providerUrl));
    }
}

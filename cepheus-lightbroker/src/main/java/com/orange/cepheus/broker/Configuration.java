/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties to access ligth broker
 */
@Component
@ConfigurationProperties("broker")
public class Configuration {

    private String localBroker;

    private String remoteBroker;

    public Configuration(){
    }

    public String getLocalBroker() {
        return localBroker;
    }

    public void setLocalBroker(String localBroker) {
        this.localBroker = localBroker;
    }

    public String getRemoteBroker() {
        return remoteBroker;
    }

    public void setRemoteBroker(String remoteBroker) {
        this.remoteBroker = remoteBroker;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "localBroker='" + localBroker + '\'' +
                ", remoteBroker='" + remoteBroker + '\'' +
                '}';
    }
}

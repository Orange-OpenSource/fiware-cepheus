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

    //remoteHost
    @NotEmpty
    private String remoteHost;

    //remotePort
    private int remotePort;

    public Configuration() {
    }

    public Configuration(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "remoteHost='" + remoteHost + '\'' +
                ", remotePort=" + remotePort +
                '}';
    }
}

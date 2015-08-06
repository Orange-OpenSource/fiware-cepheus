package com.orange.cepheus.lb;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

/**
 * Configuration properties to access ligth broker
 */
@Component
@ConfigurationProperties("lb")
public class Configuration {

    //host
    @NotEmpty
    private String host;

    //port
    private int port;

    public Configuration() {
    }

    public Configuration(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}

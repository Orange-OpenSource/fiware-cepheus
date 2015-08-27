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
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Configuration properties to access ligth broker
 */
@Component
@ConfigurationProperties("broker")
public class Configuration {

    public static class RemoteBroker {

        /**
         * Url to the broker
         */
        private String url;

        /**
         * Fiware specific service name (optional)
         */
        private String serviceName;

        /**
         * Fiware specific service path (optional)
         */
        private String servicePath;

        /**
         * OAuth token for secured brokers
         */
        private String authToken;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServicePath() {
            return servicePath;
        }

        public void setServicePath(String servicePath) {
            this.servicePath = servicePath;
        }

        public String getAuthToken() {
            return authToken;
        }

        public void setAuthToken(String authToken) {
            this.authToken = authToken;
        }

        @Override
        public String toString() {
            return "RemoteBroker{" +
                    "url='" + url + '\'' +
                    ", serviceName='" + serviceName + '\'' +
                    ", servicePath='" + servicePath + '\'' +
                    ", authToken='" + authToken + '\'' +
                    '}';
        }
    }

    private String localBroker;

    private RemoteBroker remoteBroker;

    public Configuration() {
    }

    public String getLocalBroker() {
        return localBroker;
    }

    public void setLocalBroker(String localBroker) {
        this.localBroker = localBroker;
    }

    public RemoteBroker getRemoteBroker() {
        return remoteBroker;
    }

    public void setRemoteBroker(RemoteBroker remoteBroker) {
        this.remoteBroker = remoteBroker;
    }

    /**
     * Set custom headers for Brokers
     */
    public HttpHeaders getHeadersForBroker(HttpHeaders httpHeaders) {

        if (remoteBroker.getServiceName() != null) {
            httpHeaders.add("Fiware-Service", remoteBroker.getServiceName());
        }
        if (remoteBroker.getServicePath() != null) {
            httpHeaders.add("Fiware-ServicePath", remoteBroker.getServicePath());
        }
        if (remoteBroker.getAuthToken() != null) {
            httpHeaders.add("X-Auth-Token", remoteBroker.getAuthToken());
        }
        return httpHeaders;
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "localBroker='" + localBroker + '\'' +
                ", remoteBroker=" + remoteBroker +
                '}';
    }
}

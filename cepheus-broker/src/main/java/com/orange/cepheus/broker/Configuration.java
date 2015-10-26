/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

/**
 * Configuration properties to access ligth broker
 */
@Component
public class Configuration {

    /**
     * Url to this broker instance
     */
    @Value("${local.url}")
    private String localUrl;

    /**
     * Url to the remote broker
     */
    @Value("${remote.url:}")
    private String remoteUrl;

    /**
     * Fiware specific service name (optional)
     */
    @Value("${remote.serviceName:}")
    private String remoteServiceName;

    /**
     * Fiware specific service path (optional)
     */
    @Value("${remote.servicePath:}")
    private String remoteServicePath;

    /**
     * OAuth token for secured brokers
     */
    @Value("${remote.authToken:}")
    private String remoteAuthToken;

    /**
     * Disable forwarding updateContext requests to the remote broker
     *
     * All NGSI requests are forwarded by the Cepheus-Broker to the remote broker.
     * Except:
     *  - subscribeContext/updateContextSubscription/unsubscribeContext (handled by the app)
     *  - updateContext (if
     */
    @Value("${remote.forward.updateContext:true}")
    private boolean remoteForwardUpdateContext = true;

    public Configuration() {
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getRemoteServiceName() {
        return remoteServiceName;
    }

    public void setRemoteServiceName(String remoteServiceName) {
        this.remoteServiceName = remoteServiceName;
    }

    public String getRemoteServicePath() {
        return remoteServicePath;
    }

    public void setRemoteServicePath(String remoteServicePath) {
        this.remoteServicePath = remoteServicePath;
    }

    public String getRemoteAuthToken() {
        return remoteAuthToken;
    }

    public void setRemoteAuthToken(String remoteAuthToken) {
        this.remoteAuthToken = remoteAuthToken;
    }

    public boolean isRemoteForwardUpdateContext() {
        return remoteForwardUpdateContext;
    }

    public void setRemoteForwardUpdateContext(boolean remoteForwardUpdateContext) {
        this.remoteForwardUpdateContext = remoteForwardUpdateContext;
    }

    /*
     * Inject Orion-specific headers into the given HttpHeaders list
     * @param httpHeaders
     */
    public void addRemoteHeaders(HttpHeaders httpHeaders) {
        if (remoteServiceName != null) {
            httpHeaders.set("Fiware-Service", remoteServiceName);
        }
        if (remoteServicePath != null) {
            httpHeaders.set("Fiware-ServicePath", remoteServicePath);
        }
        if (remoteAuthToken != null) {
            httpHeaders.set("X-Auth-Token", remoteAuthToken);
        }
    }

    @Override
    public String toString() {
        return "Configuration{" +
                "localUrl='" + localUrl + '\'' +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", remoteServiceName='" + remoteServiceName + '\'' +
                ", remoteServicePath='" + remoteServicePath + '\'' +
                ", remoteAuthToken='" + remoteAuthToken + '\'' +
                ", remoteForwardUpdateContext=" + remoteForwardUpdateContext +
                '}';
    }
}

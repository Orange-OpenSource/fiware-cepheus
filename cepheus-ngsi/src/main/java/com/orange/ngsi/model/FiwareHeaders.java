/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import org.springframework.http.HttpHeaders;

/**
 * Store the Fiware headers like Fiware-Service, Fiware-ServicePath and X-Auth-Token
 */
public class FiwareHeaders {

    String fiwareService;
    String fiwarePath;
    String fiwareToken;

    public FiwareHeaders() {
    }

    public FiwareHeaders(String fiwareService, String fiwarePath, String fiwareToken) {
        this.fiwareService = fiwareService;
        this.fiwarePath = fiwarePath;
        this.fiwareToken = fiwareToken;
    }

    public String getFiwareService() {
        return fiwareService;
    }

    public void setFiwareService(String fiwareService) {
        this.fiwareService = fiwareService;
    }

    public String getFiwarePath() {
        return fiwarePath;
    }

    public void setFiwarePath(String fiwarePath) {
        this.fiwarePath = fiwarePath;
    }

    public String getFiwareToken() {
        return fiwareToken;
    }

    public void setFiwareToken(String fiwareToken) {
        this.fiwareToken = fiwareToken;
    }

    public void addToHttpHeaders(HttpHeaders httpHeaders) {
        if (fiwareService != null) {
            httpHeaders.set("Fiware-Service", fiwareService);
        }
        if (fiwarePath != null) {
            httpHeaders.set("Fiware-ServicePath", fiwarePath);
        }
        if (fiwareToken != null) {
            httpHeaders.set("X-Auth-Token", fiwareToken);
        }
    }

    @Override
    public String toString() {
        return "FiwareHeaders{" +
                "fiwareService='" + fiwareService + '\'' +
                ", fiwarePath='" + fiwarePath + '\'' +
                ", fiwareToken='" + fiwareToken + '\'' +
                '}';
    }
}

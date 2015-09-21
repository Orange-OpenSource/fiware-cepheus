/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.ObjectError;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Identify if host accept json or xml content
 */
@Component
public class Dispatcher {

    private static Logger logger = LoggerFactory.getLogger(Dispatcher.class);

    private Map<String, Boolean> jsonHost = new HashMap<>();

    /**
     * memorize the host accept json and api version
     * @param url of the host
     * @param accept of Request header
     * @param v1 true if host accept api v1 false if host accept api v2
     * @throws URISyntaxException
     */
    public void registerHost(String url, String accept, boolean v1) {
        String host = null;
        host = getHost(url);
        if (host != null) {
            if ((jsonHost.get(host) == null) && (accept.contains(MediaType.APPLICATION_JSON_VALUE))) {
                jsonHost.put(host, v1);
            }
        } else {
            logger.warn("failed to registerHost into Dispatcher cause: host is null in requestUrl {}", url);
        }
    }

    /**
     * indicate if the host support V1 Json
     * @param url of the host
     * @return true if host support api v1, null can be returned if host do not support json
     * @throws URISyntaxException
     */
    public Boolean supportV1Json(String url) {
        if (supportXml(url)) {
            return false;
        }
        return jsonHost.get(getHost(url));
    }

    /**
     * indicate if the host support V2 Json
     * @param url of the host
     * @return true if host support api v2, null can be returned if host do not support json
     * @throws URISyntaxException
     */
    public Boolean supportV2Json(String url) throws URISyntaxException {
        if (supportXml(url)) {
            return false;
        }
        return !jsonHost.get(getHost(url));
    }

    /**
     * indicate if the host support Xml
     * @param url of the host
     * @return true if host support xml else false
     * @throws URISyntaxException
     */
    public Boolean supportXml(String url) {
        String host = getHost(url);
        if (host != null) {
            return jsonHost.get(getHost(url)) == null ? true : false ;
        } else {
            logger.warn("failed to registerHost into Dispatcher cause: host is null in requestUrl {}", url);
            return true;
        }
    }

    private String getHost(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
            StringBuffer hostAndPort = new StringBuffer(uri.getHost());
            hostAndPort.append(":");
            hostAndPort.append(uri.getPort());
            return hostAndPort.toString();
        } catch (URISyntaxException e) {
            logger.warn("failed into Dispatcher cause: URISyntaxException {}", e.getMessage());
        }
        return null;
    }

}

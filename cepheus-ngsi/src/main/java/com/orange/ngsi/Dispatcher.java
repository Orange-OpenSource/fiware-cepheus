package com.orange.ngsi;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * Identify if host accept json or xml content
 */
@Component
public class Dispatcher {

    private Map<String, Boolean> jsonHost = new HashMap<>();

    /**
     * memorize the host accept json and api version
     * @param url of the host
     * @param accept of Request header
     * @param v1 true if host accept api v1 false if host accept api v2
     * @throws URISyntaxException
     */
    public void addJsonHost(String url, String accept, boolean v1) throws URISyntaxException {
        URI uri = new URI(url);
        if (accept.contains(MediaType.APPLICATION_JSON_VALUE)) {
            jsonHost.put(uri.getHost(), v1);
        }
    }

    /**
     * memorize the host accept json and api version
     * @param url of the host
     * @return true if host accept api v1 false if host accept api v2 and null if host accept xml
     * @throws URISyntaxException
     */
    public Boolean getV1(String url) throws URISyntaxException {
        URI uri = new URI(url);
        return jsonHost.get(uri.getHost());
    }

}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Created by pborscia on 08/06/2015.
 */
public class ServiceNamePathInterceptor implements ClientHttpRequestInterceptor {

    @Value("${header.fiware-service}")
    private String fiwareService;

    @Value("${header.fiware-servicePath}")
    private String fiwareServicePath;

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add("Fiware-Service", this.fiwareService);
        headers.add("Fiware-ServicePath", this.fiwareServicePath);
        return clientHttpRequestExecution.execute(httpRequest, body);
    }

}

package com.orange.newespr4fastdata.cep;

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

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] body, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        HttpHeaders headers = httpRequest.getHeaders();
        headers.add("Fiware-Service", "MyTenant");
        headers.add("Fiware-ServicePath", "/*");
        return clientHttpRequestExecution.execute(httpRequest, body);
    }
}

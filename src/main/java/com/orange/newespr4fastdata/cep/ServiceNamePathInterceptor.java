package com.orange.newespr4fastdata.cep;

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

package com.orange.espr4fastdata.cep;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.espr4fastdata.model.ngsi.SubscribeContext;
import com.orange.espr4fastdata.model.ngsi.SubscribeContextResponse;
import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;


/**
 * Send subscribeContext requests to the Context Broker
 */

@Service
@Configuration
public class SubscribeContextRequest {


    @Value("${subscribeContextRequest.defaultMaxTotalConnections}")
    private int defaultMaxTotalConnections;

    @Value("${subscribeContextRequest.defaultMaxConnectionsPerRoute}")
    private int defaultMaxConnectionsPerRoute;

    @Value("${subscribeContextRequest.defaultReadTimeoutMilliseconds}")
    private int defaultReadTimeoutMilliseconds;

    public void postMessage(SubscribeContext subscribeContext, String provider) throws URISyntaxException {

        // Set the Content-Type header, ServiceName and ServicePath
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SubscribeContext> requestEntity = new HttpEntity<>(subscribeContext, requestHeaders);

        URI providerURI = new URI(provider);

        SubscribeContextResponse subscribeContextResponse = restTemplate().postForObject(providerURI, requestEntity, SubscribeContextResponse.class);

        //TODO : treat ERROR
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory());
        return restTemplate;
    }

    @Bean
    public CloseableHttpClient httpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(defaultMaxTotalConnections);
        connectionManager
                .setDefaultMaxPerRoute(defaultMaxConnectionsPerRoute);

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(defaultReadTimeoutMilliseconds).build();

        CloseableHttpClient defaultHttpClient = HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config).build();
        return defaultHttpClient;
    }
}

package com.orange.ngsi.client;

import com.orange.espr4fastdata.exception.SubscribeContextRequestException;
import com.orange.espr4fastdata.model.ngsi.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;


/**
 * Send subscribeContext requests to the Context Broker
 */

@Service
@Configuration
public class SubscribeContextRequest {

    private static Logger logger = LoggerFactory.getLogger(SubscribeContextRequest.class);

    @Value("${subscribeContextRequest.defaultMaxTotalConnections}")
    private int defaultMaxTotalConnections;

    @Value("${subscribeContextRequest.defaultMaxConnectionsPerRoute}")
    private int defaultMaxConnectionsPerRoute;

    @Value("${subscribeContextRequest.defaultReadTimeoutMilliseconds}")
    private int defaultReadTimeoutMilliseconds;

    public SubscribeResponse postSubscribeContextRequest(SubscribeContext subscribeContext, String provider) throws URISyntaxException, SubscribeContextRequestException {

        // Set the Content-Type header, ServiceName and ServicePath
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SubscribeContext> requestEntity = new HttpEntity<>(subscribeContext, requestHeaders);

        URI providerURI = new URI(provider);

        //SubscribeContextResponse subscribeContextResponse = restTemplate().postForObject(providerURI, requestEntity, SubscribeContextResponse.class);

        try {
            ResponseEntity<SubscribeContextResponse> result = restTemplate().exchange(providerURI, HttpMethod.POST, requestEntity, SubscribeContextResponse.class);


            SubscribeContextResponse subscribeContextResponse = result.getBody();
            SubscribeError subscribeError = subscribeContextResponse.getSubscribeError();

            if (subscribeError == null) {

                 String message = "SubscribeError received: " + subscribeError.getErrorCode().getCode() + " | " + subscribeError.getErrorCode().getDetail();
                 logger.warn(message);
                 throw new SubscribeContextRequestException(message);

            }

            return result.getBody().getSubscribeResponse();

        } catch (Exception e) {

            String message = "Failed Response received: " + e.getMessage();
            logger.error(message);
            throw new SubscribeContextRequestException(message);
        }


    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory() {
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(httpRequestFactory());
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

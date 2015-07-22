package com.orange.ngsi.client;

import com.orange.espr4fastdata.exception.SubscribeContextRequestException;
import com.orange.espr4fastdata.model.ngsi.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
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

    @Autowired
    public AsyncRestTemplate asyncRestTemplate;

    public void postSubscribeContextRequest(SubscribeContext subscribeContext, String provider) throws URISyntaxException {

        // Set the Content-Type header
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SubscribeContext> requestEntity = new HttpEntity<>(subscribeContext, requestHeaders);

        URI providerURI = new URI(provider);

        ListenableFuture<ResponseEntity<SubscribeContextResponse>> futureEntity;
        futureEntity = asyncRestTemplate.exchange(providerURI, HttpMethod.POST, requestEntity, SubscribeContextResponse.class);

        futureEntity.addCallback(new ListenableFutureCallback<ResponseEntity<SubscribeContextResponse>>() {
            @Override
            public void onSuccess(ResponseEntity result) {
                logger.debug("Response received (async callable)");
                logger.debug("Status Code of SubscribeContextResponse : {} SubscribeContextResponse received : {}", result.getStatusCode(), result.getBody());

                SubscribeContextResponse subscribeContextResponse = (SubscribeContextResponse)result.getBody();
                SubscribeError subscribeError = subscribeContextResponse.getSubscribeError();

                if (subscribeError == null) {

                    String message = "SubscribeError received: " + subscribeError.getErrorCode().getCode() + " | " + subscribeError.getErrorCode().getDetail();
                    logger.warn(message);
                }
                else {

                    //TODO update timestamp and Idsubcription to Map
                }
            }

            @Override
            public void onFailure(Throwable t) {
                logger.warn("Failed Response received: {} ", t.getCause()
                        + "|" + t.getMessage());
            }
        });

    }

}

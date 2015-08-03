package com.orange.ngsi.client;

import com.orange.ngsi.model.*;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * Provides methods to request a NGSI 10 v1 component (supporting JSON)
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class NgsiClient {

    private static Logger logger = LoggerFactory.getLogger(NgsiClient.class);

    @Autowired
    public AsyncRestTemplate asyncRestTemplate;

    @Autowired
    public PoolingNHttpClientConnectionManager poolingNHttpClientConnectionManager;

    /**
     * Let some time for the client to shutdown gracefully
     * @throws IOException
     */
    public void shutdownGracefully() throws IOException {
        poolingNHttpClientConnectionManager.shutdown(10000);
    }

    /**
     * Send an update request
     * @param brokerUrl the URL of the broker
     * @param httpHeaders the HTTP header to use, or null for default
     * @param updateContext the UpdateContext
     * @param onSuccess method called on success with an UpdateContextResponse
     * @param onFailure method called on failure with a Throwable
     */
    public void updateContext(String brokerUrl, HttpHeaders httpHeaders, UpdateContext updateContext, Consumer<UpdateContextResponse> onSuccess, Consumer<Throwable> onFailure) {
        logger.debug("UpdateContextRequest: {} {}", brokerUrl, updateContext);

        if (httpHeaders == null) {
            httpHeaders = getRequestHeaders();
        }
        HttpEntity<UpdateContext> requestEntity = new HttpEntity<>(updateContext, httpHeaders);

        asyncRestTemplate.exchange(brokerUrl, HttpMethod.POST, requestEntity, UpdateContextResponse.class)
                .addCallback(new ListenableFutureCallback<ResponseEntity<UpdateContextResponse>>() {
                    @Override
                    public void onSuccess(ResponseEntity<UpdateContextResponse> result) {
                        logger.debug("UpdateContextResponse: {} {}", result.getStatusCode(), result.getBody());
                        onSuccess.accept(result.getBody());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.debug("UpdateContextResponse failure: {}", t);
                        onFailure.accept(t);
                    }
                });
    }

    /**
     * Send a subscription request
     * @param providerUrl the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscribeContext the SubscriptionContext
     * @param onSuccess method called on success with a SubscribeResponse
     * @param onFailure method called on failure with a Throwable
     */
    public void subscribeContext(String providerUrl, HttpHeaders httpHeaders, SubscribeContext subscribeContext, Consumer<SubscribeContextResponse> onSuccess, Consumer<Throwable> onFailure) {
        logger.debug("SubscribeContextRequest: {} {}", providerUrl, subscribeContext);

        if (httpHeaders == null) {
            httpHeaders = getRequestHeaders();
        }
        HttpEntity<SubscribeContext> requestEntity = new HttpEntity<>(subscribeContext, httpHeaders);

        asyncRestTemplate.exchange(providerUrl, HttpMethod.POST, requestEntity, SubscribeContextResponse.class)
                .addCallback(new ListenableFutureCallback<ResponseEntity<SubscribeContextResponse>>() {
                    @Override
                    public void onSuccess(ResponseEntity<SubscribeContextResponse> result) {
                        logger.debug("SubscribeContextResponse: {} {}", result.getStatusCode(), result.getBody());
                        onSuccess.accept(result.getBody());
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        logger.debug("SubscribeContextResponse failure: {}", t);
                        onFailure.accept(t);
                    }
                });
    }

    /**
     * Send an unsubscribe request
     * @param providerUrl the URL of the subscription provider
     * @param httpHeaders the HTTP header to use, or null for default
     * @param subscriptionId the subscription ID to unsubscribe
     * @param onSuccess method called on success with the subscription ID
     * @param onFailure method called on failure with a Throwable
     */
    public void unsubscribeContext(String providerUrl, HttpHeaders httpHeaders, String subscriptionId, Consumer<UnsubscribeContextResponse> onSuccess, Consumer<Throwable> onFailure) {
        logger.debug("UnsubscribeContextRequest: {} {}", providerUrl, subscriptionId);

        UnsubscribeContext unsubscribeContext = new UnsubscribeContext();
        unsubscribeContext.setSubscriptionId(subscriptionId);

        if (httpHeaders == null) {
            httpHeaders = getRequestHeaders();
        }
        HttpEntity<UnsubscribeContext> requestEntity = new HttpEntity<>(unsubscribeContext, httpHeaders);

        asyncRestTemplate.exchange(providerUrl, HttpMethod.POST, requestEntity, UnsubscribeContextResponse.class)
                .addCallback(new ListenableFutureCallback<ResponseEntity<UnsubscribeContextResponse>>() {
                    @Override public void onSuccess(ResponseEntity<UnsubscribeContextResponse> result) {
                        logger.debug("UnsubscribeContextResponse: {} {}", result.getStatusCode(), result.getBody());
                        onSuccess.accept(result.getBody());
                    }

                    @Override public void onFailure(Throwable t) {
                        logger.debug("UnsubscribeContextResponse failure: {}", t);
                        onFailure.accept(t);
                    }
                });
    }

    /**
     * The default HTTP request headers used for the requests.
     * @return the HTTP request headers.
     */
    public HttpHeaders getRequestHeaders() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return requestHeaders;
    }
}

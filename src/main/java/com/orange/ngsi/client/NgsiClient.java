package com.orange.ngsi.client;

import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;

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

    private HttpHeaders requestHeaders;

    /**
     * Send an update request
     * @param brokerUrl the URL of the broker
     * @param updateContext the UpdateContext
     * @param onSuccess method called on success with an UpdateContextResponse
     * @param onFailure method called on failure with a Throwable
     */
    public void updateContext(String brokerUrl, UpdateContext updateContext, Consumer<UpdateContextResponse> onSuccess, Consumer<Throwable> onFailure) {
        logger.debug("UpdateContextRequest: {} {}", brokerUrl, updateContext);

        HttpEntity<UpdateContext> requestEntity = new HttpEntity<>(updateContext, getRequestHeaders());

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
     * @param subscribeContext the SubscriptionContext
     * @param onSuccess method called on success with a SubscribeResponse
     * @param onFailure method called on failure with a Throwable
     */
    public void subscribeContext(String providerUrl, SubscribeContext subscribeContext, Consumer<SubscribeContextResponse> onSuccess, Consumer<Throwable> onFailure) {
        logger.debug("SubscribeContextRequest: {} {}", providerUrl, subscribeContext);

        HttpEntity<SubscribeContext> requestEntity = new HttpEntity<>(subscribeContext, getRequestHeaders());

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
     * @param subscriptionId the subscription ID to unsubscribe
     * @param onSuccess method called on success with the subscription ID
     * @param onFailure method called on failure with a Throwable
     */
    public void unsubscribeContext(String providerUrl, String subscriptionId, Consumer<UnsubscribeContextResponse> onSuccess, Consumer<Throwable> onFailure) {
        logger.debug("UnsubscribeContextRequest: {} {}", providerUrl, subscriptionId);

        UnsubscribeContext unsubscribeContext = new UnsubscribeContext();
        unsubscribeContext.setSubscriptionId(subscriptionId);

        HttpEntity<UnsubscribeContext> requestEntity = new HttpEntity<>(unsubscribeContext, getRequestHeaders());

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
     * The HTTP request headers used for the requests.
     * @return the HTTP request headers.
     */
    public HttpHeaders getRequestHeaders() {
        if (requestHeaders == null) {
            requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        }
        return requestHeaders;
    }
}

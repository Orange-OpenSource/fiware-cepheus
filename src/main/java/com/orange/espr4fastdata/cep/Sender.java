package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.model.cep.Broker;
import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import com.orange.espr4fastdata.model.ngsi.UpdateContextResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by pborscia on 08/06/2015.
 */
@Service
public class Sender {

    private static Logger logger = LoggerFactory.getLogger(Sender.class);

    private AsyncRestTemplate restTemplate;

    @Value("${sender.readtimeout}")
    private int readTimeout;

    @Value("${sender.connectTimeout}")
    private int connectTimeout;

    public Sender() {
        restTemplate = new AsyncRestTemplate(this.clientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        //TODO replace by authent interceptor
        //restTemplate.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new ServiceNamePathInterceptor()));

    }

    public UpdateContextResponse postMessage(UpdateContext updateContext, Broker broker) {


        UpdateContextResponse updateContextResponse = null;
        try {

            // Set the Content-Type header, ServiceName and ServicePath
            HttpHeaders requestHeaders = new HttpHeaders();
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("Fiware-Service", broker.getServiceName());
            requestHeaders.add("Fiware-ServicePath", broker.getServicePath());

            HttpEntity<UpdateContext> requestEntity = new HttpEntity<UpdateContext>(updateContext, requestHeaders);

            ListenableFuture<ResponseEntity<UpdateContextResponse>> futureEntity = restTemplate.exchange(broker.getUrl(), HttpMethod.POST,requestEntity,UpdateContextResponse.class);

            futureEntity
                    .addCallback(new ListenableFutureCallback<ResponseEntity>() {
                        @Override
                        public void onSuccess(ResponseEntity result) {
                            logger.debug("Response received (async callable)");
                            logger.debug("Status Code of UpdateContextResponse : {} UpdateContextResponse received : {}", result.getStatusCode(), result.getBody());

                            //TODO : anything else ??
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            logger.warn("Failed Response received: {} ", t.getCause()
                                    + "|" + t.getMessage());
                            //TODO : anything else ??
                        }
                    });



        } catch (HttpStatusCodeException e) {
            logger.warn("POST FAILED with HttpStatusCode: {} ", e.getStatusCode()
                    + "|" + e.getStatusText());
        } catch (RuntimeException e) {
            logger.error("POST FAILED {}",e);

        }
        return updateContextResponse;
    }

    public AsyncRestTemplate getRestTemplate(){
        return this.restTemplate;
    }

    private AsyncClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory();
        factory.setReadTimeout(2000);
        factory.setConnectTimeout(2000);
        factory.setConnectionRequestTimeout(2000);

        return factory;
    }
}

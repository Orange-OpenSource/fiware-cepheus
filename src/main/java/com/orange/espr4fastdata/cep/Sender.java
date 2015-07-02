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
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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

    private RestTemplate restTemplate;

    @Value("${sender.readtimeout}")
    private int readTimeout;

    @Value("${sender.connectTimeout}")
    private int connectTimeout;

    public Sender() {
        restTemplate = new RestTemplate(this.clientHttpRequestFactory());
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

            ResponseEntity<UpdateContextResponse> responseEntity = restTemplate.exchange(broker.getUrl(), HttpMethod.POST,requestEntity,UpdateContextResponse.class);

            logger.debug("Status Code of UpdateContextResponse : {} from broker : {}", responseEntity.getStatusCode(), broker.getUrl());

            logger.debug("UpdateContextResponse received {} ", responseEntity.getBody());

        } catch (HttpStatusCodeException e) {
            logger.warn("POST FAILED with HttpStatusCode: {} ", e.getStatusCode()
                    + "|" + e.getStatusText());
        } catch (RuntimeException e) {
            logger.error("POST FAILED {}",e);

        }
        return updateContextResponse;
    }

    public RestTemplate getRestTemplate(){
        return this.restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(this.readTimeout);
        factory.setConnectTimeout(this.connectTimeout);
        return factory;
    }
}

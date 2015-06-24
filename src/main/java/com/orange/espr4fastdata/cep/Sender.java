package com.orange.espr4fastdata.cep;

import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import com.orange.espr4fastdata.model.ngsi.UpdateContextResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Created by pborscia on 08/06/2015.
 */
@ComponentScan
public class Sender {

    private static Logger logger = LoggerFactory.getLogger(Sender.class);

    RestTemplate restTemplate;

    @Value("${sender.readtimeout}")
    private int readTimeout;

    @Value("${sender.connectTimeout}")
    private int connectTimeout;

    public Sender() {
        restTemplate = new RestTemplate(this.clientHttpRequestFactory());
        restTemplate.setInterceptors(Collections.<ClientHttpRequestInterceptor>singletonList(new ServiceNamePathInterceptor()));

    }

    public UpdateContextResponse postMessage(UpdateContext updateContext, String brokerURI) {


        UpdateContextResponse updateContextResponse = null;
        try {

            updateContextResponse = restTemplate.postForObject(brokerURI,updateContext,UpdateContextResponse.class);

            logger.info("UpdateContextResponse received {} ", updateContextResponse);

        } catch (HttpStatusCodeException e) {
            logger.warn("POST FAILED with HttpStatusCode: {} ", e.getStatusCode()
                    + "|" + e.getStatusText());
        } catch (RuntimeException e) {
            logger.error("POST FAILED ");

        }
        return updateContextResponse;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(this.readTimeout);
        factory.setConnectTimeout(this.connectTimeout);
        return factory;
    }
}

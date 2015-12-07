/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import org.springframework.context.annotation.*;

/**
 * Default configuration used when multi-tenant is disabled.
 * In this configuration, the following beans are singletons.
 */
@Configuration
@Profile("!multi-tenant")
public class DefaultConfiguration {

    @Bean
    public Init init() {
        return new Init();
    }

    @Bean
    ComplexEventProcessor complexEventProcessor() {
        return new EsperEventProcessor();
    }

    @Bean
    EventMapper eventMapper() {
        return new EventMapper();
    }

    @Bean
    EventSinkListener eventSinkListener() {
        return new EventSinkListener();
    }

    @Bean
    SubscriptionManager subscriptionManager() {
        return new SubscriptionManager();
    }
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.tenant;

import com.orange.cepheus.cep.*;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.*;

/**
 * Multi-tenant configuration.
 * In this configuration, the beans defined in the default configuration become "tenant scoped".
 * In addition, a tenant filter is injected for each request to add a tenant context holding the beans (see TenantScope).
 */
@Configuration
@Profile("multi-tenant")
public class TenantConfiguration {

    /**
     * Declare the "tenant" scope.
     */
    @Bean
    public static CustomScopeConfigurer customScopeConfigurer (TenantScope tenantScope) {
        CustomScopeConfigurer configurer = new CustomScopeConfigurer ();
        configurer.addScope("tenant", tenantScope);
        return configurer;
    }

    /**
     * Declare the tenant Filter to inject tenant scoped beans in requests.
     */
    @Bean
    public TenantFilter tenantFilter() {
        return new TenantFilter();
    }

    /**
     * The tenant scope.
     */
    @Bean
    public TenantScope tenantScope() {
        return new TenantScope();
    }

    /**
     * This bean will initially load the beans for each tenant.
     */
    @Bean
    public TenantInit tenantInit() {
        return new TenantInit();
    }

    /*
     * The beans depending on the tenant scope
     */

    @Bean
    @Scope(value = "tenant", proxyMode = ScopedProxyMode.TARGET_CLASS)
    ComplexEventProcessor complexEventProcessor() {
        return new EsperEventProcessor();
    }

    @Bean
    @Scope(value = "tenant", proxyMode = ScopedProxyMode.TARGET_CLASS)
    EventMapper eventMapper() {
        return new EventMapper();
    }

    @Bean
    @Scope(value = "tenant", proxyMode = ScopedProxyMode.TARGET_CLASS)
    EventSinkListener eventSinkListener() {
        return new EventSinkListener();
    }

    @Bean
    @Scope(value = "tenant", proxyMode = ScopedProxyMode.TARGET_CLASS)
    SubscriptionManager subscriptionManager() {
        return new SubscriptionManager();
    }
}

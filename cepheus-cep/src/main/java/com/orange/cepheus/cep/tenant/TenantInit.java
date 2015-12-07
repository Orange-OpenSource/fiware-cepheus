/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.tenant;

import com.orange.cepheus.cep.ComplexEventProcessor;
import com.orange.cepheus.cep.EventMapper;
import com.orange.cepheus.cep.Init;
import com.orange.cepheus.cep.SubscriptionManager;
import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.cepheus.cep.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * This Bean is created to load a the persisted Configurations
 * into the Complex Event Processor on startup for each tenant.
 */
@Component
@Profile("multi-tenant")
public class TenantInit {

    private static Logger logger = LoggerFactory.getLogger(TenantInit.class);

    @Autowired
    protected ApplicationContext applicationContext;

    @Autowired
    Persistence persistence;

    @Autowired
    TenantFilter tenantFilter;

    @PostConstruct
    protected void loadConfigurationOnStartup() {

        persistence.listConfigurations().forEach(id -> {

            logger.info("Loading configuration for tenant '{}'", id);

            // Force Tenant scope context to get the correct beans
            tenantFilter.forceTenantScope(id);

            // Get beans needing the configuration
            EventMapper eventMapper = (EventMapper) applicationContext.getBean("eventMapper");
            ComplexEventProcessor complexEventProcessor = (ComplexEventProcessor) applicationContext.getBean("complexEventProcessor");
            SubscriptionManager subscriptionManager = (SubscriptionManager) applicationContext.getBean("subscriptionManager");

            // Try restoring the persisted configuration if any
            try {
                Configuration configuration = persistence.loadConfiguration(id);
                eventMapper.setConfiguration(configuration);
                complexEventProcessor.setConfiguration(configuration);
                subscriptionManager.setConfiguration(configuration);
            } catch (PersistenceException | ConfigurationException e) {
                logger.error("Failed to load or apply persisted configuration {}", id, e);
            }

            // Remove tenant scope
            tenantFilter.forceTenantScope(null);
        });
    }
}

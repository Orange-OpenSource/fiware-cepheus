/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.NamedThreadLocal;

import java.util.HashMap;

/**
 * Custom scope to associate beans to the "tenant" scope.
 * It also associates the tenant context to the current thread.
 */
public class TenantScope implements Scope {

    private static Logger logger = LoggerFactory.getLogger(TenantScope.class);

    /**
     * A tenant context is a simple map to hold beans and tenantID
     */
    public static class Context extends HashMap<String, Object> {};

    /**
     * Tenant contexts are stored in the current thread for each requests.
     */
    private static final ThreadLocal<Context> tenantHolder = new NamedThreadLocal<>("tenantHolder");

    /**
     * Associate the tenant context to current thread
     * @param tenantContext
     */
    public static void storeTenantContext(Context tenantContext) {
        tenantHolder.set(tenantContext);
    }

    /**
     * Reset the tenant context associated to the current thread
     */
    public static void resetTenant() {
        tenantHolder.remove();
    }

    /**
     * Get the bean associated to the current tenant context or create it from objectFactory
     * @param name the name of the bean
     * @param objectFactory the bean factory
     * @return the bean
     */
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Object o = tenantHolder.get().get(name);
        if (o == null) {
            o = objectFactory.getObject();
            tenantHolder.get().put(name, o);
        }
        //logger.error("SCOPE: get {} / {}", name, o);
        return o;
    }

    @Override
    public Object remove(String name) {
        return tenantHolder.get().remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // ignored
    }

    @Override
    public Object resolveContextualObject(String key) {
        return tenantHolder.get().get(key);
    }

    /**
     * Return the tenant ID.
     * @return the tenant id
     */
    @Override
    public String getConversationId() {
        return (String) tenantHolder.get().get(TenantFilter.TENANT_ID);
    }

    /**
     * @return the Fiware-Service tenant information
     */
    public String getService() {
        return (String) tenantHolder.get().get(TenantFilter.FIWARE_SERVICE);
    }

    /**
     * @return the Fiware-ServicePath tenant information
     */
    public String getServicePath() {
        return (String) tenantHolder.get().get(TenantFilter.FIWARE_SERVICE_PATH);
    }
}

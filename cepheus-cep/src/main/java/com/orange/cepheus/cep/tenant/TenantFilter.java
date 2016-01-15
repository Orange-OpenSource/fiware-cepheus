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
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Custom filter to associate Http request to a tenant
 * defined by Fiware-Service and Fiware-ServicePath headers.
 */
public class TenantFilter implements Filter {
    public static final String FIWARE_SERVICE = "Fiware-Service";
    public static final String FIWARE_SERVICE_PATH = "Fiware-ServicePath";
    public static final String TENANT_ID = "tenantID";

    public static final String DEFAULT_SERVICE = "default";
    public static final String DEFAULT_SERVICE_PATH = "/";

    public static final String DEFAULT_TENANTID = tenantIdFromService(DEFAULT_SERVICE, DEFAULT_SERVICE_PATH);

    /**
     * Generate the tenantId from the Service/ServicePath
     * @param service
     * @param servicePath
     * @return the tenantId
     */
    public static String tenantIdFromService(String service, String servicePath) {
        return service + servicePath;
    }

    private static Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    private static class BadHeaderException extends Exception {
        public BadHeaderException(String message) {
            super(message);
        }
    };

    /**
     * Map of all the context for each tenant, key: tenantId, a concatenation of service and servicePath
     */
    private final ConcurrentMap<String, TenantScope.Context> tenantContexts = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * For each request, initialize the tenant (default when no
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        try {
            // Extract the service / servicePath headers
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String service = getService(httpServletRequest);
            String servicePath = getServicePath(httpServletRequest);

            // Associate the tenant context to the current thread
            TenantScope.storeTenantContext(getTenantContext(service, servicePath));

            // Continue request
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (BadHeaderException e) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\""+e.getMessage()+"\"}");
            response.getWriter().flush();
        }
    }

    @Override
    public void destroy() {
    }

    /**
     * Remove the tenant context associated to a tenantId
     * @param tenantId the ID of the tenant to remove
     */
    public void removeTenant(String tenantId) {
        tenantContexts.remove(tenantId);
    }

    /**
     * This method used to force a given Tenant-scoped context for the current thread
     * to allow IoC to retrieve Tenant scoped beans.
     * This must never be called in a HttpServletRequest context !
     * @param tenantId id of the tenant, or null to reset context.
     */
    public void forceTenantScope(String tenantId) {
        if (tenantId == null) {
            TenantScope.resetTenant();
            return;
        }

        // Extract service and servicePath from tenantId
        int slashPos = tenantId.indexOf("/");
        String service = tenantId.substring(0, slashPos);
        String servicePath = tenantId.substring(slashPos);

        // Associate the tenant context to the current thread
        TenantScope.storeTenantContext(getTenantContext(service, servicePath));
    }

    /**
     * Return a tenant context for the given service / servicePath
     * @param service
     * @param servicePath
     * @return a tenant context
     */
    private TenantScope.Context getTenantContext(String service, String servicePath) {
        String tenantId = tenantIdFromService(service, servicePath);

        TenantScope.Context tenantMap = tenantContexts.get(tenantId);
        if (tenantMap == null) {
            synchronized (this) {
                tenantMap = tenantContexts.get(tenantId);
                if (tenantMap == null) {
                    tenantMap = new TenantScope.Context();
                    tenantMap.put(TENANT_ID, tenantId);
                    if (!DEFAULT_SERVICE.equals(service)) {
                        tenantMap.put(FIWARE_SERVICE, service);
                    }
                    if (!DEFAULT_SERVICE_PATH.equals(servicePath)) {
                        tenantMap.put(FIWARE_SERVICE_PATH, servicePath);
                    }
                    tenantContexts.put(tenantId, tenantMap);
                }
            }
        }
        return tenantMap;
    }

    /**
     * Extract the Fiware-Service from request
     * @param httpServletRequest
     * @return the Service or default
     */
    private String getService(HttpServletRequest httpServletRequest) throws BadHeaderException {
        String service = httpServletRequest.getHeader(FIWARE_SERVICE);
        if (service == null) {
            service = DEFAULT_SERVICE;
        } else if (!service.matches("^[\\w]+$")) {
            throw new BadHeaderException("Fiware-Service header can only contain [A-Za-z0-9_] characters");
        }
        return service;
    }

    /**
     * Extract the Fiware-ServicePath from request
     * @param httpServletRequest
     * @return the ServicePath or default
     */
    private String getServicePath(HttpServletRequest httpServletRequest) throws BadHeaderException {
        String servicePath = httpServletRequest.getHeader(FIWARE_SERVICE_PATH);
        if (servicePath == null) {
            servicePath = DEFAULT_SERVICE_PATH;
        } else if (!servicePath.matches("^/[\\w/]*$")) {
            throw new BadHeaderException("Fiware-ServicePath must only start with a / and contain [A-Za-z0-9_/] characters");
        }
        return servicePath;
    }
}

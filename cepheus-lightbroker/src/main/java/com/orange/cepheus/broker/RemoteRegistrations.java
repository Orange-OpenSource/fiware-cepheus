/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.RegisterContext;
import com.orange.ngsi.model.RegisterContextResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Propagate the registrations to the remote broker.
 *
 * On registration error, the registrations are re-scheduled later.
 */
@Component
public class RemoteRegistrations {

    private static Logger logger = LoggerFactory.getLogger(RemoteRegistration.class);

    @Autowired
    protected Configuration configuration;

    @Autowired
    protected NgsiClient ngsiClient;

    /**
     * Keep track of remote registrations.
     */
    private class RemoteRegistration {

        /**
         * Registration ID on the remote broker
         */
        String registrationId;

        /**
         * Keeps the context to register to remote broker for retries.
         * Will be null once a successful registration is achieved.
         */
        RegisterContext registerContext;
    }

    /**
     * Map local registrationId to a RemoteRegistration
     */
    private Map<String, RemoteRegistration> registrations = new ConcurrentHashMap<>();

    /**
     * Try propagating the registerContext to a remote broker
     * @param registerContext the registerContext to send
     * @param localRegistrationId the local registrationId
     */
    public void registerContext(final RegisterContext registerContext, final String localRegistrationId) {

        // When no remote broker is define, don't do anything.
        if ((configuration.getRemoteBroker() == null) || (configuration.getRemoteBroker().getUrl() == null) || (configuration.getRemoteBroker().getUrl().isEmpty())) {
            return;
        }
        String remoteBroker = configuration.getRemoteBroker().getUrl();

        logger.debug("registering {} to remote broker {}", localRegistrationId, remoteBroker);

        // If we already had a remote registration, reset its registerContext
        String previousRemoteRegistrationId = resetRemoteRegistration(localRegistrationId);
        // Update the registerContext with the previous remote registrationId if any
        registerContext.setRegistrationId(previousRemoteRegistrationId);

        ngsiClient.registerContext(remoteBroker, null, registerContext).addCallback(
                result -> {
                    String remoteRegistrationId = result.getRegistrationId();
                    boolean error = result.getErrorCode() != null || remoteRegistrationId == null;
                    if (error) {
                        logger.warn("failed to register {} to remote broker (will retry later) with error {}", localRegistrationId, result.getErrorCode());
                    } else {
                        logger.debug("successfully registered {} to remote broker ({})", localRegistrationId, result.getRegistrationId());
                    }
                    // On error, keep registerContext for future retry
                    updateRemoteRegistration(localRegistrationId, remoteRegistrationId, error ? registerContext : null);
                },
                ex -> {
                    logger.warn("failed to register {} to remote broker (will retry later) with error {}", localRegistrationId, ex.toString());
                    updateRemoteRegistration(localRegistrationId, null, registerContext);
                });
    }

    /**
     * Scheduled task that will retry failed remote registrations.
     * For each remote registration, that still have a non null registerContext, retry the registration.
     */
    @Scheduled(fixedDelay = 60000)
    public void registerPendingRemoteRegistrations() {
        registrations.forEach((localRegistrationId, remoteRegistration) -> {
            RegisterContext registerContext = remoteRegistration.registerContext;
            if (registerContext != null) {
                registerContext(registerContext, localRegistrationId);
            }
        });
    }

    /**
     * Find the remote registrationId corresponding to a local registrationId
     * @param localRegistrationId the local registrationId
     * @return the remote registrationId or null
     */
    public String getRemoteRegistrationId(String localRegistrationId) {
        RemoteRegistration remoteRegistration = registrations.get(localRegistrationId);
        return remoteRegistration != null ? remoteRegistration.registrationId : null;
    }

    /**
     * Remove a remote registration associated to a local registrationId
     * @param localRegistrationId the local registrationId
     */
    public synchronized void removeRegistration(String localRegistrationId) {
        registrations.remove(localRegistrationId);
    }

    /**
     * Reset any RemoteRegistration associated to the local registrationId (remove the registerContext)
     * @param localRegistrationId the local registrationId
     * @return the previous remote registrationId, or null
     */
    private synchronized String resetRemoteRegistration(String localRegistrationId) {
        RemoteRegistration registration = registrations.get(localRegistrationId);
        if (registration != null) {
            registration.registerContext = null;
            return registration.registrationId;
        }
        return null;
    }

    /**
     * Update the remote registration data associated to a local registration id
     * @param localRegistrationId the local registrationId
     * @param remoteRegistrationId the remote registrationId to update
     * @param registerContext the registerContext to update
     */
    private synchronized void updateRemoteRegistration(String localRegistrationId, String remoteRegistrationId, RegisterContext registerContext) {
        RemoteRegistration registration = registrations.get(localRegistrationId);
        if (registration == null) {
            registration = new RemoteRegistration();
            registrations.put(localRegistrationId, registration);
        }
        registration.registrationId = remoteRegistrationId;
        registration.registerContext = registerContext;
    }
}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.cepheus.broker.exception.RegistrationException;
import com.orange.ngsi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.xml.datatype.DatatypeFactory;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Maintains the list of all context registrations
 */
@Component
public class LocalRegistrations {

    /**
     * All registrations updates are forwarded to the remote broker
     */
    @Autowired
    protected RemoteRegistrations remoteRegistrations;

    @Autowired
    private Patterns patterns;

    /**
     * List of all context registrations
     */
    Map<String, RegisterContext> registrations = new ConcurrentHashMap<>();

    /**
     * Add or update a new context registration.
     * When the duration of the context is set to zero, this is handled as a remove.
     * @param registerContext
     * @return contextRegistrationId
     */
    public String updateRegistrationContext(RegisterContext registerContext) throws RegistrationException {
        Duration duration = registrationDuration(registerContext);
        String registrationId = registerContext.getRegistrationId();

        // Handle a zero duration as a special remove operation
        if (duration.isZero() && registrationId != null) {
            registrations.remove(registrationId);
            remoteRegistrations.removeRegistration(registrationId);
            return registrationId;
        }

        // Compile all entity patterns now to check for conformance (result is cached for later use)
        try {
            registerContext.getContextRegistrationList().forEach(c -> c.getEntityIdList().forEach(patterns::getPattern));
        } catch (PatternSyntaxException e) {
            throw new RegistrationException("bad pattern", e);
        }

        // Generate a registration id if none was provided or if it does not refer to an existing registration
        if (registrationId == null || registrations.get(registrationId) == null) {
            registrationId = UUID.randomUUID().toString();
            registerContext.setRegistrationId(registrationId);
        }

        // Set the expiration date
        registerContext.setExpirationDate(Instant.now().plus(duration));

        registrations.put(registrationId, registerContext);

        // Forward to remote broker
        remoteRegistrations.registerContext(registerContext, registrationId);

        return registrationId;
    }

    /**
     * Retrieve a registration (warning: might be expired !)
     * @param registrationId the id of the registration
     * @return the corresponding registration or null if not found
     */
    public RegisterContext getRegistration(String registrationId) {
        return registrations.get(registrationId);
    }

    /**
     * Find the providingApplication of a context element
     * @param searchEntityId the entity id to search
     * @param searchAttributes the attributes to search
     * @return list of matching providing applications
     */
    public Iterator<URI> findProvidingApplication(EntityId searchEntityId, Set<String> searchAttributes) {

        // Filter out expired registrations
        Predicate<RegisterContext> filterExpired = registerContext -> registerContext.getExpirationDate().isAfter(Instant.now());

        // Filter only matching entity ids
        Predicate<EntityId> filterEntityId = patterns.getFilterEntityId(searchEntityId);

        // Only filter by attributes if search is looking for them
        final boolean noAttributes = searchAttributes == null || searchAttributes.size() == 0;

        // Filter each registration (remove expired) and return its providing application
        // if at least one of its listed entities matches the searched context element
        // and if all searched attributes are defined in the registration (if any)
        return registrations.values().stream()
                .filter(filterExpired).map(RegisterContext::getContextRegistrationList)
                .flatMap(List::stream)
                .filter(c -> c.getEntityIdList().stream().filter(filterEntityId).findFirst().isPresent()
                        && (noAttributes || allContextRegistrationAttributes(c).containsAll(searchAttributes)))
                .map(ContextRegistration::getProvidingApplication).iterator();
    }

    /**
     * @return all the names of the attributes of a context registration
     */
    private Collection<String> allContextRegistrationAttributes(ContextRegistration contextRegistration) {
        return contextRegistration.getContextRegistrationAttributeList().stream().map(ContextRegistrationAttribute::getName).collect(Collectors.toList());
    }

    /**
     * Removed expired registrations every min
     */
    @Scheduled(fixedDelay = 60000)
    public void purgeExpiredContextRegistrations() {
        final Instant now = Instant.now();
        registrations.forEach((registrationId, registerContext) -> {
            if (registerContext.getExpirationDate().isBefore(now)) {
                registrations.remove(registrationId);
                remoteRegistrations.removeRegistration(registrationId);
            }
        });
    }

    /**
     * @return the duration of the registration
     * @throws RegistrationException
     */
    private Duration registrationDuration(RegisterContext registerContext) throws RegistrationException {
        // Use java.xml.datatype functions as java.time do not handle durations with months and years...
        try {
            long duration = DatatypeFactory.newInstance().newDuration(registerContext.getDuration()).getTimeInMillis(new Date());
            return Duration.ofMillis(duration);
        } catch (Exception e) {
            throw new RegistrationException("bad duration: "+registerContext.getDuration(), e);
        }
    }
}

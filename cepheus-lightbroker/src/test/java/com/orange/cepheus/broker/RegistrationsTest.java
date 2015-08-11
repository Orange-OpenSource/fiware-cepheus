/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.ngsi.model.ContextRegistration;
import com.orange.ngsi.model.ContextRegistrationAttribute;
import com.orange.ngsi.model.EntityId;
import com.orange.ngsi.model.RegisterContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for registrations management
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RegistrationsTest {

    @Autowired
    Registrations registrations;

    @Test
    public void testRegistration() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        String registrationId = registrations.addContextRegistration(registerContext);
        Assert.hasLength(registrationId);
        assertNotNull(registrations.getRegistration(registrationId));
        assertNotNull(registerContext.getExpirationDate());
    }

    @Test
    public void testUnregisterWithZeroDuration() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        String registrationId = registrations.addContextRegistration(registerContext);

        // Remove registration using a zero duration
        RegisterContext zeroDuration = createRegistrationContext();
        zeroDuration.setRegistrationId(registrationId);
        zeroDuration.setDuration("PT0S");
        registrations.addContextRegistration(zeroDuration);

        Assert.isNull(registrations.getRegistration(registrationId));
    }

    @Test
    public void testRegistrationBadDuration() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        registerContext.setDuration("PIPO");

        //TODO expect does not work on inner Exception
        try {
            registrations.addContextRegistration(registerContext);
            fail("registration should fail on bad duration with RegistrationException");
        } catch (Registrations.RegistrationException ex) {
        }
    }

    @Test
    public void testRegistrationBadPattern() throws Exception {
        // Set the entity id to a bad pattern
        RegisterContext registerContext = createRegistrationContext();
        registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).setId("]|,\\((");
        registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).setIsPattern(true);

        //TODO expect does not work on inner Exception
        try {
            registrations.addContextRegistration(registerContext);
            fail("registration should fail on bad pattern with RegistrationException");
        } catch (Registrations.RegistrationException ex) {
        }
    }

    @Test
    public void testRegistrationPurge() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        registerContext.setDuration("PT1S"); // 1 s only
        String registrationId = registrations.addContextRegistration(registerContext);

        // Wait for expiration
        Thread.sleep(1500);

        // Force trigger of scheduled purge
        registrations.purgeExpiredContextRegistrations();

        assertNull(registrations.getRegistration(registrationId));
    }

    @Test
    public void testFindEntityId() throws Exception {
        // Insert 3 registrations
        for (String n : new String[]{"A", "B", "C"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n, "temp"));
        }

        // Find B
        EntityId searchedEntityId = new EntityId("B", "string", false);
        Iterator<URI> it = registrations.findProvidingApplication(searchedEntityId, null);
        assertTrue(it.hasNext());
        assertEquals("http://B", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIds() throws Exception {
        // Insert 3 registrations
        for (String n : new String[]{"A", "B", "C"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n, "temp"));
        }
        // Insert 3 more
        for (String n : new String[]{"A", "B", "C"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n+"2", "temp"));
        }

        // Find the two B
        EntityId searchedEntityId = new EntityId("B", "string", false);
        List<String> results = new LinkedList<>();
        registrations.findProvidingApplication(searchedEntityId, null).forEachRemaining(uri -> results.add(uri.toString()));
        Collections.sort(results);
        assertEquals(2, results.size());
        assertEquals("http://B", results.get(0));
        assertEquals("http://B2", results.get(1));
    }

    @Test
    public void testFindEntityIdPattern() throws Exception {
        // Insert 3 registrations
        for (String n : new String[]{"A", "B", "C"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n, "temp"));
        }

        // Find A and B
        EntityId searchedEntityId = new EntityId("A|B", "string", true);
        List<String> results = new LinkedList<>();
        registrations.findProvidingApplication(searchedEntityId, null).forEachRemaining(uri -> results.add(uri.toString()));
        Collections.sort(results);
        assertEquals(2, results.size());
        assertEquals("http://A", results.get(0));
        assertEquals("http://B", results.get(1));
    }

    @Test
    public void testFindEntyIdAndAttribute() throws Exception {
        // Insert 3 registrations
        for (String n : new String[]{"A", "B", "C"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n, "temp"+n));
        }
        // Add other A entities but with other attributes
        registrations.addContextRegistration(createRegistrationContext("A", "string", false, "http://AB", "tempB"));
        registrations.addContextRegistration(createRegistrationContext("A", "string", false, "http://AC", "tempC"));

        // Find only entity A with attr tempA
        EntityId searchedEntityId = new EntityId("A", "string", false);
        Set<String> attribute = Collections.singleton("tempA");
        Iterator<URI> it = registrations.findProvidingApplication(searchedEntityId, attribute);

        assertTrue(it.hasNext());
        assertEquals("http://A", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntyIdAndAttributes() throws Exception {
        // Insert 2 registrations only temp2 attr
        for (String n : new String[]{"A", "B"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n, "temp2"));
        }
        // Insert 1 registration with both temp2 & temp3 attrs
        for (String n : new String[]{"C"}) {
            RegisterContext registerContext = createRegistrationContext(n, "string", false, "http://"+n, "temp"+n);
            List<ContextRegistrationAttribute> attrs = new LinkedList<>();
            attrs.add(new ContextRegistrationAttribute("temp2", false));
            attrs.add(new ContextRegistrationAttribute("temp3", false));
            registerContext.getContextRegistrationList().get(0).setContextRegistrationAttributeList(attrs);
            registrations.addContextRegistration(registerContext);
        }
        // Insert 2 registrations only temp3
        for (String n : new String[]{"D", "E"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n, "temp3"));
        }

        // Find only entity with temp2 and temp3
        EntityId searchedEntityId = new EntityId(".*", "string", true);
        Set<String> attributes = new HashSet<>();
        Collections.addAll(attributes, "temp2", "temp3");
        Iterator<URI> it = registrations.findProvidingApplication(searchedEntityId, attributes);
        assertTrue(it.hasNext());
        assertEquals("http://C", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIdNoMatch() throws Exception {
        // Insert 3 registrations
        for (String n : new String[]{"A", "B", "C"}) {
            registrations.addContextRegistration(createRegistrationContext(n, "string", false, "http://"+n, "temp"));
        }

        EntityId searchedEntityId = new EntityId("D", "string", false);
        Iterator<URI> it = registrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("B", "wrongtype", false);
        it = registrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIdExpired() throws Exception {
        // Insert 3 registrations with short expiration
        for (String n : new String[]{"A", "B", "C"}) {
            RegisterContext registerContext = createRegistrationContext(n, "string", false, "http://"+n, "temp");
            registerContext.setDuration("PT1S");
            registrations.addContextRegistration(registerContext);
        }

        // Wait for expiration
        Thread.sleep(1500);

        EntityId searchedEntityId = new EntityId("A", "string", false);
        Iterator<URI> it = registrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("B", "string", false);
        it = registrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("C", "string", false);
        it = registrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());
    }

    private RegisterContext createRegistrationContext() throws Exception {
        return createRegistrationContext("SENSOR1", "string", false, "http://providingApplication", "temp");
    }

    private RegisterContext createRegistrationContext(String entityId, String entityType, boolean entityIsPattern, String providingApp, String attr) throws Exception {
        RegisterContext registerContext = new RegisterContext();
        registerContext.setDuration("PT1M");

        ContextRegistration contextRegistration = new ContextRegistration();
        contextRegistration.setEntityIdList(Collections.singletonList(new EntityId(entityId, entityType, entityIsPattern)));
        contextRegistration.setContextRegistrationAttributeList(Collections.singletonList(new ContextRegistrationAttribute(attr, false)));
        contextRegistration.setProvidingApplication(new URI(providingApp));
        registerContext.setContextRegistrationList(Collections.singletonList(contextRegistration));

        return registerContext;
    }
}

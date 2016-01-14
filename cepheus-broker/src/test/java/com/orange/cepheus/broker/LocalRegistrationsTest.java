/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.cepheus.broker.exception.RegistrationException;
import com.orange.cepheus.broker.exception.RegistrationPersistenceException;
import com.orange.cepheus.broker.model.Registration;
import com.orange.cepheus.broker.persistence.RegistrationsRepository;
import com.orange.ngsi.model.ContextRegistrationAttribute;
import com.orange.ngsi.model.EntityId;
import com.orange.ngsi.model.RegisterContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.orange.cepheus.broker.Util.*;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for localRegistrations management
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocalRegistrationsTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Mock
    protected RemoteRegistrations remoteRegistrations;

    @Mock
    protected RegistrationsRepository registrationsRepository;

    @Autowired
    @InjectMocks
    protected LocalRegistrations localRegistrations;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        //setup registrationsRepository mock to respond the registration not exists in to the database
        when(registrationsRepository.getRegistration(any())).thenThrow(EmptyResultDataAccessException.class);
    }

    @After
    public void resetMocks() {
        reset(remoteRegistrations);
        reset(registrationsRepository);
    }

    @Test
    public void testRegistration() throws Exception {

        RegisterContext registerContext = createRegistrationContext();
        String registrationId = localRegistrations.updateRegistrationContext(registerContext, null);
        Assert.hasLength(registrationId);
        Registration registration = localRegistrations.getRegistration(registrationId);
        assertNotNull(registration);
        assertNotNull(registration.getExpirationDate());

        verify(remoteRegistrations).registerContext(eq(registerContext), eq(registrationId), eq(null));
        verify(registrationsRepository).getRegistration(eq(registrationId));
        verify(registrationsRepository).saveRegistration(eq(registration));
    }

    @Test
    public void testRegistrationWithPersistenceException() throws Exception {

        thrown.expect(RegistrationPersistenceException.class);
        doThrow(RegistrationPersistenceException.class).when(registrationsRepository).saveRegistration(any());

        RegisterContext registerContext = createRegistrationContext();
        String registrationId = localRegistrations.updateRegistrationContext(registerContext, null);
        Assert.hasLength(registrationId);
        Registration registration = localRegistrations.getRegistration(registrationId);
        assertNotNull(registration);
        assertNotNull(registration.getExpirationDate());

        verify(remoteRegistrations, never()).registerContext(eq(registerContext), eq(registrationId), null);
        verify(registrationsRepository).getRegistration(eq(registrationId));
        verify(registrationsRepository).saveRegistration(eq(registration));
    }

    @Test
    public void testUpdateRegistration() throws Exception {

        RegisterContext registerContext = createRegistrationContext();
        registerContext.setRegistrationId("12345");

        Registration registration = new Registration(Instant.now().plus(1, ChronoUnit.DAYS), registerContext);

        reset(registrationsRepository);
        when(registrationsRepository.getRegistration(any())).thenReturn(registration);

        String registrationId = localRegistrations.updateRegistrationContext(registerContext, null);
        Assert.hasLength(registrationId);
        assertNotEquals("12345", registrationId);
        Registration registration2 = localRegistrations.getRegistration(registrationId);
        assertNotNull(registration2);
        assertNotNull(registration2.getExpirationDate());

        verify(remoteRegistrations).registerContext(eq(registerContext), eq(registrationId), eq(null));
        verify(registrationsRepository).getRegistration(eq(registrationId));
        verify(registrationsRepository).updateRegistration(eq(registration));
    }

    @Test
    public void testUnregisterWithZeroDuration() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        String registrationId = localRegistrations.updateRegistrationContext(registerContext, null);

        // Remove registration using a zero duration
        RegisterContext zeroDuration = createRegistrationContext();
        zeroDuration.setRegistrationId(registrationId);
        zeroDuration.setDuration("PT0S");
        localRegistrations.updateRegistrationContext(zeroDuration, null);

        Assert.isNull(localRegistrations.getRegistration(registrationId));

        verify(remoteRegistrations).removeRegistration(registrationId);
        verify(registrationsRepository).removeRegistration(eq(registrationId));
    }

    @Test
    public void testUnregisterWithZeroDurationWithPersistenceException() throws Exception {
        thrown.expect(RegistrationPersistenceException.class);
        doThrow(RegistrationPersistenceException.class).when(registrationsRepository).removeRegistration(any());

        RegisterContext registerContext = createRegistrationContext();
        String registrationId = localRegistrations.updateRegistrationContext(registerContext, null);

        // Remove registration using a zero duration
        RegisterContext zeroDuration = createRegistrationContext();
        zeroDuration.setRegistrationId(registrationId);
        zeroDuration.setDuration("PT0S");
        localRegistrations.updateRegistrationContext(zeroDuration, null);

        Assert.isNull(localRegistrations.getRegistration(registrationId));

        verify(remoteRegistrations, never()).removeRegistration(registrationId);
        verify(registrationsRepository).removeRegistration(eq(registrationId));
    }

    @Test
    public void test1MonthDuration() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        registerContext.setDuration("P1M");
        localRegistrations.updateRegistrationContext(registerContext, null);

        Calendar c = (Calendar) Calendar.getInstance().clone();
        c.add(Calendar.MONTH, 1);
        c.add(Calendar.HOUR, 24);
        Registration registration = localRegistrations.getRegistration(registerContext.getRegistrationId());
        assertFalse(registration.getExpirationDate().isAfter(c.toInstant()));
        c.add(Calendar.HOUR, -48);
        assertFalse(registration.getExpirationDate().isBefore(c.toInstant()));
    }

    @Test
    public void testRegistrationBadDuration() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        registerContext.setDuration("PIPO");

        //TODO expect does not work on inner Exception
        try {
            localRegistrations.updateRegistrationContext(registerContext, null);
            fail("registration should fail on bad duration with RegistrationException");
        } catch (RegistrationException ex) {
        }

        verify(remoteRegistrations, never()).registerContext(any(), any(), eq(null));
    }

    @Test
    public void testRegistrationBadPattern() throws Exception {
        // Set the entity id to a bad pattern
        RegisterContext registerContext = createRegistrationContext();
        registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).setId("]|,\\((");
        registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).setIsPattern(true);

        //TODO expect does not work on inner Exception
        try {
            localRegistrations.updateRegistrationContext(registerContext, null);
            fail("registration should fail on bad pattern with RegistrationException");
        } catch (RegistrationException ex) {
        }

        verify(remoteRegistrations, never()).registerContext(any(), any(), eq(null));
    }

    @Test
    public void testRegistrationPurge() throws Exception {
        RegisterContext registerContext = createRegistrationContext();
        registerContext.setDuration("PT1S"); // 1 s only
        String registrationId = localRegistrations.updateRegistrationContext(registerContext, null);

        // Wait for expiration
        Thread.sleep(1500);

        // Force trigger of scheduled purge
        localRegistrations.purgeExpiredContextRegistrations();

        assertNull(localRegistrations.getRegistration(registrationId));

        verify(remoteRegistrations).removeRegistration(registrationId);
    }

    @Test
    public void testRegistrationPurgeWithPersistenceException() throws Exception {

        doThrow(RegistrationPersistenceException.class).when(registrationsRepository).removeRegistration(any());

        RegisterContext registerContext = createRegistrationContext();
        registerContext.setDuration("PT1S"); // 1 s only
        String registrationId = localRegistrations.updateRegistrationContext(registerContext, null);

        // Wait for expiration
        Thread.sleep(1500);

        // Force trigger of scheduled purge
        localRegistrations.purgeExpiredContextRegistrations();

        assertNull(localRegistrations.getRegistration(registrationId));

        verify(remoteRegistrations).removeRegistration(registrationId);
        verify(registrationsRepository).removeRegistration(registrationId);
    }

    @Test
    public void testFindEntityId() throws Exception {
        // Insert 3 localRegistrations
        for (String n : new String[]{"A", "B", "C"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n, "temp"), null);
        }

        // Find B
        EntityId searchedEntityId = new EntityId("B", "string", false);
        Iterator<URI> it = localRegistrations.findProvidingApplication(searchedEntityId, null);
        assertTrue(it.hasNext());
        assertEquals("http://B", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIds() throws Exception {
        // Insert 3 localRegistrations
        for (String n : new String[]{"A", "B", "C"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n, "temp"), null);
        }
        // Insert 3 more
        for (String n : new String[]{"A", "B", "C"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n + "2", "temp"), null);
        }

        // Find the two B
        EntityId searchedEntityId = new EntityId("B", "string", false);
        List<String> results = new LinkedList<>();
        localRegistrations.findProvidingApplication(searchedEntityId, null).forEachRemaining(uri -> results.add(uri.toString()));
        Collections.sort(results);
        assertEquals(2, results.size());
        assertEquals("http://B", results.get(0));
        assertEquals("http://B2", results.get(1));
    }

    @Test
    public void testFindEntityIdPattern() throws Exception {
        // Insert 3 localRegistrations
        for (String n : new String[]{"A", "B", "C"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n, "temp"), null);
        }

        // Find A and B
        EntityId searchedEntityId = new EntityId("A|B", "string", true);
        List<String> results = new LinkedList<>();
        localRegistrations.findProvidingApplication(searchedEntityId, null).forEachRemaining(uri -> results.add(uri.toString()));
        Collections.sort(results);
        assertEquals(2, results.size());
        assertEquals("http://A", results.get(0));
        assertEquals("http://B", results.get(1));
    }

    @Test
    public void testFindEntyIdAndAttribute() throws Exception {
        // Insert 3 localRegistrations
        for (String n : new String[]{"A", "B", "C"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n, "temp" + n), null);
        }
        // Add other A entities but with other attributes
        localRegistrations.updateRegistrationContext(createRegistrationContext("A", "string", false, "http://AB", "tempB"), null);
        localRegistrations.updateRegistrationContext(createRegistrationContext("A", "string", false, "http://AC", "tempC"), null);

        // Find only entity A with attr tempA
        EntityId searchedEntityId = new EntityId("A", "string", false);
        Set<String> attribute = Collections.singleton("tempA");
        Iterator<URI> it = localRegistrations.findProvidingApplication(searchedEntityId, attribute);

        assertTrue(it.hasNext());
        assertEquals("http://A", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntyIdAndAttributes() throws Exception {
        // Insert 2 localRegistrations only temp2 attr
        for (String n : new String[]{"A", "B"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n, "temp2"), null);
        }
        // Insert 1 registration with both temp2 & temp3 attrs
        for (String n : new String[]{"C"}) {
            RegisterContext registerContext = createRegistrationContext(n, "string", false, "http://"+n, "temp"+n);
            List<ContextRegistrationAttribute> attrs = new LinkedList<>();
            attrs.add(new ContextRegistrationAttribute("temp2", false));
            attrs.add(new ContextRegistrationAttribute("temp3", false));
            registerContext.getContextRegistrationList().get(0).setContextRegistrationAttributeList(attrs);
            localRegistrations.updateRegistrationContext(registerContext, null);
        }
        // Insert 2 localRegistrations only temp3
        for (String n : new String[]{"D", "E"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n, "temp3"), null);
        }

        // Find only entity with temp2 and temp3
        EntityId searchedEntityId = new EntityId(".*", "string", true);
        Set<String> attributes = new HashSet<>();
        Collections.addAll(attributes, "temp2", "temp3");
        Iterator<URI> it = localRegistrations.findProvidingApplication(searchedEntityId, attributes);
        assertTrue(it.hasNext());
        assertEquals("http://C", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIdNoMatch() throws Exception {
        // Insert 3 localRegistrations
        for (String n : new String[]{"A", "B", "C"}) {
            localRegistrations.updateRegistrationContext(createRegistrationContext(n, "string", false, "http://" + n, "temp"), null);
        }

        EntityId searchedEntityId = new EntityId("D", "string", false);
        Iterator<URI> it = localRegistrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("B", "wrongtype", false);
        it = localRegistrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIdExpired() throws Exception {
        // Insert 3 localRegistrations with short expiration
        for (String n : new String[]{"A", "B", "C"}) {
            RegisterContext registerContext = createRegistrationContext(n, "string", false, "http://"+n, "temp");
            registerContext.setDuration("PT1S");
            localRegistrations.updateRegistrationContext(registerContext, null);
        }

        // Wait for expiration
        Thread.sleep(1500);

        EntityId searchedEntityId = new EntityId("A", "string", false);
        Iterator<URI> it = localRegistrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("B", "string", false);
        it = localRegistrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("C", "string", false);
        it = localRegistrations.findProvidingApplication(searchedEntityId, null);
        assertFalse(it.hasNext());
    }
}

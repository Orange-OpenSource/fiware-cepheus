/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */
package com.orange.cepheus.broker;

import com.orange.cepheus.broker.exception.SubscriptionException;
import com.orange.ngsi.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static com.orange.cepheus.broker.Util.createRegistrationContext;
import static com.orange.cepheus.broker.Util.createSubscribeContext;
import static com.orange.cepheus.broker.Util.createSubscribeContextTemperature;
import static org.junit.Assert.*;

/**
 * Tests for localRegistrations management
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubscriptionsTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Autowired
    public Subscriptions subscriptions;

    @Test
    public void addSubscriptionTest() throws URISyntaxException, SubscriptionException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        String subscriptionId = subscriptions.addSubscription(subscribeContext);

        Assert.notNull(subscriptionId);
        Assert.hasLength(subscriptionId);
        Assert.notNull(subscriptions.getSubscription(subscriptionId));
        Assert.notNull(subscribeContext.getExpirationDate());
    }

    @Test
    public void addSubscriptionWithNegativeDurationTest() throws SubscriptionException, URISyntaxException {
        thrown.expect(SubscriptionException.class);
        thrown.expectMessage("negative duration is not allowed");
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setDuration("-PT10S");
        subscriptions.addSubscription(subscribeContext);
    }

    @Test
    public void addSubscriptionWithBadDurationTest() throws SubscriptionException, URISyntaxException {
        thrown.expect(SubscriptionException.class);
        thrown.expectMessage("bad duration: PIPO");
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setDuration("PIPO");
        subscriptions.addSubscription(subscribeContext);
    }

    @Test
    public void addSubscriptionWithZeroDurationTest() throws SubscriptionException, URISyntaxException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setDuration("PT0S");
        String subscriptionId = subscriptions.addSubscription(subscribeContext);
        Assert.notNull(subscriptionId);
        Assert.hasLength(subscriptionId);
        Assert.notNull(subscriptions.getSubscription(subscriptionId));
        Assert.notNull(subscribeContext.getExpirationDate());
        Instant now = Instant.now();
        Instant after = now.plus(31, ChronoUnit.DAYS).plus(10, ChronoUnit.MINUTES);
        Instant before = now.plus(30, ChronoUnit.DAYS).minus(10, ChronoUnit.MINUTES);
        assertFalse(subscribeContext.getExpirationDate().isAfter(after));
        assertFalse(subscribeContext.getExpirationDate().isBefore(before));
    }

    @Test
    public void addSubscriptionWithBadPatternTest() throws SubscriptionException, URISyntaxException {
        thrown.expect(SubscriptionException.class);
        thrown.expectMessage("bad pattern");
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.getEntityIdList().get(0).setId("]|,\\((");
        subscribeContext.getEntityIdList().get(0).setIsPattern(true);

        subscriptions.addSubscription(subscribeContext);
    }

    @Test
    public void deleteExistSubscriptions() throws URISyntaxException, SubscriptionException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        String subscriptionId = subscriptions.addSubscription(subscribeContext);
        UnsubscribeContext unsubscribeContext = new UnsubscribeContext(subscriptionId);
        assertTrue(subscriptions.deleteSubscription(unsubscribeContext));
    }

    @Test
    public void deleteNotExistSubscriptions() throws URISyntaxException, SubscriptionException {
        UnsubscribeContext unsubscribeContext = new UnsubscribeContext("12345");
        assertFalse(subscriptions.deleteSubscription(unsubscribeContext));
    }

    @Test
    public void purgeExpiredSubscriptionsTest() throws URISyntaxException, SubscriptionException, InterruptedException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setDuration("PT1S"); // 1s only
        String subscriptionId = subscriptions.addSubscription(subscribeContext);

        Thread.sleep(1500);

        subscriptions.purgeExpiredSubscriptions();
        assertNull(subscriptions.getSubscription(subscriptionId));
    }

    @Test
    public void testFindEntityId() throws Exception {
        // Insert 3 subscriptions
        for (String n : new String[]{"A", "B", "C"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n, "temp"));
        }
        // Find B
        EntityId searchedEntityId = new EntityId("B", "string", false);
        Iterator<URI> it = subscriptions.findReferences(searchedEntityId, null);
        assertTrue(it.hasNext());
        assertEquals("http://B", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIds() throws Exception {
        // Insert 3 subscriptions
        for (String n : new String[]{"A", "B", "C"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n, "temp"));
        }
        // Insert 3 more
        for (String n : new String[]{"A", "B", "C"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n + "2", "temp"));
        }

        // Find the two B
        EntityId searchedEntityId = new EntityId("B", "string", false);
        List<String> results = new LinkedList<>();
        subscriptions.findReferences(searchedEntityId, null).forEachRemaining(uri -> results.add(uri.toString()));
        Collections.sort(results);
        assertEquals(2, results.size());
        assertEquals("http://B", results.get(0));
        assertEquals("http://B2", results.get(1));
    }

    @Test
    public void testFindEntityIdPattern() throws Exception {
        // Insert 3 localRegistrations
        for (String n : new String[]{"A", "B", "C"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n, "temp"));
        }

        // Find A and B
        EntityId searchedEntityId = new EntityId("A|B", "string", true);
        List<String> results = new LinkedList<>();
        subscriptions.findReferences(searchedEntityId, null).forEachRemaining(uri -> results.add(uri.toString()));
        Collections.sort(results);
        assertEquals(2, results.size());
        assertEquals("http://A", results.get(0));
        assertEquals("http://B", results.get(1));
    }

    @Test
    public void testFindEntityIdPattern2() throws Exception {
        // Insert 3 subscriptions
        subscriptions.addSubscription(createSubscribeContext("A|B", "string", true, "http://" + "AB", "temp"));
        for (String n : new String[]{"C", "D"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n, "temp"));
        }

        // Find A and B
        EntityId searchedEntityId = new EntityId("A", "string", false);
        List<String> results = new LinkedList<>();
        subscriptions.findReferences(searchedEntityId, null).forEachRemaining(uri -> results.add(uri.toString()));
        Collections.sort(results);
        assertEquals(1, results.size());
        assertEquals("http://AB", results.get(0));
    }

    @Test
    public void testFindEntyIdAndAttributes() throws Exception {
        // Insert 2 subscriptions only temp2 attr
        for (String n : new String[]{"A", "B"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n, "temp2"));
        }
        // Insert 1 subscription with both temp2 & temp3 attrs
        for (String n : new String[]{"C"}) {
            SubscribeContext subscribeContext = createSubscribeContext(n, "string", false, "http://" + n, "temp" + n);
            List<String> attrs = new LinkedList<>();
            attrs.add("temp2");
            attrs.add("temp3");
            subscribeContext.setAttributeList(attrs);
            subscriptions.addSubscription(subscribeContext);
        }
        // Insert 2 subscriptions only temp3
        for (String n : new String[]{"D", "E"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n, "temp3"));
        }

        // Find only entity with temp2 and temp3
        EntityId searchedEntityId = new EntityId(".*", "string", true);
        Set<String> attributes = new HashSet<>();
        Collections.addAll(attributes, "temp2", "temp3");
        Iterator<URI> it = subscriptions.findReferences(searchedEntityId, attributes);
        assertTrue(it.hasNext());
        assertEquals("http://C", it.next().toString());
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIdNoMatch() throws Exception {
        // Insert 3 subscriptions
        for (String n : new String[]{"A", "B", "C"}) {
            subscriptions.addSubscription(createSubscribeContext(n, "string", false, "http://" + n, "temp"));
        }

        EntityId searchedEntityId = new EntityId("D", "string", false);
        Iterator<URI> it = subscriptions.findReferences(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("B", "wrongtype", false);
        it = subscriptions.findReferences(searchedEntityId, null);
        assertFalse(it.hasNext());
    }

    @Test
    public void testFindEntityIdExpired() throws Exception {
        // Insert 3 localRegistrations with short expiration
        for (String n : new String[]{"A", "B", "C"}) {
            SubscribeContext subscribeContext = createSubscribeContext(n, "string", false, "http://" + n, "temp");
            subscribeContext.setDuration("PT1S");
            subscriptions.addSubscription(subscribeContext);
        }

        // Wait for expiration
        Thread.sleep(1500);

        EntityId searchedEntityId = new EntityId("A", "string", false);
        Iterator<URI> it = subscriptions.findReferences(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("B", "string", false);
        it = subscriptions.findReferences(searchedEntityId, null);
        assertFalse(it.hasNext());

        searchedEntityId = new EntityId("C", "string", false);
        it = subscriptions.findReferences(searchedEntityId, null);
        assertFalse(it.hasNext());
    }
}

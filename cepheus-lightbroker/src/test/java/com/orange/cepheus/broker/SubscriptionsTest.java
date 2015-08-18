/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */
package com.orange.cepheus.broker;

import com.orange.cepheus.broker.exception.SubscriptionException;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.UnsubscribeContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.orange.cepheus.broker.Util.createSubscribeContextTemperature;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

}

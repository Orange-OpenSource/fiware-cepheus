/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.persistence;

import com.orange.cepheus.broker.Application;
import com.orange.cepheus.broker.exception.SubscriptionPersistenceException;
import com.orange.cepheus.broker.model.Subscription;
import com.orange.ngsi.model.SubscribeContext;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.orange.cepheus.broker.Util.createSubscribeContextTemperature;

/**
 * Tests for SubscriptionsRepository
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestPropertySource(locations="classpath:test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubscriptionsRepositoryTest {

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Autowired
    SubscriptionsRepository subscriptionsRepository;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Before
    public void init() throws SQLException {
        jdbcTemplate.execute("delete from t_subscriptions");
    }

    @Test
    public void saveSubscriptionTest() throws URISyntaxException, SubscriptionPersistenceException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("12345", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        subscriptionsRepository.saveSubscription(subscription);
        Map<String, Subscription> subscriptions = subscriptionsRepository.getAllSubscriptions();
        Assert.assertEquals(1, subscriptions.size());
        Assert.assertEquals(subscription.getSubscriptionId(), subscriptions.get("12345").getSubscriptionId());
        Assert.assertEquals(subscription.getExpirationDate(), subscriptions.get("12345").getExpirationDate());
        Assert.assertEquals(subscribeContext.getDuration(), subscriptions.get("12345").getSubscribeContext().getDuration());
    }

    @Test
    public void saveSubscriptionWithExceptionTest() throws URISyntaxException, SubscriptionPersistenceException {
        thrown.expect(SubscriptionPersistenceException.class);
        Subscription subscription = new Subscription();
        subscription.setSubscribeContext(new SubscribeContext());
        subscriptionsRepository.saveSubscription(subscription);
    }

    @Test
    public void saveSubscriptionWithDuplicateKeyExceptionTest() throws URISyntaxException, SubscriptionPersistenceException {
        thrown.expect(SubscriptionPersistenceException.class);
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("12345", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        subscriptionsRepository.saveSubscription(subscription);
        subscriptionsRepository.saveSubscription(subscription);
    }

    @Test
    public void updateSubscriptionTest() throws URISyntaxException, SubscriptionPersistenceException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("12345", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        subscriptionsRepository.saveSubscription(subscription);
        Map<String, Subscription> subscriptions = subscriptionsRepository.getAllSubscriptions();
        Assert.assertEquals(1, subscriptions.size());
        Assert.assertEquals("P1M", subscriptions.get("12345").getSubscribeContext().getDuration());
        subscribeContext.setDuration("PT1D");
        subscription.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.updateSubscription(subscription);
        subscriptions = subscriptionsRepository.getAllSubscriptions();
        Assert.assertEquals(1, subscriptions.size());
        Assert.assertEquals("PT1D", subscriptions.get("12345").getSubscribeContext().getDuration());
        Assert.assertEquals(subscription.getExpirationDate(), subscriptions.get("12345").getExpirationDate());
    }

    @Test
    public void updateSubscriptionWithExceptionTest() throws URISyntaxException, SubscriptionPersistenceException {
        thrown.expect(SubscriptionPersistenceException.class);
        Subscription subscription = new Subscription();
        subscription.setSubscribeContext(new SubscribeContext());
        subscriptionsRepository.updateSubscription(subscription);
    }

    @Test
    public void removeSubscriptionTest() throws URISyntaxException, SubscriptionPersistenceException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("12345", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        subscriptionsRepository.saveSubscription(subscription);
        Assert.assertEquals(1, subscriptionsRepository.getAllSubscriptions().size());
        subscriptionsRepository.removeSubscription("12345");
        Assert.assertEquals(0, subscriptionsRepository.getAllSubscriptions().size());
    }

    @Test
    public void getAllSubscriptionsTest() throws URISyntaxException, SubscriptionPersistenceException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        Subscription subscription = new Subscription("12345", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext);
        subscriptionsRepository.saveSubscription(subscription);
        SubscribeContext subscribeContext2 = createSubscribeContextTemperature();
        Subscription subscription2 = new Subscription("12346", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext2);
        subscriptionsRepository.saveSubscription(subscription2);
        SubscribeContext subscribeContext3 = createSubscribeContextTemperature();
        Subscription subscription3 = new Subscription("12347", Instant.now().plus(1, ChronoUnit.DAYS), subscribeContext3);
        subscriptionsRepository.saveSubscription(subscription3);
        Assert.assertEquals(3, subscriptionsRepository.getAllSubscriptions().size());
    }

}

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orange.cepheus.broker.Application;
import com.orange.ngsi.model.SubscribeContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

    @Autowired
    SubscriptionsRepository subscriptionsRepository;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Before
    public void init() throws SQLException {
        jdbcTemplate.execute("delete from t_subscriptions");
    }

    @Test
    public void saveSubscriptionTest() throws URISyntaxException, JsonProcessingException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setSubscriptionId("12345");
        subscribeContext.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.saveSubscription("12345", subscribeContext);
        Map<String, SubscribeContext> subscriptions = subscriptionsRepository.getAllSubscriptions();
        Assert.assertEquals(1, subscriptions.size());
        Assert.assertEquals(subscribeContext.getSubscriptionId(), subscriptions.get("12345").getSubscriptionId());
        Assert.assertEquals(subscribeContext.getExpirationDate(), subscriptions.get("12345").getExpirationDate());
        Assert.assertEquals(subscribeContext.getDuration(), subscriptions.get("12345").getDuration());
    }

    @Test
    public void updateSubscriptionTest() throws URISyntaxException, JsonProcessingException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setSubscriptionId("12345");
        subscribeContext.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.saveSubscription("12345", subscribeContext);
        Map<String, SubscribeContext> subscriptions = subscriptionsRepository.getAllSubscriptions();
        Assert.assertEquals(1, subscriptions.size());
        Assert.assertEquals("P1M", subscriptions.get("12345").getDuration());
        subscribeContext.setDuration("PT1D");
        subscribeContext.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.updateSubscription("12345", subscribeContext);
        subscriptions = subscriptionsRepository.getAllSubscriptions();
        Assert.assertEquals(1, subscriptions.size());
        Assert.assertEquals("PT1D", subscriptions.get("12345").getDuration());
        Assert.assertEquals(subscribeContext.getExpirationDate(), subscriptions.get("12345").getExpirationDate());
    }

    @Test
    public void removeSubscriptionTest() throws URISyntaxException, JsonProcessingException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setSubscriptionId("12345");
        subscribeContext.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.saveSubscription("12345", subscribeContext);
        Assert.assertEquals(1, subscriptionsRepository.getAllSubscriptions().size());
        subscriptionsRepository.removeSubscription("12345");
        Assert.assertEquals(0, subscriptionsRepository.getAllSubscriptions().size());
    }

    @Test
    public void getAllSubscriptionsTest() throws URISyntaxException, JsonProcessingException {
        SubscribeContext subscribeContext = createSubscribeContextTemperature();
        subscribeContext.setSubscriptionId("12345");
        subscribeContext.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.saveSubscription("12345", subscribeContext);
        SubscribeContext subscribeContext2 = createSubscribeContextTemperature();
        subscribeContext2.setSubscriptionId("12346");
        subscribeContext2.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.saveSubscription("12346", subscribeContext);
        SubscribeContext subscribeContext3 = createSubscribeContextTemperature();
        subscribeContext3.setSubscriptionId("12347");
        subscribeContext3.setExpirationDate(Instant.now().plus(1, ChronoUnit.DAYS));
        subscriptionsRepository.saveSubscription("12347", subscribeContext);
        Assert.assertEquals(3, subscriptionsRepository.getAllSubscriptions().size());
    }

}

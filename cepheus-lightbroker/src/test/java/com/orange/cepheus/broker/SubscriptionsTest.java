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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.net.URISyntaxException;

import static com.orange.cepheus.broker.Util.createSubscribeContextTemperature;

/**
 * Tests for localRegistrations management
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubscriptionsTest {

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
}

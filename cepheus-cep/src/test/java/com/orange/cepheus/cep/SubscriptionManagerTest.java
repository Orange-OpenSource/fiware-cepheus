/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.orange.cepheus.Application;
import com.orange.cepheus.model.Configuration;
import com.orange.cepheus.model.EventTypeIn;
import com.orange.cepheus.model.Provider;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.SubscribeContextResponse;
import com.orange.ngsi.model.SubscribeResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static com.orange.cepheus.Util.*;

/**
 * Tests for SubscriptionManager
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class SubscriptionManagerTest {

    @Mock
    TaskScheduler taskScheduler;

    @Mock
    NgsiClient ngsiClient;

    @Autowired
    @InjectMocks
    SubscriptionManager subscriptionManager;

    @Before
    public void setUp() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void after() {
        subscriptionManager.setConfiguration(emptyConfiguration());
        reset(ngsiClient);
        reset(taskScheduler);
    }

    @Test
    public void setConfigurationOK() {

        // Mock the task scheduler and capture the runnable
        ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
        when(taskScheduler.scheduleWithFixedDelay(runnableArg.capture(), anyLong())).thenReturn(Mockito.mock(ScheduledFuture.class));

        Configuration configuration = getBasicConf();
        subscriptionManager.setConfiguration(configuration);

        // Execute scheduled runnable
        runnableArg.getValue().run();

        ArgumentCaptor<String> urlProviderArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscribeContext> subscribeContextArg = ArgumentCaptor.forClass(SubscribeContext.class);
        ArgumentCaptor<Consumer> onSuccessArg = ArgumentCaptor.forClass(Consumer.class);

        verify(ngsiClient, times(1)).subscribeContext(urlProviderArg.capture(), eq(null), subscribeContextArg.capture(), onSuccessArg.capture(),
                any(Consumer.class));

        SubscribeContext subscribeContext = subscribeContextArg.getValue();
        assertEquals("S.*", subscribeContext.getEntityIdList().get(0).getId());
        assertEquals("TempSensor", subscribeContext.getEntityIdList().get(0).getType());
        assertEquals(true, subscribeContext.getEntityIdList().get(0).getIsPattern());
        assertEquals("temp", subscribeContext.getAttributeList().get(0));
        assertEquals("PT1H", subscribeContext.getDuration());
        assertEquals("http://iotAgent", urlProviderArg.getValue());

        // Call success callback
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        subscribeResponse.setSubscriptionId("12345678");
        subscribeResponse.setDuration("PT1H");
        SubscribeContextResponse response = new SubscribeContextResponse();
        response.setSubscribeResponse(subscribeResponse);
        onSuccessArg.getValue().accept(response);

        Set<Provider> providers = configuration.getEventTypeIns().get(0).getProviders();
        for(Provider provider: providers) {
            assertEquals("12345678", provider.getSubscriptionId());
            assertNotNull(provider.getSubscriptionDate());
        }
    }

    @Test
    public void testUnsubscribeOnEventTypeRemoval() {

        // Mock the task scheduler and capture the runnable
        ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
        when(taskScheduler.scheduleWithFixedDelay(runnableArg.capture(), anyLong())).thenReturn(Mockito.mock(ScheduledFuture.class));

        Configuration configuration = getBasicConf();
        subscriptionManager.setConfiguration(configuration);

        // Execute scheduled runnable
        runnableArg.getValue().run();

        ArgumentCaptor<Consumer> onSuccessArg = ArgumentCaptor.forClass(Consumer.class);
        verify(ngsiClient, times(1)).subscribeContext(eq("http://iotAgent"), eq(null), any(), onSuccessArg.capture(), any(Consumer.class));

        // Call success callback
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        subscribeResponse.setSubscriptionId("12345678");
        subscribeResponse.setDuration("PT1H");
        SubscribeContextResponse response = new SubscribeContextResponse();
        response.setSubscribeResponse(subscribeResponse);
        onSuccessArg.getValue().accept(response);

        // Set a configuration without the eventType
        Configuration emptyConfiguration = new Configuration();
        emptyConfiguration.setEventTypeIns(Collections.emptyList());
        subscriptionManager.setConfiguration(emptyConfiguration);

        // Check that unsubsribe is called when a later configuration removed the event type
        verify(ngsiClient, times(1)).unsubscribeContext(eq("http://iotAgent"), eq(null), eq("12345678"), any(), any());

    }

    @Test
    public void testUnsubscribeOnProviderRemoval() {

        // Mock the task scheduler and capture the runnable
        ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
        when(taskScheduler.scheduleWithFixedDelay(runnableArg.capture(), anyLong())).thenReturn(Mockito.mock(ScheduledFuture.class));

        Configuration configuration = getBasicConf();
        subscriptionManager.setConfiguration(configuration);

        // Execute scheduled runnable
        runnableArg.getValue().run();

        ArgumentCaptor<Consumer> onSuccessArg = ArgumentCaptor.forClass(Consumer.class);
        verify(ngsiClient, times(1)).subscribeContext(eq("http://iotAgent"), eq(null), any(), onSuccessArg.capture(), any(Consumer.class));

        // Call success callback
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        subscribeResponse.setSubscriptionId("12345678");
        subscribeResponse.setDuration("PT1H");
        SubscribeContextResponse response = new SubscribeContextResponse();
        response.setSubscribeResponse(subscribeResponse);
        onSuccessArg.getValue().accept(response);

        Configuration emptyConfiguration = getBasicConf();
        emptyConfiguration.getEventTypeIns().get(0).setProviders(Collections.emptySet());
        subscriptionManager.setConfiguration(emptyConfiguration);

        // Check that unsubsribe is called when a later configuration removed the event type
        verify(ngsiClient, times(1)).unsubscribeContext(eq("http://iotAgent"), eq(null), eq("12345678"), any(), any());
    }
}

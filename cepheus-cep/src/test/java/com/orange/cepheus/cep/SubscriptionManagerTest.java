/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.orange.cepheus.cep.model.Configuration;
import com.orange.cepheus.cep.model.Provider;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.SubscribeContextResponse;
import com.orange.ngsi.model.SubscribeResponse;
import com.orange.ngsi.model.UnsubscribeContextResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static com.orange.cepheus.cep.Util.*;

/**
 * Tests for SubscriptionManager
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SubscriptionManagerTest {

    @Mock
    TaskScheduler taskScheduler;

    @Mock
    NgsiClient ngsiClient = Mockito.mock(NgsiClient.class, RETURNS_SMART_NULLS);

    @Autowired
    @InjectMocks
    SubscriptionManager subscriptionManager;

    @Before
    public void setUp() throws URISyntaxException {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void after() {
        reset(ngsiClient);
        reset(taskScheduler);
    }

    @Test
    public void setConfigurationOK() throws Exception {

        // Mock the task scheduler and capture the runnable
        ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
        when(taskScheduler.scheduleWithFixedDelay(runnableArg.capture(), anyLong())).thenReturn(Mockito.mock(ScheduledFuture.class));

        // Mock the response to the subsribeContext
        ArgumentCaptor<SuccessCallback> successArg = ArgumentCaptor.forClass(SuccessCallback.class);
        ListenableFuture<SubscribeContextResponse> responseFuture = Mockito.mock(ListenableFuture.class);
        doNothing().when(responseFuture).addCallback(successArg.capture(), any());

        Configuration configuration = getBasicConf();
        subscriptionManager.setConfiguration(configuration);

        // Capture the arg of subscription and return the mocked future
        ArgumentCaptor<String> urlProviderArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<SubscribeContext> subscribeContextArg = ArgumentCaptor.forClass(SubscribeContext.class);
        when(ngsiClient.subscribeContext(urlProviderArg.capture(), eq(null), subscribeContextArg.capture())).thenReturn(responseFuture);

        // Execute scheduled runnable
        runnableArg.getValue().run();

        // Return the SubscribeContextResponse
        callSuccessCallback(successArg);

        SubscribeContext subscribeContext = subscribeContextArg.getValue();
        assertEquals("S.*", subscribeContext.getEntityIdList().get(0).getId());
        assertEquals("TempSensor", subscribeContext.getEntityIdList().get(0).getType());
        assertEquals(true, subscribeContext.getEntityIdList().get(0).getIsPattern());
        assertEquals("temp", subscribeContext.getAttributeList().get(0));
        assertEquals("PT1H", subscribeContext.getDuration());
        assertEquals("http://iotAgent", urlProviderArg.getValue());

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

        // Mock the response to the subsribeContext
        ArgumentCaptor<SuccessCallback> successArg = ArgumentCaptor.forClass(SuccessCallback.class);
        ListenableFuture<SubscribeContextResponse> responseFuture = Mockito.mock(ListenableFuture.class);
        doNothing().when(responseFuture).addCallback(successArg.capture(), any());

        // Return the mocked future on subscription
        when(ngsiClient.subscribeContext(any(), any(), any())).thenReturn(responseFuture);

        Configuration configuration = getBasicConf();
        subscriptionManager.setConfiguration(configuration);

        // Execute scheduled runnable
        runnableArg.getValue().run();

        // Return the SubscribeContextResponse
        callSuccessCallback(successArg);

        // Mock future for unsubscribeContext
        ListenableFuture<UnsubscribeContextResponse> responseFuture2 = Mockito.mock(ListenableFuture.class);
        doNothing().when(responseFuture2).addCallback(successArg.capture(), any());
        when(ngsiClient.unsubscribeContext(eq("http://iotAgent"), eq(null), eq("12345678"))).thenReturn(responseFuture2);

        // Set a configuration without the eventType
        Configuration emptyConfiguration = new Configuration();
        emptyConfiguration.setEventTypeIns(Collections.emptyList());
        subscriptionManager.setConfiguration(emptyConfiguration);

        // Check that unsubsribe is called when a later configuration removed the event type
        Assert.notNull(successArg.getValue());
    }

    @Test
    public void testUnsubscribeOnProviderRemoval() {

        // Mock the task scheduler and capture the runnable
        ArgumentCaptor<Runnable> runnableArg = ArgumentCaptor.forClass(Runnable.class);
        when(taskScheduler.scheduleWithFixedDelay(runnableArg.capture(), anyLong())).thenReturn(Mockito.mock(ScheduledFuture.class));

        // Mock the response to the subsribeContext
        ArgumentCaptor<SuccessCallback> successArg = ArgumentCaptor.forClass(SuccessCallback.class);
        ListenableFuture<SubscribeContextResponse> responseFuture = Mockito.mock(ListenableFuture.class);
        doNothing().when(responseFuture).addCallback(successArg.capture(), any());

        // Return the mocked future on subscription
        when(ngsiClient.subscribeContext(any(),any(), any())).thenReturn(responseFuture);

        Configuration configuration = getBasicConf();
        subscriptionManager.setConfiguration(configuration);

        // Execute scheduled runnable
        runnableArg.getValue().run();

        // Return the SubscribeContextResponse
        callSuccessCallback(successArg);

        // Mock future for unsubscribeContext
        ListenableFuture<UnsubscribeContextResponse> responseFuture2 = Mockito.mock(ListenableFuture.class);
        doNothing().when(responseFuture2).addCallback(successArg.capture(), any());
        when(ngsiClient.unsubscribeContext(eq("http://iotAgent"), eq(null), eq("12345678"))).thenReturn(responseFuture2);

        // Reset conf should trigger unsubsribeContext
        Configuration emptyConfiguration = getBasicConf();
        emptyConfiguration.getEventTypeIns().get(0).setProviders(Collections.emptySet());
        subscriptionManager.setConfiguration(emptyConfiguration);

        // Check that unsubsribe is called
        Assert.notNull(successArg.getValue());

    }

    @Test
    public void testValidateSubscriptionsId() {
        Assert.isTrue(subscriptionManager.validateSubscriptionsId());
    }

    private  void callSuccessCallback (ArgumentCaptor<SuccessCallback> successArg) {
        SubscribeContextResponse response = new SubscribeContextResponse();
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        subscribeResponse.setSubscriptionId("12345678");
        subscribeResponse.setDuration("PT1H");
        response.setSubscribeResponse(subscribeResponse);
        successArg.getValue().onSuccess(response);
    }
}

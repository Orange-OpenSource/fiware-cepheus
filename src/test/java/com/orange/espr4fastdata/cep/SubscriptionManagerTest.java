/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.EventBean;
import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.Configuration;
import com.orange.espr4fastdata.model.Provider;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.SubscribeContext;
import com.orange.ngsi.model.SubscribeContextResponse;
import com.orange.ngsi.model.SubscribeResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static com.orange.espr4fastdata.util.Util.*;

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
        assertEquals("P1H", subscribeContext.getDuration());
        assertEquals("http://iotAgent", urlProviderArg.getValue());

        // Call success callback
        SubscribeResponse subscribeResponse = new SubscribeResponse();
        subscribeResponse.setSubscriptionId("12345678");
        subscribeResponse.setDuration("P1H");
        SubscribeContextResponse response = new SubscribeContextResponse();
        response.setSubscribeResponse(subscribeResponse);
        onSuccessArg.getValue().accept(response);

        Set<Provider> providers = configuration.getEventTypeIns().get(0).getProviders();
        for(Provider provider: providers) {
            assertEquals("12345678", provider.getSubscriptionId());
            assertNotNull(provider.getSubscriptionDate());
        }
    }
}

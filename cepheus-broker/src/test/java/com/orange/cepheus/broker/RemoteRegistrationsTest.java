/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker;

import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.RegisterContext;
import com.orange.ngsi.model.RegisterContextResponse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

import static com.orange.cepheus.broker.Util.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests for RemoteRegistrations
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RemoteRegistrationsTest {

    private final static String remoteBrokerUrl = "http://remoteBroker:8080";

    @Mock
    Configuration configuration;

    @Captor
    private ArgumentCaptor<SuccessCallback<RegisterContextResponse>> successCaptor;

    @Captor
    private ArgumentCaptor<FailureCallback> failureCaptor;

    @Mock
    private ListenableFuture<RegisterContextResponse> registerFuture;

    @Mock
    NgsiClient ngsiClient;

    @Autowired
    @InjectMocks
    protected RemoteRegistrations remoteRegistrations;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void resetMocks() {
        reset(ngsiClient);
        reset(configuration);
        reset(registerFuture);
    }

    @Test
    public void testRemoteRegistration() throws Exception {
        String localRegistrationId = "localRegistrationId1";
        String remoteRegistrationId = "remoteRegistrationId1";

        RegisterContext registerContext = createRegistrationContext();
        registerContext.setRegistrationId(localRegistrationId);

        // prepare mocks
        doNothing().when(registerFuture).addCallback(successCaptor.capture(), any());
        when(ngsiClient.registerContext(any(), any(), any())).thenReturn(registerFuture);
        when(configuration.getRemoteUrl()).thenReturn(remoteBrokerUrl);

        // make *the* call
        remoteRegistrations.registerContext(registerContext, localRegistrationId);

        // check that the registerId was reset on register (not sending a local registrationId to remote broker)
        assertNull(registerContext.getRegistrationId());

        // fake response from ngsi client to registerContext
        RegisterContextResponse response = new RegisterContextResponse();
        response.setRegistrationId(remoteRegistrationId);
        response.setDuration(registerContext.getDuration());
        successCaptor.getValue().onSuccess(response);

        // check NGSI client is called
        verify(ngsiClient, times(1)).registerContext(eq(remoteBrokerUrl), eq(null), eq(registerContext));

        // check pending will not retrigger a successful register
        remoteRegistrations.registerPendingRemoteRegistrations();
        verify(ngsiClient, times(1)).registerContext(any(), any(), any());

        // check that the remote registrationId is well set
        assertEquals(remoteRegistrationId, remoteRegistrations.getRemoteRegistrationId(localRegistrationId));

        // check that remove works
        remoteRegistrations.removeRegistration(localRegistrationId);
        assertNull(remoteRegistrations.getRemoteRegistrationId(localRegistrationId));
    }

    @Test
    public void testRemoteRegistrationFailThenRetry() throws Exception {
        String localRegistrationId = "localRegistrationId1";
        String remoteRegistrationId = "remoteRegistrationId1";

        RegisterContext registerContext = createRegistrationContext();

        // prepare mocks
        doNothing().when(registerFuture).addCallback(successCaptor.capture(), failureCaptor.capture());
        when(ngsiClient.registerContext(any(), any(), any())).thenReturn(registerFuture);
        when(configuration.getRemoteUrl()).thenReturn(remoteBrokerUrl);

        // make *the* call
        remoteRegistrations.registerContext(registerContext, localRegistrationId);

        // check NGSI client is called
        verify(ngsiClient, times(1)).registerContext(eq(remoteBrokerUrl), eq(null), eq(registerContext));

        // fake response from ngsi client to registerContext
        failureCaptor.getValue().onFailure(new RuntimeException("fail"));

        // check that no remote registration is associated on failed register
        assertNull(remoteRegistrations.getRemoteRegistrationId(localRegistrationId));

        // check register is called another time
        remoteRegistrations.registerPendingRemoteRegistrations();
        verify(ngsiClient, times(2)).registerContext(eq(remoteBrokerUrl), eq(null), eq(registerContext));

        // fake response from ngsi client to registerContext
        RegisterContextResponse response = new RegisterContextResponse();
        response.setRegistrationId(remoteRegistrationId);
        response.setDuration(registerContext.getDuration());
        successCaptor.getValue().onSuccess(response);

        // check that the remote registrationId is well set
        assertEquals(remoteRegistrationId, remoteRegistrations.getRemoteRegistrationId(localRegistrationId));
    }

    @Test
    public void testRemoteRegistrationUpdate() throws Exception {
        String localRegistrationId = "localRegistrationId1";
        String remoteRegistrationId = "remoteRegistrationId1";

        RegisterContext registerContext = createRegistrationContext();

        // prepare mocks
        doNothing().when(registerFuture).addCallback(successCaptor.capture(), any());
        when(ngsiClient.registerContext(any(), any(), any())).thenReturn(registerFuture);
        when(configuration.getRemoteUrl()).thenReturn(remoteBrokerUrl);


        // make the call first call
        remoteRegistrations.registerContext(registerContext, localRegistrationId);

        // fake response from ngsi client to registerContext
        RegisterContextResponse response = new RegisterContextResponse();
        response.setRegistrationId(remoteRegistrationId);
        response.setDuration(registerContext.getDuration());
        successCaptor.getValue().onSuccess(response);

        // rechecks
        verify(ngsiClient, times(1)).registerContext(eq(remoteBrokerUrl), eq(null), eq(registerContext));
        assertEquals(remoteRegistrationId, remoteRegistrations.getRemoteRegistrationId(localRegistrationId));

        // Prepare a new registerContext
        RegisterContext registerContextUpdate = createRegistrationContext();
        registerContext.setRegistrationId(localRegistrationId);

        // trigger an update of the register
        remoteRegistrations.registerContext(registerContextUpdate, localRegistrationId);

        // check that the registerId was reset to previous remote registrationId
        assertEquals(remoteRegistrationId, registerContextUpdate.getRegistrationId());

        // fake response from ngsi client to registerContext
        RegisterContextResponse response2 = new RegisterContextResponse();
        response2.setRegistrationId(remoteRegistrationId);
        response2.setDuration(registerContextUpdate.getDuration());
        successCaptor.getValue().onSuccess(response2);

        // rechecks
        verify(ngsiClient, times(1)).registerContext(eq(remoteBrokerUrl), eq(null), eq(registerContextUpdate));
        assertEquals(remoteRegistrationId, remoteRegistrations.getRemoteRegistrationId(localRegistrationId));
    }

    @Test
    public void testRemoteRegistrationNoBroker() throws Exception {
        String localRegistrationId = "localRegistrationId1";

        RegisterContext registerContext = createRegistrationContext();

        // declare no broker
        when(configuration.getRemoteUrl()).thenReturn(null);

        // make *the* call
        remoteRegistrations.registerContext(registerContext, localRegistrationId);

        // check no remote was called
        verify(ngsiClient, never()).registerContext(any(), any(), any());
        assertNull(remoteRegistrations.getRemoteRegistrationId(localRegistrationId));
    }

    @Test
    public void testRemoteRegistrationNoUrlBroker() throws Exception {
        String localRegistrationId = "localRegistrationId1";

        RegisterContext registerContext = createRegistrationContext();

        // declare no broker
        when(configuration.getRemoteUrl()).thenReturn(null);

        // make *the* call
        remoteRegistrations.registerContext(registerContext, localRegistrationId);

        // check no remote was called
        verify(ngsiClient, never()).registerContext(any(), any(), any());
        assertNull(remoteRegistrations.getRemoteRegistrationId(localRegistrationId));
    }

    @Test
    public void testRemoteRegistrationEmptyUrlBroker() throws Exception {
        String localRegistrationId = "localRegistrationId1";

        RegisterContext registerContext = createRegistrationContext();

        // declare no broker
        when(configuration.getRemoteUrl()).thenReturn("");

        // make *the* call
        remoteRegistrations.registerContext(registerContext, localRegistrationId);

        // check no remote was called
        verify(ngsiClient, never()).registerContext(any(), any(), any());
        assertNull(remoteRegistrations.getRemoteRegistrationId(localRegistrationId));
    }
}

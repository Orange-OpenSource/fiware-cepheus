package com.orange.cepheus.cep.tenant;

import com.orange.cepheus.cep.ComplexEventProcessor;
import com.orange.cepheus.cep.EventMapper;
import com.orange.cepheus.cep.SubscriptionManager;
import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.cepheus.cep.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.LinkedList;

import static org.mockito.Mockito.*;


/**
 * Test class for TenantInit.
 */
@RunWith(MockitoJUnitRunner.class)
public class TenantInitTest {

    @Mock
    ApplicationContext applicationContext;

    @Mock
    EventMapper eventMapper;

    @Mock
    ComplexEventProcessor complexEventProcessor;

    @Mock
    SubscriptionManager subscriptionManager;

    @Mock
    Persistence persistence;

    @Mock
    TenantFilter tenantFilter;

    @Autowired
    @InjectMocks
    public TenantInit tenantInit;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void checkNoTenantConfigurationLoaded() throws PersistenceException {
        when(persistence.listConfigurations()).thenReturn(new LinkedList<>());
        tenantInit.loadConfigurationOnStartup();
        verify(tenantFilter, never()).forceTenantScope(any());
        verify(persistence, never()).loadConfiguration(any());
    }

    @Test
    public void checkOkTenantConfigurationLoaded() throws PersistenceException, ConfigurationException {
        Collection<String> configurations = new LinkedList<>();
        configurations.add("smartcity/team1");
        when(persistence.listConfigurations()).thenReturn(configurations);
        when(applicationContext.getBean("eventMapper")).thenReturn(eventMapper);
        when(applicationContext.getBean("complexEventProcessor")).thenReturn(complexEventProcessor);
        when(applicationContext.getBean("subscriptionManager")).thenReturn(subscriptionManager);

        InOrder inOrder = inOrder( tenantFilter );

        tenantInit.loadConfigurationOnStartup();
        verify(tenantFilter, atLeastOnce()).forceTenantScope("smartcity/team1");
        verify(tenantFilter, atLeastOnce()).forceTenantScope(null);
        inOrder.verify(tenantFilter).forceTenantScope("smartcity/team1");
        inOrder.verify(tenantFilter).forceTenantScope(null);
        verify(persistence, atLeastOnce()).loadConfiguration("smartcity/team1");
        verify(eventMapper, atLeastOnce()).setConfiguration(any());
        verify(complexEventProcessor, atLeastOnce()).setConfiguration(any());
        verify(subscriptionManager, atLeastOnce()).setConfiguration(any());
    }

    @Test
    public void checkKoTenantConfigurationLoaded() throws PersistenceException, ConfigurationException {
        Collection<String> configurations = new LinkedList<>();
        configurations.add("smartcity/team1");
        doThrow(PersistenceException.class).when(persistence).loadConfiguration("smartcity/team1");
        when(persistence.listConfigurations()).thenReturn(configurations);
        when(applicationContext.getBean("eventMapper")).thenReturn(eventMapper);
        when(applicationContext.getBean("complexEventProcessor")).thenReturn(complexEventProcessor);
        when(applicationContext.getBean("subscriptionManager")).thenReturn(subscriptionManager);

        InOrder inOrder = inOrder( tenantFilter );

        tenantInit.loadConfigurationOnStartup();
        verify(tenantFilter, atLeastOnce()).forceTenantScope("smartcity/team1");
        verify(tenantFilter, atLeastOnce()).forceTenantScope(null);
        inOrder.verify(tenantFilter).forceTenantScope("smartcity/team1");
        inOrder.verify(tenantFilter).forceTenantScope(null);
        verify(persistence, atLeastOnce()).loadConfiguration("smartcity/team1");
        verify(eventMapper, never()).setConfiguration(any());
        verify(complexEventProcessor, never()).setConfiguration(any());
        verify(subscriptionManager, never()).setConfiguration(any());
    }

    @After
    public void resetMock() {
        reset(eventMapper);
        reset(complexEventProcessor);
        reset(subscriptionManager);
        reset(applicationContext);
        reset(persistence);
        reset(tenantFilter);
    }
}

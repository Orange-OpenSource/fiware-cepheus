package com.orange.cepheus.cep.tenant;

import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.cepheus.cep.persistence.Persistence;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
    public void checkOkTenantConfigurationLoaded() {
        Collection<String> configurations = new LinkedList<>();
        configurations.add("smartcity/team1");
        when(persistence.listConfigurations()).thenReturn(configurations);
    }

    @After
    public void resetMock() {
        reset(applicationContext);
        reset(persistence);
        reset(tenantFilter);
    }
}

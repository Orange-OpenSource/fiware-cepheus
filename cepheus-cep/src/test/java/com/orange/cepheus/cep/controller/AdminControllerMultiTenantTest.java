/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep.controller;

import com.orange.cepheus.cep.Application;
import com.orange.cepheus.cep.ComplexEventProcessor;
import com.orange.cepheus.cep.EventMapper;
import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.PersistenceException;
import com.orange.cepheus.cep.model.Configuration;
import com.orange.cepheus.cep.persistence.Persistence;
import com.orange.cepheus.cep.tenant.TenantFilter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static com.orange.cepheus.cep.Util.getBasicConf;
import static com.orange.cepheus.cep.Util.json;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * Test the Admin controller in multi tenant mode
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebAppConfiguration
@ActiveProfiles("multi-tenant")
public class AdminControllerMultiTenantTest {

    private static final String tenantService1 = "smartcity1";
    private static final String tenantService2 = "smartcity2";
    private static final String tenantServicePath1 = "/team1";
    private static final String tenantServicePath2 = "/team2";

    private MockMvc mockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter mapping;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private TenantFilter tenantFilter;

    @Mock
    private ComplexEventProcessor complexEventProcessor;

    @Mock
    private Persistence persistence;

    @Mock
    private EventMapper eventMapper;

    @Autowired
    @InjectMocks
    AdminController adminController;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        // Inject tenantFilter to webApp mock
        this.mockMvc = webAppContextSetup(webApplicationContext).addFilter(tenantFilter).build();
    }

    @After
    public void resetMocks() {
        reset(complexEventProcessor);
        reset(persistence);
        reset(eventMapper);
    }

    @Test
    public void checkConfigurationNotFound() throws Exception {
        when(complexEventProcessor.getConfiguration()).thenReturn(null);

        mockMvc.perform(get("/v1/admin/config")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postConfOK() throws Exception {
        Configuration configuration = getBasicConf();

        mockMvc.perform(post("/v1/admin/config").content(json(mapping, configuration)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        ArgumentCaptor<Configuration> configurationArg = ArgumentCaptor.forClass(Configuration.class);
        verify(complexEventProcessor).setConfiguration(configurationArg.capture());

        Configuration capturedConfiguration = configurationArg.getValue();
        assertEquals(1, capturedConfiguration.getEventTypeIns().size());
        assertEquals("S.*", capturedConfiguration.getEventTypeIns().get(0).getId());
        assertEquals(1, capturedConfiguration.getEventTypeOuts().size());
        assertEquals("OUT1", capturedConfiguration.getEventTypeOuts().get(0).getId());

        verify(persistence).saveConfiguration(eq(TenantFilter.DEFAULT_TENANTID), eq(capturedConfiguration));
    }

    @Test
    public void postConfService() throws Exception {
        Configuration configuration = getBasicConf();

        mockMvc.perform(post("/v1/admin/config")
                .content(json(mapping, configuration))
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE, tenantService1))
                .andExpect(status().isCreated());

        ArgumentCaptor<Configuration> configurationArg = ArgumentCaptor.forClass(Configuration.class);
        verify(complexEventProcessor).setConfiguration(configurationArg.capture());
        Configuration capturedConfiguration = configurationArg.getValue();

        String tenantId = TenantFilter.tenantIdFromService(tenantService1, TenantFilter.DEFAULT_SERVICE_PATH);
        verify(persistence).saveConfiguration(eq(tenantId), eq(capturedConfiguration));
    }

    @Test
    public void postConfServicePath() throws Exception {
        Configuration configuration = getBasicConf();

        mockMvc.perform(post("/v1/admin/config")
                .content(json(mapping, configuration))
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE_PATH, tenantServicePath1))
                .andExpect(status().isCreated());

        ArgumentCaptor<Configuration> configurationArg = ArgumentCaptor.forClass(Configuration.class);
        verify(complexEventProcessor).setConfiguration(configurationArg.capture());
        Configuration capturedConfiguration = configurationArg.getValue();

        String tenantId = TenantFilter.tenantIdFromService(TenantFilter.DEFAULT_SERVICE, tenantServicePath1);
        verify(persistence).saveConfiguration(eq(tenantId), eq(capturedConfiguration));
    }

    @Test
    public void postConfServiceValidation() throws Exception {
        String jsonConfiguration = json(mapping, getBasicConf());

        // Check * is forbidden in service
        mockMvc.perform(post("/v1/admin/config")
                .content(jsonConfiguration)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE, "*"))
                .andExpect(status().isBadRequest());

        // Check / is forbidden in service
        mockMvc.perform(post("/v1/admin/config")
                .content(jsonConfiguration)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE, "/d"))
                .andExpect(status().isBadRequest());

        // Check A-Za-z0-9_ is valid
        mockMvc.perform(post("/v1/admin/config")
                .content(jsonConfiguration)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE, "ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz_0123456789"))
                .andExpect(status().isCreated());
    }

    @Test
    public void postConfServicePathValidation() throws Exception {
        String jsonConfiguration = json(mapping, getBasicConf());

        // Check / is mandatory in servicepath
        mockMvc.perform(post("/v1/admin/config")
                .content(jsonConfiguration)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE_PATH, "badservicepath"))
                .andExpect(status().isBadRequest());

        // Check / is allowed in servicepath
        mockMvc.perform(post("/v1/admin/config")
                .content(jsonConfiguration)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE_PATH, "/Service_Path1"))
                .andExpect(status().isCreated());

        // Check /A-Za-z0-9_ is valid
        mockMvc.perform(post("/v1/admin/config")
                .content(jsonConfiguration)
                .contentType(MediaType.APPLICATION_JSON)
                .header(TenantFilter.FIWARE_SERVICE_PATH, "/ABCDEFGHIJKLMNOPQRSTUVWXYZ/_abcdefghijklmnopqrstuvwxyz/_0123456789"))
                .andExpect(status().isCreated());
    }

    @Test
    public void getConfiguration() throws Exception {
        Configuration configuration = getBasicConf();
        when(complexEventProcessor.getConfiguration()).thenReturn(configuration);

        mockMvc.perform(get("/v1/admin/config")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.in[0].id").value(configuration.getEventTypeIns().get(0).getId()))
                .andExpect(jsonPath("$.out[0].id").value(configuration.getEventTypeOuts().get(0).getId()))
                .andExpect(jsonPath("$.statements[0]").value(configuration.getStatements().get(0)));
    }

    @Test
    public void deleteConfiguration() throws Exception {

        mockMvc.perform(delete("/v1/admin/config")
                .header(TenantFilter.FIWARE_SERVICE, tenantService1)
                .header(TenantFilter.FIWARE_SERVICE_PATH, tenantServicePath1))
                .andExpect(status().isOk());

        verify(complexEventProcessor).reset();
        verify(persistence).deleteConfiguration(eq(TenantFilter.tenantIdFromService(tenantService1, tenantServicePath1)));
    }

    @Test
    public void configurationErrorHandling() throws Exception {
        Configuration configuration = getBasicConf();

        doThrow(new ConfigurationException("ERROR", new Exception("DETAIL ERROR"))).when(complexEventProcessor).setConfiguration(any(Configuration.class));

        mockMvc.perform(post("/v1/admin/config").content(json(mapping, configuration)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.reasonPhrase").value("ERROR"))
                .andExpect(jsonPath("$.details").value("DETAIL ERROR"));
    }

    @Test
    public void eventMapperErrorHandling() throws Exception {
        Configuration configuration = getBasicConf();

        doThrow(new ConfigurationException("ERROR", new Exception("DETAIL ERROR"))).when(eventMapper).setConfiguration(any(Configuration.class));

        mockMvc.perform(post("/v1/admin/config").content(json(mapping, configuration)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.reasonPhrase").value("ERROR"))
                .andExpect(jsonPath("$.details").value("DETAIL ERROR"));
    }

    @Test
    public void persistenceErrorHandling() throws Exception {

        doThrow(new PersistenceException("ERROR")).when(persistence).saveConfiguration(any(), any(Configuration.class));

        Configuration configuration = getBasicConf();

        mockMvc.perform(post("/v1/admin/config").content(json(mapping, configuration)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.reasonPhrase").value("ERROR"));
    }

}

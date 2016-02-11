/*
 * Copyright (C) 2016 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.broker.controller;

import com.orange.cepheus.broker.Application;
import com.orange.ngsi.model.*;
import org.junit.After;
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
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

import static com.orange.cepheus.broker.Util.createSubscribeContextTemperature;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static com.orange.cepheus.broker.Util.*;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Tests for the NgsiRestController
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class NgsiRestControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MappingJackson2HttpMessageConverter mapper;

    @Mock
    private NgsiController ngsiController;

    @Autowired
    @InjectMocks
    private NgsiRestController ngsiRestController;

    @PostConstruct
    public void tearUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void teadDown() {
        reset(ngsiController);
    }

    @Test
    public void testAppendContextElement_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        AppendContextElement appendContextElement = new AppendContextElement();
        appendContextElement.setAttributeList(attributes);

        mockMvc.perform(post("/v1/contextEntities/12345678").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(json(mapper, appendContextElement))).andExpect(status().isOk()).andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.id").value("12345678")).andExpect(jsonPath("$.type").value(""))
                .andExpect(jsonPath("$.isPattern").value("false"))
                .andExpect(jsonPath("$.contextResponses[0].attributes[0].name").value("test"))
                .andExpect(jsonPath("$.contextResponses[0].attributes[0].type").value("string"))
                .andExpect(jsonPath("$.contextResponses[0].attributes[0].value").value("OK"))
                .andExpect(jsonPath("$.contextResponses[0].statusCode.code").value("200"));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContextRequest = updateContextArg.getValue();
        assertNotNull(updateContextRequest);
        assertEquals(UpdateAction.APPEND, updateContextRequest.getUpdateAction());
        assertEquals("12345678", updateContextRequest.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContextRequest.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContextRequest.getContextElements().get(0).getEntityId().getIsPattern());
    }

    @Test
    public void testAppendContextElement_Error() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        UpdateContextResponse response = new UpdateContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_400));
        when(ngsiController.updateContext(any())).thenReturn(response);

        AppendContextElement appendContextElement = new AppendContextElement();
        appendContextElement.setAttributeList(attributes);

        mockMvc.perform(post("/v1/contextEntities/12345678").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(json(mapper, appendContextElement))).andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode.code").value("400")).andExpect(jsonPath("$.id").doesNotExist())
                .andExpect(jsonPath("$.type").doesNotExist()).andExpect(jsonPath("$.isPattern").doesNotExist())
                .andExpect(jsonPath("$.contextResponses").doesNotExist());

        verify(ngsiController).updateContext(any());
    }

    @Test
    public void testUpdateContextElement_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        UpdateContextElement updateContextElement = new UpdateContextElement();
        updateContextElement.setContextAttributes(attributes);

        mockMvc.perform(put("/v1/contextEntities/12345678").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(json(mapper, updateContextElement))).andExpect(status().isOk()).andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.contextResponses[0].attributes[0].name").value("test"))
                .andExpect(jsonPath("$.contextResponses[0].attributes[0].type").value("string"))
                .andExpect(jsonPath("$.contextResponses[0].attributes[0].value").value("OK"))
                .andExpect(jsonPath("$.contextResponses[0].statusCode.code").value("200"));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContextRequest = updateContextArg.getValue();
        assertNotNull(updateContextRequest);
        assertEquals(UpdateAction.UPDATE, updateContextRequest.getUpdateAction());
        assertEquals("12345678", updateContextRequest.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContextRequest.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContextRequest.getContextElements().get(0).getEntityId().getIsPattern());
    }

    @Test
    public void testUpdateContextElement_Error() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        UpdateContextResponse response = new UpdateContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_400));
        when(ngsiController.updateContext(any())).thenReturn(response);

        UpdateContextElement updateContextElement = new UpdateContextElement();
        updateContextElement.setContextAttributes(attributes);

        mockMvc.perform(put("/v1/contextEntities/12345678").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
                .content(json(mapper, updateContextElement))).andExpect(status().isOk())
                .andExpect(jsonPath("$.errorCode.code").value("400")).andExpect(jsonPath("$.contextResponses").doesNotExist());

        verify(ngsiController).updateContext(any());
    }

    @Test
    public void testGetContextElement_OK() throws Exception {
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        QueryContextResponse response = new QueryContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntities/12345678").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode.code").value("200"))
                .andExpect(jsonPath("$.contextElement.attributes[0].name").value("test"))
                .andExpect(jsonPath("$.contextElement.attributes[0].type").value("string"))
                .andExpect(jsonPath("$.contextElement.attributes[0].value").value("OK"));

        verify(ngsiController).queryContext(queryContextArg.capture());

        QueryContext queryContext = queryContextArg.getValue();
        assertNotNull(queryContext);
        assertNotNull(queryContext.getEntityIdList());
        assertEquals(1, queryContext.getEntityIdList().size());
        assertNull(queryContext.getAttributeList());
        assertEquals("12345678", queryContext.getEntityIdList().get(0).getId());
        assertEquals("", queryContext.getEntityIdList().get(0).getType());
        assertEquals(false, queryContext.getEntityIdList().get(0).getIsPattern());
    }

    @Test
    public void testGetContextElement_Error() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        QueryContextResponse response = new QueryContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_400));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntities/12345678").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode.code").value("400")).andExpect(jsonPath("$.contextElement").doesNotExist());

        verify(ngsiController).queryContext(any());
    }

    @Test
    public void testDeleteContextElement_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        mockMvc.perform(delete("/v1/contextEntities/12345678").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContext = updateContextArg.getValue();
        assertNotNull(updateContext);
        assertNotNull(updateContext.getContextElements());
        assertEquals(1, updateContext.getContextElements().size());
        assertEquals(UpdateAction.DELETE, updateContext.getUpdateAction());
        assertEquals("12345678", updateContext.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContext.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContext.getContextElements().get(0).getEntityId().getIsPattern());
    }

    @Test
    public void testDeleteContextElement_Error() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        UpdateContextResponse response = new UpdateContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_400));
        when(ngsiController.updateContext(any())).thenReturn(response);

        mockMvc.perform(delete("/v1/contextEntities/12345678").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("400"));

        verify(ngsiController).updateContext(any());
    }

    @Test
    public void testAppendContextAttribute_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        ContextAttribute attribute = new ContextAttribute("temp", "float", "15.5");

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(Collections.singletonList(attribute));
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(attribute);

        mockMvc.perform(post("/v1/contextEntities/12345678/attributes/temp").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute))).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContextRequest = updateContextArg.getValue();
        assertNotNull(updateContextRequest);
        assertEquals(UpdateAction.APPEND, updateContextRequest.getUpdateAction());
        assertNotNull(updateContextRequest.getContextElements());
        assertEquals(1, updateContextRequest.getContextElements().size());
        assertNotNull(updateContextRequest.getContextElements().get(0).getEntityId());
        assertEquals("12345678", updateContextRequest.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContextRequest.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContextRequest.getContextElements().get(0).getEntityId().getIsPattern());
        assertNotNull(updateContextRequest.getContextElements().get(0).getContextAttributeList());
        assertEquals(1, updateContextRequest.getContextElements().get(0).getContextAttributeList().size());
        assertEquals("temp", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getName());
        assertEquals("float", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getType());
        assertEquals("15.5", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getValue());
    }

    @Test
    public void testAppendContextAttribute_EmptyAttrError() throws Exception {

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(null);

        mockMvc.perform(post("/v1/contextEntities/12345678/attributes/temp")
                .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute)))
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_472.getLabel()));

        verify(ngsiController, never()).updateContext(any());
    }

    @Test
    public void testAppendContextAttribute_MismatchAttrError() throws Exception {

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(new ContextAttribute("temp", "float", "15.5"));

        mockMvc.perform(post("/v1/contextEntities/12345678/attributes/BAD_ATTR").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute)))
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_472.getLabel()));

        verify(ngsiController, never()).updateContext(any());
    }

    @Test
    public void testAppendContextAttribute_Error() throws Exception {

        UpdateContextResponse response = new UpdateContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_500));
        when(ngsiController.updateContext(any())).thenReturn(response);

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(new ContextAttribute("temp", "float", "15.5"));

        mockMvc.perform(post("/v1/contextEntities/12345678/attributes/temp").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute))).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_500.getLabel()));

        verify(ngsiController).updateContext(any());
    }

    @Test
    public void testUpdateContextAttribute_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        ContextAttribute attribute = new ContextAttribute("temp", "float", "15.5");

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(Collections.singletonList(attribute));
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(attribute);

        mockMvc.perform(put("/v1/contextEntities/12345678/attributes/temp").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute))).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContextRequest = updateContextArg.getValue();
        assertNotNull(updateContextRequest);
        assertEquals(UpdateAction.UPDATE, updateContextRequest.getUpdateAction());
        assertNotNull(updateContextRequest.getContextElements());
        assertEquals(1, updateContextRequest.getContextElements().size());
        assertNotNull(updateContextRequest.getContextElements().get(0).getEntityId());
        assertEquals("12345678", updateContextRequest.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContextRequest.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContextRequest.getContextElements().get(0).getEntityId().getIsPattern());
        assertNotNull(updateContextRequest.getContextElements().get(0).getContextAttributeList());
        assertEquals(1, updateContextRequest.getContextElements().get(0).getContextAttributeList().size());
        assertEquals("temp", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getName());
        assertEquals("float", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getType());
        assertEquals("15.5", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getValue());
    }

    @Test
    public void testUpdateContextAttribute_EmptyAttrError() throws Exception {

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(null);

        mockMvc.perform(put("/v1/contextEntities/12345678/attributes/temp").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute)))
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_472.getLabel()));

        verify(ngsiController, never()).updateContext(any());
    }

    @Test
    public void testUpdateContextAttribute_MismatchAttrError() throws Exception {

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(new ContextAttribute("temp", "float", "15.5"));

        mockMvc.perform(put("/v1/contextEntities/12345678/attributes/BAD_ATTR").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute)))
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_472.getLabel()));

        verify(ngsiController, never()).updateContext(any());
    }

    @Test
    public void testUpdateContextAttribute_Error() throws Exception {

        UpdateContextResponse response = new UpdateContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_500));
        when(ngsiController.updateContext(any())).thenReturn(response);

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(new ContextAttribute("temp", "float", "15.5"));

        mockMvc.perform(put("/v1/contextEntities/12345678/attributes/temp").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute))).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_500.getLabel()));

        verify(ngsiController).updateContext(any());
    }

    @Test
    public void testGetContextAttribute_OK() throws Exception {
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        QueryContextResponse response = new QueryContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntities/12345678/attributes/test").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode.code").value("200"))
                .andExpect(jsonPath("$.attributes[0].name").value("test"))
                .andExpect(jsonPath("$.attributes[0].type").value("string"))
                .andExpect(jsonPath("$.attributes[0].value").value("OK"));

        verify(ngsiController).queryContext(queryContextArg.capture());

        QueryContext queryContext = queryContextArg.getValue();
        assertNotNull(queryContext);
        assertNotNull(queryContext.getEntityIdList());
        assertEquals(1, queryContext.getEntityIdList().size());
        assertEquals("12345678", queryContext.getEntityIdList().get(0).getId());
        assertEquals("", queryContext.getEntityIdList().get(0).getType());
        assertEquals(false, queryContext.getEntityIdList().get(0).getIsPattern());
        assertNotNull(queryContext.getAttributeList());
        assertEquals(1, queryContext.getAttributeList().size());
        assertEquals("test", queryContext.getAttributeList().get(0));
    }

    @Test
    public void testDeleteContextAttribute_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        mockMvc.perform(delete("/v1/contextEntities/12345678/attributes/test").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContext = updateContextArg.getValue();
        assertNotNull(updateContext);
        assertNotNull(updateContext.getContextElements());
        assertEquals(1, updateContext.getContextElements().size());
        assertEquals(UpdateAction.DELETE, updateContext.getUpdateAction());
        assertEquals("12345678", updateContext.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContext.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContext.getContextElements().get(0).getEntityId().getIsPattern());
        assertNotNull(updateContext.getContextElements().get(0).getContextAttributeList());
        assertEquals(1, updateContext.getContextElements().get(0).getContextAttributeList().size());
        assertEquals("test", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getName());
        assertEquals("", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getType());
        assertEquals("", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getValue());
    }

    @Test
    public void testUpdateContextAttributeValue_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        ContextAttribute attribute = new ContextAttribute("temp", "float", "15.5");
        attribute.setMetadata(Collections.singletonList(new ContextMetadata("ID", "string", "DEADBEEF")));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(Collections.singletonList(attribute));
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        UpdateContextAttribute updateContextAttribute = new UpdateContextAttribute();
        updateContextAttribute.setAttribute(attribute);

        mockMvc.perform(put("/v1/contextEntities/12345678/attributes/temp/DEADBEEF").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json(mapper, updateContextAttribute))).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CodeEnum.CODE_200.getLabel()));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContextRequest = updateContextArg.getValue();
        assertNotNull(updateContextRequest);
        assertEquals(UpdateAction.UPDATE, updateContextRequest.getUpdateAction());
        assertNotNull(updateContextRequest.getContextElements());
        assertEquals(1, updateContextRequest.getContextElements().size());
        assertNotNull(updateContextRequest.getContextElements().get(0).getEntityId());
        assertEquals("12345678", updateContextRequest.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContextRequest.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContextRequest.getContextElements().get(0).getEntityId().getIsPattern());
        assertNotNull(updateContextRequest.getContextElements().get(0).getContextAttributeList());
        assertEquals(1, updateContextRequest.getContextElements().get(0).getContextAttributeList().size());
        assertEquals("temp", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getName());
        assertEquals("float", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getType());
        assertEquals("15.5", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getValue());
        assertNotNull(updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getMetadata());
        assertEquals(1, updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().size());
        assertEquals("ID", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().get(0).getName());
        assertEquals("string", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().get(0).getType());
        assertEquals("DEADBEEF", updateContextRequest.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().get(0).getValue());
    }

    @Test
    public void testGetContextAttributeValue_NotImplemented() throws Exception {
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        QueryContextResponse response = new QueryContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntities/12345678/attributes/test/DEADBEEF").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("500"));

        verify(ngsiController, never()).queryContext(queryContextArg.capture());
    }

    @Test
    public void testDeleteContextAttributeValue_OK() throws Exception {
        ArgumentCaptor<UpdateContext> updateContextArg = ArgumentCaptor.forClass(UpdateContext.class);

        ContextAttribute contextAttribute = new ContextAttribute("test", "string", "OK");
        contextAttribute.setMetadata(Collections.singletonList(new ContextMetadata("ID", "string", "DEADBEEF")));
        List attributes = Collections.singletonList(contextAttribute);

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        UpdateContextResponse response = new UpdateContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.updateContext(any())).thenReturn(response);

        mockMvc.perform(delete("/v1/contextEntities/12345678/attributes/test/DEADBEEF").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));

        verify(ngsiController).updateContext(updateContextArg.capture());

        UpdateContext updateContext = updateContextArg.getValue();
        assertNotNull(updateContext);
        assertNotNull(updateContext.getContextElements());
        assertEquals(1, updateContext.getContextElements().size());
        assertEquals(UpdateAction.DELETE, updateContext.getUpdateAction());
        assertEquals("12345678", updateContext.getContextElements().get(0).getEntityId().getId());
        assertEquals("", updateContext.getContextElements().get(0).getEntityId().getType());
        assertEquals(false, updateContext.getContextElements().get(0).getEntityId().getIsPattern());
        assertNotNull(updateContext.getContextElements().get(0).getContextAttributeList());
        assertEquals(1, updateContext.getContextElements().get(0).getContextAttributeList().size());
        assertEquals("test", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getName());
        assertEquals("", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getType());
        assertEquals("", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getValue());
        assertNotNull(updateContext.getContextElements().get(0).getContextAttributeList().get(0).getMetadata());
        assertEquals(1, updateContext.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().size());
        assertEquals("ID", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().get(0).getName());
        assertEquals("string", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().get(0).getType());
        assertEquals("DEADBEEF", updateContext.getContextElements().get(0).getContextAttributeList().get(0).getMetadata().get(0).getValue());
    }

    @Test
    public void testGetContextEntityTypes_OK() throws Exception {
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "TempSensor", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        QueryContextResponse response = new QueryContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntityTypes/TempSensor").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.contextResponses").isArray())
                .andExpect(jsonPath("$.contextResponses[0].statusCode.code").value("200"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.id").value("12345678"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.isPattern").value("false"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.attributes[0].name").value("test"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.attributes[0].type").value("string"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.attributes[0].value").value("OK"));

        verify(ngsiController).queryContext(queryContextArg.capture());

        QueryContext queryContext = queryContextArg.getValue();
        assertNotNull(queryContext);
        assertNotNull(queryContext.getEntityIdList());
        assertEquals(1, queryContext.getEntityIdList().size());
        assertNull(queryContext.getAttributeList());
        assertEquals(".*", queryContext.getEntityIdList().get(0).getId());
        assertEquals("TempSensor", queryContext.getEntityIdList().get(0).getType());
        assertEquals(true, queryContext.getEntityIdList().get(0).getIsPattern());
    }

    @Test
    public void testGetContextEntityTypes_Error() throws Exception {
        QueryContextResponse response = new QueryContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_500));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntityTypes/TempSensor").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.errorCode.code").value("500"))
                .andExpect(jsonPath("$.contextResponses").doesNotExist());

        verify(ngsiController).queryContext(any());
    }

    @Test
    public void testGetContextEntityTypesAttribute_OK() throws Exception {
        ArgumentCaptor<QueryContext> queryContextArg = ArgumentCaptor.forClass(QueryContext.class);

        List attributes = Collections.singletonList(new ContextAttribute("test", "string", "OK"));

        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("12345678", "TempSensor", false));
        contextElement.setContextAttributeList(attributes);
        ContextElementResponse contextElementResponse = new ContextElementResponse(contextElement, new StatusCode(CodeEnum.CODE_200));
        QueryContextResponse response = new QueryContextResponse();
        response.setContextElementResponses(Collections.singletonList(contextElementResponse));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntityTypes/TempSensor/attributes/temp").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.contextResponses").isArray())
                .andExpect(jsonPath("$.contextResponses[0].statusCode.code").value("200"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.id").value("12345678"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.type").value("TempSensor"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.isPattern").value("false"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.attributes[0].name").value("test"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.attributes[0].type").value("string"))
                .andExpect(jsonPath("$.contextResponses[0].contextElement.attributes[0].value").value("OK"));

        verify(ngsiController).queryContext(queryContextArg.capture());

        QueryContext queryContext = queryContextArg.getValue();
        assertNotNull(queryContext);
        assertNotNull(queryContext.getEntityIdList());
        assertEquals(1, queryContext.getEntityIdList().size());
        assertNotNull(queryContext.getAttributeList());
        assertEquals(".*", queryContext.getEntityIdList().get(0).getId());
        assertEquals("TempSensor", queryContext.getEntityIdList().get(0).getType());
        assertEquals(true, queryContext.getEntityIdList().get(0).getIsPattern());
        assertEquals("temp", queryContext.getAttributeList().get(0));
    }

    @Test
    public void testGetContextEntityTypesAttribute_Error() throws Exception {
        QueryContextResponse response = new QueryContextResponse();
        response.setErrorCode(new StatusCode(CodeEnum.CODE_500));
        when(ngsiController.queryContext(any())).thenReturn(response);

        mockMvc.perform(get("/v1/contextEntityTypes/TempSensor/attributes/temp").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.errorCode.code").value("500"))
                .andExpect(jsonPath("$.contextResponses").doesNotExist());

        verify(ngsiController).queryContext(any());
    }

    @Test
    public void testAppendSubscription() throws Exception {

        mockMvc.perform(post("/v1/contextSubscriptions")
                .content(json(mapper, createSubscribeContextTemperature()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ngsiController).subscribeContext(any());
    }

    @Test
    public void testUpdateSubscription() throws Exception {

        mockMvc.perform(put("/v1/contextSubscriptions/12345678")
                .content(json(mapper, createUpdateSubscribeContext()))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(ngsiController).updateContextSubscription(any());
    }

    @Test
    public void testDeleteSubscription() throws Exception {

        mockMvc.perform(delete("/v1/contextSubscriptions/12345678"))
                .andExpect(status().isOk());

        verify(ngsiController).unsubscribeContext(any());
    }
}

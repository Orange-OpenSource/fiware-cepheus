/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.cepheus.cep;

import com.orange.cepheus.cep.exception.ConfigurationException;
import com.orange.cepheus.cep.exception.EventProcessingException;
import com.orange.cepheus.cep.model.*;
import com.orange.ngsi.model.ContextAttribute;
import com.orange.ngsi.model.ContextElement;
import com.orange.ngsi.model.ContextMetadata;
import com.orange.ngsi.model.EntityId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test the EventMapper class
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class EventMapperTest {

    @Autowired
    public EventMapper eventMapper;

    /**
     * Test that esperTypeFromEventType return the right Java types
     */
    @Test
    public void testEsperTypes() {
        EventType e = new EventType("ID", "TYPE", false);
        e.addAttribute(new Attribute("1", "string"));
        e.addAttribute(new Attribute("2", "double"));
        e.addAttribute(new Attribute("3", "int"));
        e.addAttribute(new Attribute("4", "float"));
        e.addAttribute(new Attribute("5", "boolean"));
        e.addAttribute(new Attribute("6", "XXXX"));
        e.addAttribute(new Attribute("7", "long"));

        Map<String, Object> map = eventMapper.esperTypeFromEventType(e);

        assertEquals(String.class, map.get("id"));
        assertEquals(String.class, map.get("1"));
        assertEquals(double.class, map.get("2"));
        assertEquals(int.class, map.get("3"));
        assertEquals(float.class, map.get("4"));
        assertEquals(boolean.class, map.get("5"));
        assertEquals(long.class, map.get("7"));
    }

    /**
     * Test that conversion fails on null attribute
     * @throws Exception
     */
    @Test(expected = EventProcessingException.class)
    public void testContextElementConversionNull() throws Exception {
        ContextElement ce = new ContextElement();
        ce.setEntityId(new EntityId("S1", "TempSensor", false));

        List<ContextAttribute> attributes = new LinkedList<>();
        attributes.add(new ContextAttribute("1", "string", null));
        ce.setContextAttributeList(attributes);

        eventMapper.eventFromContextElement(ce).getValues();
    }

    /**
     * Test that the ContextElement is correctly converted to an Esper map
     * @throws Exception
     */
    @Test
    public void testContextElementConversion() throws Exception {
        ContextElement ce = new ContextElement();
        ce.setEntityId(new EntityId("S1", "TempSensor", false));

        List<ContextAttribute> attributes = new LinkedList<>();
        attributes.add(new ContextAttribute("1", "string", "hello"));
        attributes.add(new ContextAttribute("1_string", null, "hello"));
        attributes.add(new ContextAttribute("2", "float", 1.4f));
        attributes.add(new ContextAttribute("3", "double", 3.1d));
        attributes.add(new ContextAttribute("4", "int", 3));
        attributes.add(new ContextAttribute("5", "long", 3l));
        ce.setContextAttributeList(attributes);

        Map<String, Object> values = eventMapper.eventFromContextElement(ce).getValues();
        assertEquals("S1", values.get("id"));
        assertEquals("hello", values.get("1"));
        assertEquals("hello", values.get("1_string"));
        assertEquals(1.4f, values.get("2"));
        assertEquals(3.1d, values.get("3"));
        assertEquals(3, values.get("4"));
        assertEquals(3l, values.get("5"));

    }

    /**
     * Test that the ContextElement from string values is correctly converted to an Esper map
     * @throws Exception
     */
    @Test
    public void testContextElementConversionString() throws Exception {
        ContextElement ce = new ContextElement();
        ce.setEntityId(new EntityId("S1", "TempSensor", false));

        List<ContextAttribute> attributes = new LinkedList<>();
        attributes.add(new ContextAttribute("1", "string", "hello"));
        attributes.add(new ContextAttribute("2", "float", "1.4"));
        attributes.add(new ContextAttribute("3", "double", "3.1"));
        attributes.add(new ContextAttribute("4", "int", "3"));
        attributes.add(new ContextAttribute("5", "long", "3"));
        ce.setContextAttributeList(attributes);

        Map<String, Object> values = eventMapper.eventFromContextElement(ce).getValues();
        assertEquals("S1", values.get("id"));
        assertEquals("hello", values.get("1"));
        assertEquals(1.4f, values.get("2"));
        assertEquals(3.1d, values.get("3"));
        assertEquals(3, values.get("4"));
        assertEquals(3l, values.get("5"));
    }

    /**
     * Test that the ContextElement with metadata is correctly converted to an Esper map
     * @throws Exception
     */
    @Test
    public void testContextElementConversionMetadata() throws Exception {
        ContextElement ce = new ContextElement();
        ce.setEntityId(new EntityId("S1", "TempSensor", false));
        ContextAttribute ca = new ContextAttribute("ATTR1", "string", "hello");
        ce.setContextAttributeList(Collections.singletonList(ca));

        List<ContextMetadata> metadatas = new LinkedList<>();
        metadatas.add(new ContextMetadata("1", "string", "hello"));
        metadatas.add(new ContextMetadata("2", "float", 1.4f));
        metadatas.add(new ContextMetadata("3", "double", 3.1d));
        metadatas.add(new ContextMetadata("4", "int", 3));
        metadatas.add(new ContextMetadata("5", "long", 3l));
        ca.setMetadata(metadatas);

        Map<String, Object> values = eventMapper.eventFromContextElement(ce).getValues();
        assertEquals("hello", values.get("ATTR1_1"));
        assertEquals(1.4f, values.get("ATTR1_2"));
        assertEquals(3.1d, values.get("ATTR1_3"));
        assertEquals(3, values.get("ATTR1_4"));
        assertEquals(3l, values.get("ATTR1_5"));

    }

    /**
     * Test that the ContextElement with string metadata is correctly converted to an Esper map
     * @throws Exception
     */
    @Test
    public void testContextElementConversionMetadataString() throws Exception {
        ContextElement ce = new ContextElement();
        ce.setEntityId(new EntityId("S1", "TempSensor", false));
        ContextAttribute ca = new ContextAttribute("ATTR1", "string", "hello");
        ce.setContextAttributeList(Collections.singletonList(ca));

        List<ContextMetadata> metadatas = new LinkedList<>();
        metadatas.add(new ContextMetadata("1", "string", "hello"));
        metadatas.add(new ContextMetadata("2", "float", "1.4"));
        metadatas.add(new ContextMetadata("3", "double", "3.1"));
        metadatas.add(new ContextMetadata("4", "int", "3"));
        metadatas.add(new ContextMetadata("5", "long", "3"));

        ca.setMetadata(metadatas);

        Map<String, Object> values = eventMapper.eventFromContextElement(ce).getValues();
        assertEquals("hello", values.get("ATTR1_1"));
        assertEquals(1.4f, values.get("ATTR1_2"));
        assertEquals(3.1d, values.get("ATTR1_3"));
        assertEquals(3, values.get("ATTR1_4"));
        assertEquals(3l, values.get("ATTR1_5"));
    }

    /**
     * Test that the ContextElement conversion fails
     * @throws Exception
     */
    @Test(expected = EventProcessingException.class)
    public void testContextElementConversionFailConv() throws Exception {
        ContextElement ce = new ContextElement();
        ce.setEntityId(new EntityId("S1", "TempSensor", false));
        ContextAttribute ca = new ContextAttribute("ATTR1", "string", "hello");
        ce.setContextAttributeList(Collections.singletonList(ca));

        List<ContextMetadata> metadatas = new LinkedList<>();
        metadatas.add(new ContextMetadata("1", "float", "hello"));
        ca.setMetadata(metadatas);

        eventMapper.eventFromContextElement(ce).getValues();
    }

    /**
     * Test that the ContextElement with string metadata is correctly converted to an Esper map
     * @throws Exception
     */
    @Test
    public void testContextElementJsonPath_OK() throws Exception {

        Attribute a = new Attribute("Attr", "string");
        a.setJsonpath("$.test[0]");

        EventTypeIn e = new EventTypeIn("S.*", "TempSensor", true);
        e.setAttributes(Collections.singleton(a));

        Configuration configuration = new Configuration();
        configuration.setEventTypeIns(Collections.singletonList(e));

        eventMapper.setConfiguration(configuration);

        ContextElement ce = new ContextElement();
        ce.setEntityId(new EntityId("S1", "TempSensor", false));
        ContextAttribute ca = new ContextAttribute("Attr", "string", Collections.singletonMap("test", Collections.singletonList("hello")));
        ce.setContextAttributeList(Collections.singletonList(ca));

        Map<String, Object> values = eventMapper.eventFromContextElement(ce).getValues();
        assertEquals("hello", values.get("Attr"));
    }

    /**
     * Test that the ContextElement with a bad jsonpath in attribute will trigger exception
     * @throws Exception
     */
    @Test(expected = ConfigurationException.class)
    public void testContextElementWithBadJsonPathInAttribute() throws ConfigurationException {

        Attribute a = new Attribute("Attr", "string");
        a.setJsonpath("$BADJSONPATH");

        EventTypeIn e = new EventTypeIn("S.*", "TempSensor", true);
        e.setAttributes(Collections.singleton(a));

        Configuration configuration = new Configuration();
        configuration.setEventTypeIns(Collections.singletonList(e));

        eventMapper.setConfiguration(configuration);
    }

    /**
     * Test that the ContextElement with a bad jsonpath in metadata will trigger exception
     * @throws Exception
     */
    @Test(expected = ConfigurationException.class)
    public void testContextElementWithBadJsonPathInMetadata() throws ConfigurationException {
        Metadata m = new Metadata("Meta", "string");
        m.setJsonpath("$BADJSONPATH2");

        Attribute a = new Attribute("Attr", "string");
        a.setMetadata(Collections.singleton(m));

        EventTypeIn e = new EventTypeIn("S.*", "TempSensor", true);
        e.setAttributes(Collections.singleton(a));

        Configuration configuration = new Configuration();
        configuration.setEventTypeIns(Collections.singletonList(e));

        eventMapper.setConfiguration(configuration);
    }
}

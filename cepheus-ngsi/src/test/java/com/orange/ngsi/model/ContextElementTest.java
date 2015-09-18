/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.orange.ngsi.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static com.orange.ngsi.Util.*;
import static org.junit.Assert.assertTrue;

/**
 * Tests for ContextElement
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class ContextElementTest {

    private ObjectMapper xmlmapper = new XmlMapper();

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void convertContextElementToJsonWithoutEntityId() throws JsonProcessingException {

        ContextElement contextElement = createTemperatureContextElement(0);
        String json = objectMapper.writeValueAsString(contextElement);
        assertFalse(json.contains("EntityId"));
    }

    @Test
    public void deserializationContextElement() throws IOException {

        String json = "{\n" +
                "           \"type\": \"T1\",\n" +
                "           \"isPattern\": \"false\",\n" +
                "           \"id\": \"E1\",\n" +
                "           \"attributes\": [\n" +
                "           {\n" +
                "               \"name\": \"A\",\n" +
                "               \"type\": \"T\",\n" +
                "               \"value\": [ \"22\" , \n" +
                "                          {\n" +
                "                             \"x\": [ \"x1\", \"x2\"], \n" +
                "                             \"y\": \"3\" \n" +
                "                          }, \n" +
                "                          [ \"z1\", \"z2\" ] \n" +
                "                        ]\n" +
                "           },\n" +
                "           {\n" +
                "               \"name\": \"B\",\n" +
                "               \"type\": \"T\",\n" +
                "               \"value\": {\n" +
                "                  \"x\": { \n" +
                "                          \"x1\": \"a\", \n" +
                "                          \"x2\": \"b\" \n" +
                "                  },\n" +
                "                  \"y\": [ \"y1\", \"y2\" ]\n" +
                "               }\n" +
                "           }\n" +
                "           ]\n" +
                "       }";

        ContextElement contextElement = objectMapper.readValue(json, ContextElement.class);
        assertEquals("E1", contextElement.getEntityId().getId());
        assertEquals("A", contextElement.getContextAttributeList().get(0).getName());
        assertEquals(3, ((List<?>)contextElement.getContextAttributeList().get(0).getValue()).size());

    }

    @Test
    public void deserializationSimpleContextElement() throws IOException {

        String json = "{\n" +
                "           \"type\": \"T1\",\n" +
                "           \"isPattern\": \"false\",\n" +
                "           \"id\": \"E1\",\n" +
                "           \"attributes\": [\n" +
                "           {\n" +
                "               \"name\": \"A\",\n" +
                "               \"type\": \"T\",\n" +
                "               \"value\": \"22\" \n" +
                "           }\n" +
                "           ]\n" +
                "       }";

        ContextElement contextElement = objectMapper.readValue(json, ContextElement.class);
        assertEquals("E1", contextElement.getEntityId().getId());
        assertEquals("A", contextElement.getContextAttributeList().get(0).getName());

    }

    @Test
    public void deserializationXMLSimpleContextElement() throws IOException {

        String xml = "<contextElement>\n" +
                "        <entityId type=\"T1\" isPattern=\"false\">\n" +
                "        <id>E1</id>\n" +
                "        </entityId>\n" +
                "        <contextAttributeList>\n" +
                "        <contextAttribute>\n" +
                "        <name>A</name>\n" +
                "        <type>T</type>\n" +
                "        <value>22</value>\n" +
                "        </contextAttribute>\n" +
                "        </contextAttributeList>\n" +
                "        </contextElement>";

        ContextElement contextElement2 = xmlmapper.readValue(xml, ContextElement.class);
        assertEquals("E1", contextElement2.getEntityId().getId());
        assertEquals("A", contextElement2.getContextAttributeList().get(0).getName());

    }

    @Test
    public void deserializationXMLMoreSimpleContextElement() throws IOException {

        String xml = "<contextElement>\n" +
                "        <entityId type=\"T1\" isPattern=\"false\">\n" +
                "        <id>E1</id>\n" +
                "        </entityId>\n" +
                "        </contextElement>";

        ContextElement contextElement2 = xmlmapper.readValue(xml, ContextElement.class);
        assertEquals("E1", contextElement2.getEntityId().getId());

    }

    @Test
    public void serializationXMLMoreSimpleContextElement() throws IOException {

        ContextElement contextElement = new ContextElement();
        EntityId entityId = new EntityId("E1", "T1", false);
        contextElement.setEntityId(entityId);


        String xml = xmlmapper.writeValueAsString(contextElement);
        assertTrue(xml.contains("E1"));

    }
}

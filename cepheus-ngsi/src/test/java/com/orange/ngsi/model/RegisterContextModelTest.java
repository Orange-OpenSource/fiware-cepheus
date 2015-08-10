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
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import static com.orange.ngsi.Util.createRegisterContextTemperature;
import static org.junit.Assert.assertEquals;

/**
 * Test for RegisterContext
 */
public class RegisterContextModelTest {

    @Test
    public void deserializationSimpleRegisterContext() throws URISyntaxException, IOException {

        String json = getJsonOrionRegisterContext();
        ObjectMapper mapper = new ObjectMapper();
        RegisterContext registerContext = mapper.readValue(json, RegisterContext.class);

        assertEquals(1, registerContext.getContextRegistrationList().size());
        assertEquals(2, registerContext.getContextRegistrationList().get(0).getEntityIdList().size());
        assertEquals("Room1", registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).getId());
        assertEquals("Room", registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).getType());
        assertEquals(false, registerContext.getContextRegistrationList().get(0).getEntityIdList().get(0).getIsPattern());
        assertEquals("Room2", registerContext.getContextRegistrationList().get(0).getEntityIdList().get(1).getId());
        assertEquals("Room", registerContext.getContextRegistrationList().get(0).getEntityIdList().get(1).getType());
        assertEquals(false, registerContext.getContextRegistrationList().get(0).getEntityIdList().get(1).getIsPattern());
        assertEquals(2, registerContext.getContextRegistrationList().get(0).getContextRegistrationAttributeList().size());
        assertEquals("temperature", registerContext.getContextRegistrationList().get(0).getContextRegistrationAttributeList().get(0).getName());
        assertEquals("float", registerContext.getContextRegistrationList().get(0).getContextRegistrationAttributeList().get(0).getType());
        assertEquals(false, registerContext.getContextRegistrationList().get(0).getContextRegistrationAttributeList().get(0).getIsDomain());
        assertEquals("pressure", registerContext.getContextRegistrationList().get(0).getContextRegistrationAttributeList().get(1).getName());
        assertEquals("integer", registerContext.getContextRegistrationList().get(0).getContextRegistrationAttributeList().get(1).getType());
        assertEquals(false, registerContext.getContextRegistrationList().get(0).getContextRegistrationAttributeList().get(1).getIsDomain());
        assertEquals(new URI("http://mysensors.com/Rooms"), registerContext.getContextRegistrationList().get(0).getProvidingApplication());
        assertEquals("P1M", registerContext.getDuration());

    }

    @Test
    public void serializationSimpleRegisterContext() throws URISyntaxException, JsonProcessingException {
        RegisterContext registerContext = createRegisterContextTemperature();
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        String json = writer.writeValueAsString(registerContext);

        List<ContextRegistration> contextRegistrationList = JsonPath.read(json, "$.contextRegistrations[*]");
        assertEquals(1, contextRegistrationList.size());
        List<EntityId> entityIdList = JsonPath.read(json, "$.contextRegistrations[0].entities[*]");
        assertEquals(1, entityIdList.size());
        assertEquals("Room*", JsonPath.read(json, "$.contextRegistrations[0].entities[0].id"));
        assertEquals("Room", JsonPath.read(json, "$.contextRegistrations[0].entities[0].type"));
        assertEquals(true, JsonPath.read(json, "$.contextRegistrations[0].entities[0].isPattern"));
        List<ContextRegistrationAttribute> attributes = JsonPath.read(json, "$.contextRegistrations[0].attributes[*]");
        assertEquals(1, attributes.size());
        assertEquals("temperature", JsonPath.read(json, "$.contextRegistrations[0].attributes[0].name"));
        assertEquals("float", JsonPath.read(json, "$.contextRegistrations[0].attributes[0].type"));
        assertEquals(false, JsonPath.read(json, "$.contextRegistrations[0].attributes[0].isDomain"));
        assertEquals("http://localhost:1028/accumulate", JsonPath.read(json, "$.contextRegistrations[0].providingApplication"));
        assertEquals("PT10S", JsonPath.read(json, "$.duration"));

    }

    private String getJsonOrionRegisterContext(){
        String json = "{\n" +
                "    \"contextRegistrations\": [\n" +
                "        {\n" +
                "            \"entities\": [\n" +
                "                {\n" +
                "                    \"type\": \"Room\",\n" +
                "                    \"isPattern\": \"false\",\n" +
                "                    \"id\": \"Room1\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"type\": \"Room\",\n" +
                "                    \"isPattern\": \"false\",\n" +
                "                    \"id\": \"Room2\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"attributes\": [\n" +
                "                {\n" +
                "                    \"name\": \"temperature\",\n" +
                "                    \"type\": \"float\",\n" +
                "                    \"isDomain\": \"false\"\n" +
                "                },\n" +
                "                {\n" +
                "                    \"name\": \"pressure\",\n" +
                "                    \"type\": \"integer\",\n" +
                "                    \"isDomain\": \"false\"\n" +
                "                }\n" +
                "            ],\n" +
                "            \"providingApplication\": \"http://mysensors.com/Rooms\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"duration\": \"P1M\"\n" +
                "}";

        return json;
    }
}

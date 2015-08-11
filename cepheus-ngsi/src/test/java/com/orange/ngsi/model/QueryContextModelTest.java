/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.orange.ngsi.Util.createQueryContextTemperature;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by pborscia on 11/08/2015.
 */
public class QueryContextModelTest {

    @Test
    public void deserializationSimpleQueryContext() throws IOException {
        String json = getJsonOrionQueryContext();
        ObjectMapper mapper = new ObjectMapper();
        QueryContext queryContext = mapper.readValue(json, QueryContext.class);

        assertEquals(1, queryContext.getEntityIdList().size());
        assertEquals("Room.*", queryContext.getEntityIdList().get(0).getId());
        assertEquals("Room", queryContext.getEntityIdList().get(0).getType());
        assertEquals(true, queryContext.getEntityIdList().get(0).getIsPattern());
        assertEquals(1, queryContext.getAttributList().size());
        assertEquals("temperature", queryContext.getAttributList().get(0));
        assertNull(queryContext.getRestriction());
    }

    @Test
    public void serializationSimpleQueryContext() throws IOException {
        QueryContext queryContext = createQueryContextTemperature();
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        String json = writer.writeValueAsString(queryContext);

        List<EntityId> entityIdList = JsonPath.read(json, "$.entities[*]");
        assertEquals(1, entityIdList.size());
        assertEquals("S*", JsonPath.read(json, "$.entities[0].id"));
        assertEquals("TempSensor", JsonPath.read(json, "$.entities[0].type"));
        assertEquals(true, JsonPath.read(json, "$.entities[0].isPattern"));
        List<String> attributeList = JsonPath.read(json, "$.attributes[*]");
        assertEquals(1, attributeList.size());
        assertEquals("temp", JsonPath.read(json, "$.attributes[0]"));
    }

    private String getJsonOrionQueryContext() {
        String json = "{\n" +
                "    \"entities\": [\n" +
                "    {\n" +
                "        \"type\": \"Room\",\n" +
                "        \"isPattern\": \"true\",\n" +
                "        \"id\": \"Room.*\"\n" +
                "    }\n" +
                "    ],\n" +
                "    \"attributes\" : [\n" +
                "    \"temperature\"\n" +
                "    ]\n" +
                "}";

        return json;
    }
}

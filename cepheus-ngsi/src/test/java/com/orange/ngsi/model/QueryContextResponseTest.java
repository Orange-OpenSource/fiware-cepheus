/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by pborscia on 11/08/2015.
 */
public class QueryContextResponseTest {

    @Test
    public void deserializationSimpleQueryContextResponse() throws IOException {

        String json = getJsonOrionQueryContextResponse();
        ObjectMapper mapper = new ObjectMapper();

        QueryContextResponse queryContextResponse = mapper.readValue(json, QueryContextResponse.class);
        assertNull(queryContextResponse.getErrorCode());
    }

    private String getJsonOrionQueryContextResponse(){
        String json = "{\n" +
                "    \"contextResponses\": [\n" +
                "        {\n" +
                "            \"contextElement\": {\n" +
                "                \"attributes\": [\n" +
                "                    {\n" +
                "                        \"name\": \"temperature\",\n" +
                "                        \"type\": \"float\",\n" +
                "                        \"value\": \"23\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"id\": \"Room1\",\n" +
                "                \"isPattern\": \"false\",\n" +
                "                \"type\": \"Room\"\n" +
                "            },\n" +
                "            \"statusCode\": {\n" +
                "                \"code\": \"200\",\n" +
                "                \"reasonPhrase\": \"OK\"\n" +
                "            }\n" +
                "        },\n" +
                "        {\n" +
                "            \"contextElement\": {\n" +
                "                \"attributes\": [\n" +
                "                    {\n" +
                "                        \"name\": \"temperature\",\n" +
                "                        \"type\": \"float\",\n" +
                "                        \"value\": \"21\"\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"id\": \"Room2\",\n" +
                "                \"isPattern\": \"false\",\n" +
                "                \"type\": \"Room\"\n" +
                "            },\n" +
                "            \"statusCode\": {\n" +
                "                \"code\": \"200\",\n" +
                "                \"reasonPhrase\": \"OK\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";

        return json;
    }
}

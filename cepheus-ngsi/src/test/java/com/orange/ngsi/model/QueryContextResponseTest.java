/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.ngsi.TestConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by pborscia on 11/08/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TestConfiguration.class)
public class QueryContextResponseTest {

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void deserializationSimpleQueryContextResponse() throws IOException {

        String json = getJsonOrionQueryContextResponse();
        QueryContextResponse queryContextResponse = objectMapper.readValue(json, QueryContextResponse.class);

        assertNull(queryContextResponse.getErrorCode());
        assertEquals(2, queryContextResponse.getContextElementResponses().size());
        assertEquals(1, queryContextResponse.getContextElementResponses().get(0).getContextElement().getContextAttributeList().size());
        assertEquals("temperature", queryContextResponse.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getName());
        assertEquals("float", queryContextResponse.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getType());
        assertEquals("23", queryContextResponse.getContextElementResponses().get(0).getContextElement().getContextAttributeList().get(0).getValue());
        assertEquals("Room1", queryContextResponse.getContextElementResponses().get(0).getContextElement().getEntityId().getId());
        assertEquals("Room", queryContextResponse.getContextElementResponses().get(0).getContextElement().getEntityId().getType());
        assertEquals(false, queryContextResponse.getContextElementResponses().get(0).getContextElement().getEntityId().getIsPattern());
        assertEquals(CodeEnum.CODE_200.getLabel(), queryContextResponse.getContextElementResponses().get(0).getStatusCode().getCode());
        assertEquals(CodeEnum.CODE_200.getShortPhrase(), queryContextResponse.getContextElementResponses().get(0).getStatusCode().getReasonPhrase());
        assertEquals(1, queryContextResponse.getContextElementResponses().get(1).getContextElement().getContextAttributeList().size());
        assertEquals("temperature", queryContextResponse.getContextElementResponses().get(1).getContextElement().getContextAttributeList().get(0).getName());
        assertEquals("float", queryContextResponse.getContextElementResponses().get(1).getContextElement().getContextAttributeList().get(0).getType());
        assertEquals("21", queryContextResponse.getContextElementResponses().get(1).getContextElement().getContextAttributeList().get(0).getValue());
        assertEquals("Room2", queryContextResponse.getContextElementResponses().get(1).getContextElement().getEntityId().getId());
        assertEquals("Room", queryContextResponse.getContextElementResponses().get(1).getContextElement().getEntityId().getType());
        assertEquals(false, queryContextResponse.getContextElementResponses().get(1).getContextElement().getEntityId().getIsPattern());
        assertEquals(CodeEnum.CODE_481.getLabel(), queryContextResponse.getContextElementResponses().get(1).getStatusCode().getCode());
        assertEquals(CodeEnum.CODE_481.getShortPhrase(), queryContextResponse.getContextElementResponses().get(1).getStatusCode().getReasonPhrase());
        assertEquals("test details", queryContextResponse.getContextElementResponses().get(1).getStatusCode().getDetail());
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
                "                \"code\": \"481\",\n" +
                "                \"reasonPhrase\": \"Entity Type required\",\n" +
                "                \"details\": \"test details\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}\n";

        return json;
    }


}


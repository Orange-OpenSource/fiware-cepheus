/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.espr4fastdata.Application;
import com.orange.ngsi.model.ContextElement;
import com.orange.espr4fastdata.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by pborscia on 01/07/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ContextElementTest {

    private Util util = new Util();

    @Test
    public void convertContextElementToJsonWithoutEntityId() throws JsonProcessingException {
        ContextElement contextElement = util.createTemperatureContextElement(0);

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(contextElement);

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

        ObjectMapper mapper = new ObjectMapper();


        ContextElement contextElement = mapper.readValue(json, ContextElement.class);

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

        ObjectMapper mapper = new ObjectMapper();


        ContextElement contextElement = mapper.readValue(json, ContextElement.class);

        assertEquals("E1", contextElement.getEntityId().getId());

        assertEquals("A", contextElement.getContextAttributeList().get(0).getName());

    }


}

package com.orange.ngsi.model;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.JsonPath;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static com.orange.ngsi.Util.*;

/**
 * Test for SubscribeContext
 */
public class SubscribeContextModelTest {

    @Test
    public void deserializationSimpleSubscribeContext() throws IOException {

        String json = getJsonOrion();

        ObjectMapper mapper = new ObjectMapper();

        SubscribeContext subscribeContext = mapper.readValue(json, SubscribeContext.class);

        assertEquals(1, subscribeContext.getEntityIdList().size());
        assertEquals("Room1", subscribeContext.getEntityIdList().get(0).getId());
        assertEquals(false, subscribeContext.getEntityIdList().get(0).getIsPattern());
        assertEquals("Room", subscribeContext.getEntityIdList().get(0).getType());
        assertEquals(1, subscribeContext.getAttributeList().size());
        assertEquals("temperature", subscribeContext.getAttributeList().get(0));
        assertEquals("http://localhost:1028/accumulate", subscribeContext.getReference().toString());
        assertEquals("P1M", subscribeContext.getDuration());
        assertEquals(1, subscribeContext.getNotifyConditionList().size());
        assertEquals(NotifyConditionEnum.ONTIMEINTERVAL, subscribeContext.getNotifyConditionList().get(0).getType());
        assertEquals(1, subscribeContext.getNotifyConditionList().get(0).getCondValues().size());
        assertEquals("PT10S", subscribeContext.getNotifyConditionList().get(0).getCondValues().get(0));
    }

    @Test
    public void serializationSimpleSubscribeContext() throws IOException {

        SubscribeContext subscribeContext = null;
        try {
            subscribeContext = createSubscribeContextTemperature();
        } catch (URISyntaxException e) {
            Assert.fail(e.getMessage());
        }

        ObjectMapper mapper = new ObjectMapper();

        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());

        String json = writer.writeValueAsString(subscribeContext);

        List<EntityId> entityIdList = JsonPath.read(json,"$.entities[*]");
        assertEquals(1, entityIdList.size());
        assertEquals("Room1", JsonPath.read(json, "$.entities[0].id"));
        assertEquals("Room", JsonPath.read(json,"$.entities[0].type"));
        assertEquals(false, JsonPath.read(json,"$.entities[0].isPattern"));
        assertEquals("P1M", JsonPath.read(json, "$.duration"));
        List<String> attributes = JsonPath.read(json,"$.attributes[*]");
        assertEquals(1,attributes.size());
        assertEquals("temperature", JsonPath.read(json,"$.attributes[0]"));
        assertEquals("http://localhost:1028/accumulate", JsonPath.read(json,"$.reference"));
        List<NotifyCondition> notifyConditionList = JsonPath.read(json,"$.notifyConditions[*]");
        assertEquals(1, notifyConditionList.size());
        assertEquals(NotifyConditionEnum.ONTIMEINTERVAL.getLabel(),JsonPath.read(json,"$.notifyConditions[0].type"));
        List<String> condValues = JsonPath.read(json,"$.notifyConditions[0].condValues[*]");
        assertEquals(1,condValues.size());
        assertEquals("PT10S",JsonPath.read(json,"$.notifyConditions[0].condValues[0]"));
    }

    private String getJsonOrion(){
        String json = "{\n" +
                "    \"entities\": [\n" +
                "        {\n" +
                "            \"type\": \"Room\",\n" +
                "            \"isPattern\": \"false\",\n" +
                "            \"id\": \"Room1\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"attributes\": [\n" +
                "        \"temperature\"\n" +
                "    ],\n" +
                "    \"reference\": \"http://localhost:1028/accumulate\",\n" +
                "    \"duration\": \"P1M\",\n" +
                "    \"notifyConditions\": [\n" +
                "        {\n" +
                "            \"type\": \"ONTIMEINTERVAL\",\n" +
                "            \"condValues\": [\n" +
                "                \"PT10S\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        return json;
    }
}

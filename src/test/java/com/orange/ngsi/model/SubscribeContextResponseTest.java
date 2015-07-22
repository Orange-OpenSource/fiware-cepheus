package com.orange.ngsi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.ngsi.model.SubscribeContextResponse;
import com.orange.espr4fastdata.util.Util;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by pborscia on 21/07/2015.
 */
public class SubscribeContextResponseTest {
    Util util = new Util();


    @Test
    public void deserializationSimpleSubscribeContextResponse() throws IOException {

        String json = getJsonOrion();

        ObjectMapper mapper = new ObjectMapper();

        SubscribeContextResponse subscribeContextResponse = mapper.readValue(json, SubscribeContextResponse.class);

        assertNull(subscribeContextResponse.getSubscribeError());
        assertEquals("P1M", subscribeContextResponse.getSubscribeResponse().getDuration());
        assertEquals("51c0ac9ed714fb3b37d7d5a8", subscribeContextResponse.getSubscribeResponse().getSubscriptionId());
        assertEquals("PT5S", subscribeContextResponse.getSubscribeResponse().getThrottling());

    }

    private String getJsonOrion(){
        String json = "{\n" +
                "    \"subscribeResponse\": {\n" +
                "        \"duration\": \"P1M\",\n" +
                "        \"subscriptionId\": \"51c0ac9ed714fb3b37d7d5a8\",\n" +
                "        \"throttling\": \"PT5S\"\n" +
                "    }\n" +
                "}";

        return json;
    }

}

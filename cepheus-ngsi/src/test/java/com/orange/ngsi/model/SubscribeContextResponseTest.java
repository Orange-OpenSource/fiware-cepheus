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
 * Tests for SubscribeContextResponse
 */
public class SubscribeContextResponseTest {

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

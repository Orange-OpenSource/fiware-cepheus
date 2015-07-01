package com.orange.espr4fastdata.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.ngsi.ContextElement;
import com.orange.espr4fastdata.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

}

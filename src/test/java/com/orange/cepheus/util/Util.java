package com.orange.cepheus.util;

import com.orange.cepheus.model.*;
import com.orange.ngsi.model.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.orange.ngsi.model.CodeEnum.CODE_200;

/**
 * Helpers for tests
 */
public class Util {

    static public Configuration getBasicConf() {
        Configuration configuration = new Configuration();
        try {
            configuration.setHost(new URI("http://localhost:8080"));
        } catch (URISyntaxException e) {
        }

        // eventIN 1
        EventTypeIn eventTypeIn = new EventTypeIn("S.*", "TempSensor", true);
        eventTypeIn.addProvider("http://iotAgent");
        Attribute attr = new Attribute("temp", "double");
        attr.setMetadata(Collections.singleton(new Metadata("unit", "string")));
        eventTypeIn.addAttribute(attr);
        configuration.setEventTypeIns(Collections.singletonList(eventTypeIn));

        //eventOUT
        EventTypeOut eventTypeOut = new EventTypeOut("OUT1", "TempSensorAvg", false);
        eventTypeOut.addBroker(new Broker("http://orion", false));
        Attribute outAttribute = new Attribute("avgTemp", "double");
        outAttribute.setMetadata(Collections.singleton(new Metadata("unit", "string")));
        eventTypeOut.addAttribute(outAttribute);
        configuration.setEventTypeOuts(Collections.singletonList(eventTypeOut));

        //rules
        List<String> rules = new ArrayList<>();
        rules.add("INSERT INTO TempSensorAvg SELECT 'OUT1' as id, avg(temp) as avgTemp, temp_unit as avgTemp_unit FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1' ");
        configuration.setStatements(rules);

        return configuration;
    }

    static public Event buildBasicEvent(Object value) {
        Event e = new Event("TempSensor");
        e.addValue("id", "S1");
        e.addValue("temp", value);
        e.addValue("temp_unit", "celcius");
        return e;
    }

    static public NotifyContext createNotifyContextTempSensor(float randomValue) throws URISyntaxException {

        NotifyContext notifyContext = new NotifyContext("1", new URI("http://iotAgent"));
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        contextElementResponse.setContextElement(createTemperatureContextElement(randomValue));
        contextElementResponse.setStatusCode(new StatusCode(CODE_200));
        notifyContext.setContextElementResponseList(Collections.singletonList(contextElementResponse));

        return notifyContext;
    }

    static public ContextElement createTemperatureContextElement(float randomValue) {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("S1", "TempSensor", false));
        ContextAttribute contextAttribute = new ContextAttribute("temp", "float", 15.5 + randomValue);
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
        return contextElement;
    }

    static public UpdateContext createUpdateContextTempSensor(float randomValue) throws URISyntaxException {
        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        updateContext.setContextElements(Collections.singletonList(createTemperatureContextElement(randomValue)));
        return updateContext;
    }

    static public UpdateContextResponse createUpdateContextResponseTempSensor() throws URISyntaxException {
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        contextElementResponse.setContextElement(createTemperatureContextElement(0));
        contextElementResponse.setStatusCode(new StatusCode(CODE_200));

        UpdateContextResponse updateContextResponse = new UpdateContextResponse();
        updateContextResponse.setErrorCode(new StatusCode(CodeEnum.CODE_200));
        updateContextResponse.setContextElementResponses(Collections.singletonList(contextElementResponse));
        return updateContextResponse;
    }

    static public ContextElement createPressureContextElement() {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("P1", "PressureSensor", false));
        ContextAttribute contextAttribute = new ContextAttribute("pressure", "int", 999);
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
        return contextElement;
    }

    static public UpdateContext createUpdateContextPressureSensor() throws URISyntaxException {
        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        updateContext.setContextElements(Collections.singletonList(createPressureContextElement()));
        return updateContext;
    }

    static public ContextElement createWrongAttributTemperatureContextElement(float randomValue) {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("S1", "TempSensor", false));
        ContextAttribute contextAttribute = new ContextAttribute("pressure", "string", "low");
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
        return contextElement;
    }

    static public UpdateContext createUpdateContextTempSensorWithWrongAttribut(float randomValue) throws URISyntaxException {
        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        updateContext.setContextElements(Collections.singletonList(createWrongAttributTemperatureContextElement(randomValue)));
        return updateContext;
    }

    static public SubscribeContext createSubscribeContextTemperature() throws URISyntaxException {
        SubscribeContext subscribeContext = new SubscribeContext();

        List<EntityId> entityIdList = new ArrayList<>();
        EntityId entityId = new EntityId("Room1","Room",false);
        entityIdList.add(entityId);
        subscribeContext.setEntityIdList(entityIdList);

        List<String> attributes = new ArrayList<>();
        attributes.add("temperature");
        subscribeContext.setAttributeList(attributes);


        subscribeContext.setReference(new URI("http://localhost:1028/accumulate"));

        subscribeContext.setDuration("P1M");

        List<NotifyCondition> notifyConditionList = new ArrayList<>();
        List<String> condValues = new ArrayList<>();
        condValues.add("PT10S");
        NotifyCondition notifyCondition = new NotifyCondition(NotifyConditionEnum.ONTIMEINTERVAL,condValues);
        notifyConditionList.add(notifyCondition);
        subscribeContext.setNotifyConditionList(notifyConditionList);

        return subscribeContext;
    }

    static public SubscribeContextResponse createSubscribeContextResponseTemperature() {
        SubscribeContextResponse subscribeContextResponse = new SubscribeContextResponse();

        SubscribeResponse subscribeResponse = new SubscribeResponse();
        subscribeResponse.setDuration("P1M");
        subscribeResponse.setSubscriptionId("12345678");
        subscribeContextResponse.setSubscribeResponse(subscribeResponse);
        return subscribeContextResponse;
    }

    static public String json(MappingJackson2HttpMessageConverter mapping, Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mapping.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}

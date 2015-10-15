package com.orange.ngsi;

import com.orange.ngsi.model.*;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.orange.ngsi.model.CodeEnum.CODE_200;

/**
 * Helpers for tests
 */
public class Util {

    static public NotifyContext createNotifyContextTempSensor(float randomValue) throws URISyntaxException {

        NotifyContext notifyContext = new NotifyContext("1", new URI("http://iotAgent"));
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        contextElementResponse.setContextElement(createTemperatureContextElement(randomValue));
        contextElementResponse.setStatusCode(new StatusCode(CODE_200));
        notifyContext.setContextElementResponseList(Collections.singletonList(contextElementResponse));

        return notifyContext;
    }

    static public NotifyContextResponse createNotifyContextResponseTempSensor() {
        NotifyContextResponse notifyContextResponse = new NotifyContextResponse();
        notifyContextResponse.setResponseCode(new StatusCode(CODE_200));
        return notifyContextResponse;
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
        updateContextResponse.setErrorCode(new StatusCode(CODE_200));
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

        Restriction restriction = new Restriction();
        restriction.setAttributeExpression("xpath/expression");
        restriction.setScopes(Collections.singletonList(new OperationScope("type", "value")));
        subscribeContext.setRestriction(restriction);

        subscribeContext.setThrottling("PT1S");

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

    static public RegisterContext createRegisterContextTemperature() throws URISyntaxException {
        RegisterContext registerContext = new RegisterContext();

        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:1028/accumulate"));
        EntityId entityId = new EntityId("Room*", "Room", true);
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        ContextRegistrationAttribute attribute = new ContextRegistrationAttribute("temperature", false);
        attribute.setType("float");
        contextRegistration.setContextRegistrationAttributeList(Collections.singletonList(attribute));
        registerContext.setContextRegistrationList(Collections.singletonList(contextRegistration));
        registerContext.setDuration("PT10S");

        return registerContext;
    }

    static public RegisterContextResponse createRegisterContextResponseTemperature() throws URISyntaxException {
        RegisterContextResponse registerContextResponse = new RegisterContextResponse();

        registerContextResponse.setDuration("PT10S");
        registerContextResponse.setRegistrationId("123456789");

        return registerContextResponse;
    }

    static public UnsubscribeContext createUnsubscribeContext() {
        return new UnsubscribeContext("12345678");
    }

    static public QueryContext createQueryContextTemperature() {
        QueryContext queryContext = new QueryContext();
        queryContext.setEntityIdList(Collections.singletonList(new EntityId("S*", "TempSensor", true)));
        queryContext.setAttributeList(Collections.singletonList("temp"));
        return queryContext;
    }

    static public QueryContextResponse createQueryContextResponseTemperature() {
        QueryContextResponse queryContextResponse = new QueryContextResponse();
        ContextElementResponse contextElementResponse = new ContextElementResponse(createTemperatureContextElement(0), new StatusCode(CODE_200));
        queryContextResponse.setContextElementResponses(Collections.singletonList(contextElementResponse));
        return queryContextResponse;
    }

    static public String json(MappingJackson2HttpMessageConverter mapping, Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mapping.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    static public String xml(MappingJackson2XmlHttpMessageConverter mapping, Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        mapping.write(o, MediaType.APPLICATION_XML, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}

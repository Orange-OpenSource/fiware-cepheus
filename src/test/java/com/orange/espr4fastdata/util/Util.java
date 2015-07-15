package com.orange.espr4fastdata.util;


import com.orange.espr4fastdata.model.cep.*;
import com.orange.espr4fastdata.model.ngsi.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by pborscia on 05/06/2015.
 */
public class Util {

    public Util() {
    }

    public Configuration getBasicConf(){
        Configuration configuration = new Configuration();
        configuration.setHost("http://localhost:8080");
        //eventIN
        EventTypeIn eventTypeIn = new EventTypeIn("S.*", "TempSensor", true);
        eventTypeIn.addProvider("http://iotAgent");
        eventTypeIn.addAttribute(new Attribute("temp", "float"));
        configuration.setEventTypeIns(Collections.singletonList(eventTypeIn));
        //eventOUT
        EventTypeOut eventTypeOut = new EventTypeOut("OUT1", "TempSensorAvg", false);
        eventTypeOut.addBroker(new Broker("http://orion", false));
        eventTypeOut.addAttribute(new Attribute("avgTemp", "double"));
        configuration.setEventTypeOuts(Collections.singletonList(eventTypeOut));

        //rules
        List<String> rules = new ArrayList<>();
        rules.add("INSERT INTO TempSensorAvg SELECT 'OUT1' as id, avg(TempSensor.temp) as avgTemp FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1' ");
        configuration.setStatements(rules);

        return configuration;
    }

    public NotifyContext createNotifyContextTempSensor(float randomValue) throws URISyntaxException {

        NotifyContext notifyContext = new NotifyContext("1", new URI("http://iotAgent"));
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        contextElementResponse.setContextElement(createTemperatureContextElement(randomValue));
        notifyContext.setContextElementResponseList(Collections.singletonList(contextElementResponse));

        return notifyContext;
    }

    public ContextElement createTemperatureContextElement(float randomValue) {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("S1", "TempSensor", false));
        //ContextAttribute contextAttribute = new ContextAttribute("temp", "float", Double.toString(15.5 + randomValue));
        ContextAttribute contextAttribute = new ContextAttribute("temp", "float");
        contextAttribute.set("value", 15.5 + randomValue);
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
        return contextElement;
    }

    public UpdateContext createUpdateContextTempSensor(float randomValue) throws URISyntaxException {
        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        updateContext.setContextElements(Collections.singletonList(createTemperatureContextElement(randomValue)));
        return updateContext;
    }

    public UpdateContextResponse createUpdateContextResponseTempSensor() throws URISyntaxException {
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        contextElementResponse.setContextElement(createTemperatureContextElement(0));
        contextElementResponse.setStatusCode(new StatusCode(CodeEnum.CODE_200));

        UpdateContextResponse updateContextResponse = new UpdateContextResponse();
        updateContextResponse.setErrorCode(new StatusCode(CodeEnum.CODE_200));
        updateContextResponse.setContextElementResponses(Collections.singletonList(contextElementResponse));
        return updateContextResponse;
    }

    public ContextElement createPressureContextElement() {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("P1", "PressureSensor", false));
        //ContextAttribute contextAttribute = new ContextAttribute("pressure", "int", "999");
        ContextAttribute contextAttribute = new ContextAttribute("pressure", "int");
        contextAttribute.set("value", 999);
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
        return contextElement;
    }

    public UpdateContext createUpdateContextPressureSensor() throws URISyntaxException {
        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        updateContext.setContextElements(Collections.singletonList(createPressureContextElement()));
        return updateContext;
    }

    public ContextElement createWrongAttributTemperatureContextElement(float randomValue) {
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId("S1", "TempSensor", false));
        //ContextAttribute contextAttribute = new ContextAttribute("pressure", "string", "low");
        ContextAttribute contextAttribute = new ContextAttribute("pressure", "string");
        contextAttribute.set("value", "low");
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
        return contextElement;
    }

    public UpdateContext createUpdateContextTempSensorWithWrongAttribut(float randomValue) throws URISyntaxException {
        UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);
        updateContext.setContextElements(Collections.singletonList(createWrongAttributTemperatureContextElement(randomValue)));
        return updateContext;
    }



}

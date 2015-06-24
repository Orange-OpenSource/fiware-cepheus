package com.orange.newespr4fastdata.util;


import com.orange.newespr4fastdata.model.*;
import com.orange.newespr4fastdata.model.cep.Attribute;
import com.orange.newespr4fastdata.model.cep.Configuration;
import com.orange.newespr4fastdata.model.cep.EventTypeIn;
import com.orange.newespr4fastdata.model.cep.EventTypeOut;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        List<EventTypeIn> eventTypeIns = new ArrayList<EventTypeIn>();
        configuration.setEventTypeIns(eventTypeIns);
        EventTypeIn eventTypeIn = new EventTypeIn();
        eventTypeIns.add(eventTypeIn);
        eventTypeIn.setProvider("http://iotAgent");
        eventTypeIn.setId("S.*");
        eventTypeIn.setType("TempSensor");
        eventTypeIn.setIsPattern(true);
        List<Attribute> attributes = new ArrayList<Attribute>();
        Attribute attributeTemp = new Attribute();
        attributeTemp.setName("temp");
        attributeTemp.setType("float");
        attributes.add(attributeTemp);
        eventTypeIn.setAttributes(attributes);
        //eventOUT
        List<EventTypeOut> eventTypeOuts = new ArrayList<EventTypeOut>();
        configuration.setEventTypeOuts(eventTypeOuts);
        EventTypeOut eventTypeOut = new EventTypeOut();
        eventTypeOuts.add(eventTypeOut);
        eventTypeOut.setBroker("http://orion");
        eventTypeOut.setId("OUT1");
        eventTypeOut.setType("TempSensorAvg");
        eventTypeOut.setIsPattern(false);
        List<Attribute> outAttributes = new ArrayList<Attribute>();
        Attribute attributeAvgTemp = new Attribute();
        attributeAvgTemp.setName("avgTemp");
        attributeAvgTemp.setType("double");
        outAttributes.add(attributeAvgTemp);
        eventTypeOut.setAttributes(outAttributes);

        //rules
        List<String> rules = new ArrayList<String>();
        rules.add("INSERT INTO TempSensorAvg SELECT 'OUT1' as id, avg(TempSensor.temp) as avgTemp FROM TempSensor.win:time(2 seconds) WHERE TempSensor.id = 'S1' ");
        configuration.setRules(rules);

        return configuration;
    }

    public NotifyContext createNotifyContextTempSensor(float randomValue) throws URISyntaxException {

        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("1");
        notifyContext.setOriginator(new URI("http://iotAgent"));
        List<ContextElementResponse> contextElementResponses = new ArrayList<ContextElementResponse>();
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        contextElementResponse.setContextElement(createTemperatureContextElement(randomValue));
        contextElementResponses.add(contextElementResponse);
        notifyContext.setContextElementResponseList(contextElementResponses);

        return notifyContext;
    }

    public ContextElement createTemperatureContextElement(float randomValue) {

        ContextElement contextElement = new ContextElement();

        EntityId entityId = new EntityId();
        entityId.setId("S1");
        entityId.setType("TempSensor");
        entityId.setIsPattern(false);
        contextElement.setEntityId(entityId);

        List<ContextAttribute> contextAttributes = new ArrayList<ContextAttribute>();
        ContextAttribute contextAttribute = new ContextAttribute();
        contextAttribute.setName("temp");
        contextAttribute.setType("float");
        float value = (float) (15.5 + randomValue);
        contextAttribute.setContextValue(Float.toString(value));
        contextAttributes.add(contextAttribute);
        contextElement.setContextAttributeList(contextAttributes);

        return contextElement;


    }

    public UpdateContext createUpdateContextTempSensor(float randomValue) throws URISyntaxException {

        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);

        List<ContextElement> contextElements = new ArrayList<ContextElement>();
        contextElements.add(createTemperatureContextElement(randomValue));
        updateContext.setContextElements(contextElements);

        return updateContext;
    }

    public UpdateContextResponse createUpdateContextResponseTempSensor() throws URISyntaxException {

        UpdateContextResponse updateContextResponse = new UpdateContextResponse();
        updateContextResponse.setErrorCode(StatusCode.CODE_200);
        List<ContextElementResponse> contextElementResponses = new ArrayList<ContextElementResponse>();
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        ContextElement contextElement = createTemperatureContextElement(0);
        contextElementResponse.setContextElement(contextElement);
        contextElementResponse.setStatusCode(StatusCode.CODE_200);
        contextElementResponses.add(contextElementResponse);
        updateContextResponse.setContextElementResponses(contextElementResponses);


        return updateContextResponse;
    }

}

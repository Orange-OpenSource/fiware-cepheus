/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.orange.espr4fastdata.model.cep.Attribute;
import com.orange.espr4fastdata.model.cep.Broker;
import com.orange.espr4fastdata.model.cep.Configuration;
import com.orange.espr4fastdata.model.cep.EventTypeOut;
import com.orange.espr4fastdata.model.ngsi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by pborscia on 04/06/2015.
 */
@Component
public class EventSinkListener  implements StatementAwareUpdateListener {

    private static Logger logger = LoggerFactory.getLogger(EventSinkListener.class);

    @Autowired
    private Sender sender;


    private Configuration configuration;


    @Override
    public void update(EventBean[] eventBeans, EventBean[] eventBeans1, EPStatement epStatement, EPServiceProvider epServiceProvider) {

        int eventBeansSize = -1;
        int eventBeans1Size = -1;

        if (eventBeans != null) {
            eventBeansSize = eventBeans.length;
        }

        if (eventBeans1 != null){
            eventBeans1Size = eventBeans1.length;
        }


        logger.debug("TRIGGER LEVE for {}, size eventBeans {}, size eventBeans1 {}", epStatement.getName(), eventBeansSize, eventBeans1Size);


        for (int i=0; i<eventBeansSize; i++) {
            EventBean eventBean = eventBeans[i];
            logger.debug("EventType {} eventBean {}", eventBeans[i].getEventType().getName(), eventBeans[i].toString());

            for (String propertyName : eventBean.getEventType().getPropertyNames()) {
                logger.info("property {} value {} ", propertyName, eventBean.get(propertyName));
            }

            //send to context broker
            EventTypeOut eventTypeOut = getEventTypeOut(eventBean.getEventType().getName());
            if (eventTypeOut == null) {
                logger.warn("EventTypeOut {} doesn't exist in Configuration, not updateContext sended", eventBean.getEventType().getName());
            } else {
                UpdateContext updateContext = getUpdateContext(eventBean, eventTypeOut);

                for (Broker broker: eventTypeOut.getBrokers()) {
                    UpdateContextResponse updateContextResponse = sender.postMessage(updateContext, broker);
                }
            }


        }


    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public UpdateContext getUpdateContext(EventBean eventBean, EventTypeOut eventTypeOut) {

            UpdateContext updateContext = new UpdateContext(UpdateAction.UPDATE);

            List<ContextElement> contextElements = new ArrayList<>();
            updateContext.setContextElements(contextElements);

            ContextElement contextElement = new ContextElement();
            contextElement.setEntityId(new EntityId(eventTypeOut.getId(), eventTypeOut.getType(), eventTypeOut.isPattern()));

            for (Attribute attribute:eventTypeOut.getAttributes()){

                //ContextAttribute contextAttribute = new ContextAttribute(attribute.getName(), attribute.getType(), String.valueOf(eventBean.get(attribute.getName())));
                ContextAttribute contextAttribute = new ContextAttribute(attribute.getName(), attribute.getType());
                contextAttribute.set("value", eventBean.get(attribute.getName()));
                contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
            }

            contextElements.add(contextElement);

            return updateContext;

    }

    private EventTypeOut getEventTypeOut(String eventBeanType) {

        for(EventTypeOut eventTypeOut: this.configuration.getEventTypeOuts()) {
            if (eventTypeOut.getType().equals(eventBeanType)) {
                return eventTypeOut;
            }
        }
        return null;
    }




}

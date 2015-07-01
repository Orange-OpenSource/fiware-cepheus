package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.*;
import com.orange.espr4fastdata.Application;
import com.orange.espr4fastdata.model.cep.Attribute;
import com.orange.espr4fastdata.model.cep.Broker;
import com.orange.espr4fastdata.model.cep.EventTypeOut;
import com.orange.espr4fastdata.model.ngsi.UpdateAction;
import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import com.orange.espr4fastdata.util.Util;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by pborscia on 01/07/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class EventSinkListenerTest {


    @Autowired
    private EsperEventProcessor esperEventProcessor;

    private Util util = new Util();

    @Test
    public void getUpdateContextTempSensor(){
        esperEventProcessor.setConfiguration(util.getBasicConf());

        UpdateContext updateContext = esperEventProcessor.eventSinkListener.getUpdateContext(getEventBean(), getEventTypeOut());

        assertEquals(UpdateAction.UPDATE,updateContext.getUpdateAction());
        assertEquals(1,updateContext.getContextElements().size());

        assertEquals("OUT1",updateContext.getContextElements().get(0).getEntityId().getId());
        assertEquals("TempSensorAvg",updateContext.getContextElements().get(0).getEntityId().getType());
        assertFalse(updateContext.getContextElements().get(0).getEntityId().getIsPattern());

        assertEquals(1,updateContext.getContextElements().get(0).getContextAttributeList().size());
        assertEquals("avgTemp",updateContext.getContextElements().get(0).getContextAttributeList().get(0).getName());
        assertEquals("double",updateContext.getContextElements().get(0).getContextAttributeList().get(0).getType());
        assertEquals("10.25",updateContext.getContextElements().get(0).getContextAttributeList().get(0).getContextValue());

    }


    private EventTypeOut getEventTypeOut(){

        EventTypeOut eventTypeOut = new EventTypeOut("OUT1", "TempSensorAvg", false);
        eventTypeOut.addBroker(new Broker("http://orion", false));
        eventTypeOut.addAttribute(new Attribute("avgTemp", "double"));

        return eventTypeOut;
    }

    private EventBean getEventBean(){
        return new EventBean() {
            @Override
            public EventType getEventType() {
                EventType eventType = new EventType() {
                    @Override
                    public Class getPropertyType(String s) {
                        return null;
                    }

                    @Override
                    public boolean isProperty(String s) {
                        return false;
                    }

                    @Override
                    public EventPropertyGetter getGetter(String s) {
                        return null;
                    }

                    @Override
                    public FragmentEventType getFragmentType(String s) {
                        return null;
                    }

                    @Override
                    public Class getUnderlyingType() {
                        return null;
                    }

                    @Override
                    public String[] getPropertyNames() {
                        return new String[0];
                    }

                    @Override
                    public EventPropertyDescriptor[] getPropertyDescriptors() {
                        return new EventPropertyDescriptor[0];
                    }

                    @Override
                    public EventPropertyDescriptor getPropertyDescriptor(String s) {
                        return null;
                    }

                    @Override
                    public EventType[] getSuperTypes() {
                        return new EventType[0];
                    }

                    @Override
                    public Iterator<EventType> getDeepSuperTypes() {
                        return null;
                    }

                    @Override
                    public String getName() {
                        return "TempSensorAvg";
                    }

                    @Override
                    public EventPropertyGetterMapped getGetterMapped(String s) {
                        return null;
                    }

                    @Override
                    public EventPropertyGetterIndexed getGetterIndexed(String s) {
                        return null;
                    }

                    @Override
                    public int getEventTypeId() {
                        return 0;
                    }

                    @Override
                    public String getStartTimestampPropertyName() {
                        return null;
                    }

                    @Override
                    public String getEndTimestampPropertyName() {
                        return null;
                    }
                };
                return eventType;
            }

            @Override
            public Object get(String s) throws PropertyAccessException {
                return (double)10.25;
            }

            @Override
            public Object getUnderlying() {
                return null;
            }

            @Override
            public Object getFragment(String s) throws PropertyAccessException {
                return null;
            }
        };


    }



















































}

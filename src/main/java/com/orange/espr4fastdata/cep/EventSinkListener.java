package com.orange.espr4fastdata.cep;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by pborscia on 04/06/2015.
 */
public class EventSinkListener  implements StatementAwareUpdateListener {

    private static Logger logger = LoggerFactory.getLogger(EventSinkListener.class);

    @Autowired
    private Sender sender;

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

        logger.info("TRIGGER LEVE for {}, size eventBeans {}, size eventBeans1 {}", epStatement.getName(), eventBeansSize, eventBeans1Size);

        for (int i=0; i<eventBeansSize; i++) {
            EventBean eventBean = eventBeans[0];
            logger.info("EventType {} eventBean {}", eventBeans[0].getEventType().getName(), eventBeans[0].toString());

            for (String propertyName : eventBean.getEventType().getPropertyNames()) {
                logger.info("property {} value {} ", propertyName, eventBean.get(propertyName));
            }

            //send to context broker
            /*UpdateContext updateContext = new UpdateContext();
            UpdateContextResponse updateContextResponse = sender.postMessage(updateContext,"URI broker")*/



        }


    }



}

package com.orange.ngsi.server;

import com.orange.ngsi.exception.MissingRequestParameterException;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by pborscia on 07/08/2015.
 */
@Component
public class NgsiValidation {

    private static Logger logger = LoggerFactory.getLogger(NgsiValidation.class);

    public void checkUpdateContext(UpdateContext updateContext) throws MissingRequestParameterException {

        if (updateContext.getUpdateAction() == null) {
            throw new MissingRequestParameterException("updateAction", "UpdateAction");
        }
        if ((updateContext.getContextElements() == null) && (!updateContext.getUpdateAction().isDelete())) {
            throw new MissingRequestParameterException("contextElements", "List<ContextElement>");
        }
        if (updateContext.getContextElements().isEmpty() && (!updateContext.getUpdateAction().isDelete())) {
            throw new MissingRequestParameterException("contextElements", "List<ContextElement>");
        }
        for (ContextElement contextElement : updateContext.getContextElements()) {
            checkContextElement(contextElement);
        }
    }

    public void checkNotifyContext(NotifyContext notifyContext) throws MissingRequestParameterException {

        if ((notifyContext.getSubscriptionId() == null) || (notifyContext.getSubscriptionId().isEmpty())) {
            throw new MissingRequestParameterException("subscriptionId", "string");
        }
        if ((notifyContext.getOriginator() == null) || (notifyContext.getOriginator().toString().isEmpty())){
            throw new MissingRequestParameterException("originator", "URI");
        }
        if (notifyContext.getContextElementResponseList() == null)  {
            throw new MissingRequestParameterException("contextElementResponse", "List<ContextElementResponse>");
        }
        for (ContextElementResponse contextElementResponse : notifyContext.getContextElementResponseList()) {
            checkContextElementResponse(contextElementResponse);
        }
    }

    public void checkRegisterContext(RegisterContext registerContext) throws MissingRequestParameterException {

        if (registerContext.getContextRegistrationList() == null)  {
            throw new MissingRequestParameterException("contextRegistrations", "List<ContextRegistration>");
        }
        for (ContextRegistration contextRegistration : registerContext.getContextRegistrationList()) {
            checkContextRegistration(contextRegistration);
        }
    }

    private void checkContextElementResponse(ContextElementResponse contextElementResponse) throws MissingRequestParameterException {

        if (contextElementResponse.getStatusCode() == null) {
            throw new MissingRequestParameterException("statusCode", "StatusCode");
        }
        if (contextElementResponse.getContextElement() == null) {
            throw new MissingRequestParameterException("contextElement", "ContextElement");
        }
        checkContextElement(contextElementResponse.getContextElement());
    }

    private void checkContextElement(ContextElement contextElement) throws MissingRequestParameterException {

        if (contextElement.getEntityId() == null) {
            throw new MissingRequestParameterException("entityId", "EntityId");
        }
        checkEntityId(contextElement.getEntityId());
        if (contextElement.getContextAttributeList() == null) {
            throw new MissingRequestParameterException("contextAttributes", "List<ContextAttribut>");
        }
    }

    private void checkEntityId(EntityId entityId) throws MissingRequestParameterException {

        if ((entityId.getId() == null) || (entityId.getId().isEmpty())) {
            throw new MissingRequestParameterException("id", "string");
        }
        if ((entityId.getType() == null) || (entityId.getType().isEmpty())) {
            throw new MissingRequestParameterException("type", "string");
        }
        if (entityId.getIsPattern() == null)  {
            entityId.setIsPattern(false);
        }
    }

    private void checkContextRegistration(ContextRegistration contextRegistration) throws MissingRequestParameterException {
        if ((contextRegistration.getProvidingApplication() == null) || (contextRegistration.getProvidingApplication().toString().isEmpty())){
            throw new MissingRequestParameterException("providingApplication", "URI");
        }
        if (contextRegistration.getEntityIdList() != null) {
            for(EntityId entityId: contextRegistration.getEntityIdList()) {
                checkEntityId(entityId);
            }
        }
        if (contextRegistration.getContextRegistrationAttributeList() != null) {
            for(ContextRegistrationAttribute attribute: contextRegistration.getContextRegistrationAttributeList()) {
                checkContextRegistrationAttribute(attribute);
            }
        }
    }

    private void checkContextRegistrationAttribute(ContextRegistrationAttribute attribute) throws MissingRequestParameterException {
        if ((attribute.getName() == null) || (attribute.getName().isEmpty())) {
            throw new MissingRequestParameterException("name", "string");
        }
        if (attribute.getIsDomain() == null)  {
            throw new MissingRequestParameterException("isDomain", "boolean");
        }
    }
}

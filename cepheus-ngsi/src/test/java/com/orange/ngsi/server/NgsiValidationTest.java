/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.orange.ngsi.exception.MismatchIdException;
import com.orange.ngsi.exception.MissingRequestParameterException;
import com.orange.ngsi.exception.UnsupportedOperationException;
import com.orange.ngsi.model.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.orange.ngsi.model.CodeEnum.CODE_200;

/**
 * Tests for the NGSI validation
 */
public class NgsiValidationTest {

    static NgsiValidation ngsiValidation = new NgsiValidation();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    // Tests for validation of updateContext
    @Test
    public void missingUpdateActionInUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("updateAction");
        ngsiValidation.checkUpdateContext(updateContext);
    }

    @Test
    public void emptyContextElementInUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.DELETE);
        List<ContextElement> contextElements = new ArrayList<>();
        updateContext.setContextElements(contextElements);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextElements");
        ngsiValidation.checkUpdateContext(updateContext);
    }

    @Test
    public void nullContextElementInUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextElements");
        ngsiValidation.checkUpdateContext(updateContext);
    }

    @Test
    public void emptyEntityIdUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);
        ContextElement contextElement = new ContextElement();
        EntityId entityId = new EntityId("", "type", false);
        contextElement.setEntityId(entityId);
        List<ContextElement> contextElements = new ArrayList<>();
        contextElements.add(contextElement);
        updateContext.setContextElements(contextElements);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkUpdateContext(updateContext);
    }

    @Test
    public void nullEntityIdUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);
        ContextElement contextElement = new ContextElement();
        EntityId entityId = new EntityId();
        contextElement.setEntityId(entityId);
        List<ContextElement> contextElements = new ArrayList<>();
        contextElements.add(contextElement);
        updateContext.setContextElements(contextElements);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkUpdateContext(updateContext);
    }

    @Test
    public void emptyEntityTypeUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);
        ContextElement contextElement = new ContextElement();
        EntityId entityId = new EntityId("id", "", false);
        contextElement.setEntityId(entityId);
        List<ContextElement> contextElements = new ArrayList<>();
        contextElements.add(contextElement);
        updateContext.setContextElements(contextElements);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkUpdateContext(updateContext);
    }

    @Test
    public void nullEntityTypeUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);
        ContextElement contextElement = new ContextElement();
        EntityId entityId = new EntityId();
        entityId.setId("id");
        contextElement.setEntityId(entityId);
        List<ContextElement> contextElements = new ArrayList<>();
        contextElements.add(contextElement);
        updateContext.setContextElements(contextElements);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkUpdateContext(updateContext);
    }

    // Tests for validation of notifyContext
    @Test
    public void nullSubscriptionIdInNotifyContext() throws MissingRequestParameterException {
        NotifyContext notifyContext = new NotifyContext();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("subscriptionId");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    @Test
    public void emptySubscriptionIdInNotifyContext() throws MissingRequestParameterException {
        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("");
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("subscriptionId");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    @Test
    public void nullOriginatorInNotifyContext() throws MissingRequestParameterException {
        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("subscriptionId");
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("originator");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    @Test
    public void emptyOriginatorInNotifyContext() throws MissingRequestParameterException, URISyntaxException {
        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("subscriptionId");
        notifyContext.setOriginator(new URI(""));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("originator");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    @Test
    public void nullContextElementResponseListInNotifyContext() throws MissingRequestParameterException, URISyntaxException {
        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("subscriptionId");
        notifyContext.setOriginator(new URI("http://iotAgent/"));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextElementResponse");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    @Test
    public void emptyContextElementResponseListInNotifyContext() throws MissingRequestParameterException, URISyntaxException {
        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("subscriptionId");
        notifyContext.setOriginator(new URI("http://iotAgent/"));
        List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
        notifyContext.setContextElementResponseList(contextElementResponseList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextElementResponse");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    @Test
    public void nullStatusCodeInNotifyContext() throws MissingRequestParameterException, URISyntaxException {
        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("subscriptionId");
        notifyContext.setOriginator(new URI("http://iotAgent/"));
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        notifyContext.setContextElementResponseList(Collections.singletonList(contextElementResponse));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("statusCode");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    @Test
    public void nullContextElementInNotifyContext() throws MissingRequestParameterException, URISyntaxException {
        NotifyContext notifyContext = new NotifyContext();
        notifyContext.setSubscriptionId("subscriptionId");
        notifyContext.setOriginator(new URI("http://iotAgent/"));
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        contextElementResponse.setStatusCode(new StatusCode(CODE_200));
        notifyContext.setContextElementResponseList(Collections.singletonList(contextElementResponse));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextElement");
        ngsiValidation.checkNotifyContext(notifyContext);
    }

    // Tests for validation of registerContext
    @Test
    public void nullContextRegistrationListInRegisterContext() throws MissingRequestParameterException {
        RegisterContext registerContext = new RegisterContext();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextRegistrations");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void emptyContextRegistrationListInRegisterContext() throws MissingRequestParameterException {
        RegisterContext registerContext = new RegisterContext();
        List<ContextRegistration> contextRegistrationList = new ArrayList<>();
        registerContext.setContextRegistrationList(contextRegistrationList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextRegistrations");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void nullProvidingApplicationInRegisterContext() throws MissingRequestParameterException {
        RegisterContext registerContext = new RegisterContext();
        List<ContextRegistration> contextRegistrationList = new ArrayList<>();
        ContextRegistration contextRegistration = new ContextRegistration();
        contextRegistrationList.add(contextRegistration);
        registerContext.setContextRegistrationList(contextRegistrationList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("providingApplication");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void emptyProvidingApplicationInRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        List<ContextRegistration> contextRegistrationList = new ArrayList<>();
        ContextRegistration contextRegistration = new ContextRegistration(new URI(""));
        contextRegistrationList.add(contextRegistration);
        registerContext.setContextRegistrationList(contextRegistrationList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("providingApplication");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void nullIdOfEntityIdInRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        List<ContextRegistration> contextRegistrationList = new ArrayList<>();
        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:8585/acumulate"));
        EntityId entityId = new EntityId();
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        contextRegistrationList.add(contextRegistration);
        registerContext.setContextRegistrationList(contextRegistrationList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void emptyIdOfEntityIdInRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        List<ContextRegistration> contextRegistrationList = new ArrayList<>();
        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:8585/acumulate"));
        EntityId entityId = new EntityId("","Room",false);
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        contextRegistrationList.add(contextRegistration);
        registerContext.setContextRegistrationList(contextRegistrationList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void nullTypeOfEntityIdInRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        List<ContextRegistration> contextRegistrationList = new ArrayList<>();
        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:8585/acumulate"));
        EntityId entityId = new EntityId();
        entityId.setId("Room1");
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        contextRegistrationList.add(contextRegistration);
        registerContext.setContextRegistrationList(contextRegistrationList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void emptyTypeOfEntityIdInRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        List<ContextRegistration> contextRegistrationList = new ArrayList<>();
        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:8585/acumulate"));
        EntityId entityId = new EntityId("Room1","",false);
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        contextRegistrationList.add(contextRegistration);
        registerContext.setContextRegistrationList(contextRegistrationList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void nullNameOfAttributeInRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:8585/acumulate"));
        EntityId entityId = new EntityId("Room1","Room",false);
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        ContextRegistrationAttribute attribute = new ContextRegistrationAttribute();
        contextRegistration.setContextRegistrationAttributeList(Collections.singletonList(attribute));
        registerContext.setContextRegistrationList(Collections.singletonList(contextRegistration));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("name");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void emptyNameOfAttributenRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:8585/acumulate"));
        EntityId entityId = new EntityId("Room1","Room",false);
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        ContextRegistrationAttribute attribute = new ContextRegistrationAttribute("",false);
        contextRegistration.setContextRegistrationAttributeList(Collections.singletonList(attribute));
        registerContext.setContextRegistrationList(Collections.singletonList(contextRegistration));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("name");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    @Test
    public void nullIsDomainOfAttributenRegisterContext() throws MissingRequestParameterException, URISyntaxException {
        RegisterContext registerContext = new RegisterContext();
        ContextRegistration contextRegistration = new ContextRegistration(new URI("http://localhost:8585/acumulate"));
        EntityId entityId = new EntityId("Room1","Room",false);
        contextRegistration.setEntityIdList(Collections.singletonList(entityId));
        ContextRegistrationAttribute attribute = new ContextRegistrationAttribute();
        attribute.setName("attributeName");
        contextRegistration.setContextRegistrationAttributeList(Collections.singletonList(attribute));
        registerContext.setContextRegistrationList(Collections.singletonList(contextRegistration));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("isDomain");
        ngsiValidation.checkRegisterContext(registerContext);
    }

    // Tests for validation of subscribeContext
    @Test
    public void nullEntityIdListInSubscribeContext() throws MissingRequestParameterException {
        SubscribeContext subscribeContext = new SubscribeContext();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("entities");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void emptyEntityIdListInSubscribeContext() throws MissingRequestParameterException {
        SubscribeContext subscribeContext = new SubscribeContext();
        List<EntityId> entityIdList = new ArrayList<>();
        subscribeContext.setEntityIdList(entityIdList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("entities");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void nullIdOfEntityIdInSubscribeContext() throws MissingRequestParameterException, URISyntaxException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId();
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void emptyIdOfEntityIdInSubscribeContext() throws MissingRequestParameterException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId("","Room",false);
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void nullTypeOfEntityIdInSubscribeContext() throws MissingRequestParameterException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        entityId.setType(null);
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void emptyTypeOfEntityIdInSubscribeContext() throws MissingRequestParameterException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId("Room1","",false);
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void nullReferenceInSubscribeContext() throws MissingRequestParameterException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("reference");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void emptyReferenceInSubscribeContext() throws MissingRequestParameterException, URISyntaxException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        subscribeContext.setReference(new URI(""));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("reference");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void nullAttributeExpressionOfRestrictionInSubscribeContext() throws MissingRequestParameterException, URISyntaxException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        subscribeContext.setReference(new URI("http://localhost:8085/accumulate"));
        subscribeContext.setRestriction(new Restriction());
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("attributeExpression");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    @Test
    public void emptyAttributeExpressionOfRestrictionInSubscribeContext() throws MissingRequestParameterException, URISyntaxException {
        SubscribeContext subscribeContext = new SubscribeContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        subscribeContext.setEntityIdList(Collections.singletonList(entityId));
        subscribeContext.setReference(new URI("http://localhost:8085/accumulate"));
        Restriction restriction = new Restriction();
        restriction.setAttributeExpression("");
        subscribeContext.setRestriction(restriction);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("attributeExpression");
        ngsiValidation.checkSubscribeContext(subscribeContext);
    }

    // Tests for validation of updateContextSubscription
    @Test
    public void nullSubscriptionIdInUpdateContextSubscription() throws MissingRequestParameterException {
        UpdateContextSubscription updateContextSubscription = new UpdateContextSubscription();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("subscriptionId");
        ngsiValidation.checkUpdateContextSubscription(updateContextSubscription);
    }

    @Test
    public void emptySubscriptionIdInUpdateContextSubscription() throws MissingRequestParameterException {
        UpdateContextSubscription updateContextSubscription = new UpdateContextSubscription();
        updateContextSubscription.setSubscriptionId("");
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("subscriptionId");
        ngsiValidation.checkUpdateContextSubscription(updateContextSubscription);
    }

    @Test
    public void nullAttributeExpressionOfRestrictionInUpdateContextSubscription() throws MissingRequestParameterException, URISyntaxException {
        UpdateContextSubscription updateContextSubscription = new UpdateContextSubscription();
        EntityId entityId = new EntityId("Room1","Room",false);
        updateContextSubscription.setSubscriptionId("12345");
        updateContextSubscription.setRestriction(new Restriction());
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("attributeExpression");
        ngsiValidation.checkUpdateContextSubscription(updateContextSubscription);
    }

    @Test
    public void emptyAttributeExpressionOfRestrictionInUpdateContextSubscription() throws MissingRequestParameterException, URISyntaxException {
        UpdateContextSubscription updateContextSubscription = new UpdateContextSubscription();
        EntityId entityId = new EntityId("Room1","Room",false);
        updateContextSubscription.setSubscriptionId("12345");
        Restriction restriction = new Restriction();
        restriction.setAttributeExpression("");
        updateContextSubscription.setRestriction(restriction);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("attributeExpression");
        ngsiValidation.checkUpdateContextSubscription(updateContextSubscription);
    }

    // Tests for validation of unsubscribeContext
    @Test
    public void nullSubscriptionIdInUnSubscribeContext() throws MissingRequestParameterException {
        UnsubscribeContext unsubscribeContext = new UnsubscribeContext();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("subscriptionId");
        ngsiValidation.checkUnsubscribeContext(unsubscribeContext);
    }

    @Test
    public void emptySubscriptionIdInUnSubscribeContext() throws MissingRequestParameterException {
        UnsubscribeContext unsubscribeContext = new UnsubscribeContext();
        unsubscribeContext.setSubscriptionId("");
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("subscriptionId");
        ngsiValidation.checkUnsubscribeContext(unsubscribeContext);
    }

    // Tests for validation of queryContext
    @Test
    public void nullEntityIdListInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("entities");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void emptyEntityIdListInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        List<EntityId> entityIdList = new ArrayList<>();
        queryContext.setEntityIdList(entityIdList);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("entities");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void nullIdOfEntityIdInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        EntityId entityId = new EntityId();
        queryContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void emptyIdOfEntityIdInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        EntityId entityId = new EntityId("","Room",false);
        queryContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("id");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void nullTypeOfEntityIdInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        entityId.setType(null);
        queryContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void emptyTypeOfEntityIdInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        EntityId entityId = new EntityId("Room1","",false);
        queryContext.setEntityIdList(Collections.singletonList(entityId));
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("type");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void nullAttributeExpressionOfRestrictionInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        queryContext.setEntityIdList(Collections.singletonList(entityId));
        queryContext.setRestriction(new Restriction());
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("attributeExpression");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void emptyAttributeExpressionOfRestrictionInQueryContext() throws MissingRequestParameterException {
        QueryContext queryContext = new QueryContext();
        EntityId entityId = new EntityId("Room1","Room",false);
        queryContext.setEntityIdList(Collections.singletonList(entityId));
        Restriction restriction = new Restriction();
        restriction.setAttributeExpression("");
        queryContext.setRestriction(restriction);
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("attributeExpression");
        ngsiValidation.checkQueryContext(queryContext);
    }

    @Test
    public void nullAttributesInAppendContextElement() throws MissingRequestParameterException {
        AppendContextElement appendContextElement = new AppendContextElement();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextAttributes");
        ngsiValidation.checkAppendContextElement(appendContextElement);
    }

    @Test
    public void emptyAttributesInAppendContextElement() throws MissingRequestParameterException {
        AppendContextElement appendContextElement = new AppendContextElement();
        appendContextElement.setAttributeList(Collections.emptyList());
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextAttributes");
        ngsiValidation.checkAppendContextElement(appendContextElement);
    }

    @Test
    public void nullAttributesInUpdateContextElement() throws MissingRequestParameterException {
        UpdateContextElement updateContextElement = new UpdateContextElement();
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextAttributes");
        ngsiValidation.checkUpdateContextElement(updateContextElement);
    }

    @Test
    public void emptyAttributesInUpdateContextElement() throws MissingRequestParameterException {
        UpdateContextElement updateContextElement = new UpdateContextElement();
        updateContextElement.setContextAttributes(Collections.emptyList());
        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextAttributes");
        ngsiValidation.checkUpdateContextElement(updateContextElement);
    }

    @Test
    public void differentSubscriptionIDInUpdateSubscription() throws Exception {
        UpdateContextSubscription updateContextSubscription = new UpdateContextSubscription();
        EntityId entityId = new EntityId("Room1","Room",false);
        updateContextSubscription.setSubscriptionId("12345");
        thrown.expect(MismatchIdException.class);
        thrown.expectMessage("mismatch id between parameter 54321 and body 12345");
        ngsiValidation.checkUpdateSubscription("54321", updateContextSubscription);
    }
}

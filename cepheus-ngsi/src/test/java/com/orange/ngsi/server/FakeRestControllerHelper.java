/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.orange.ngsi.Util;
import com.orange.ngsi.model.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/rest/i")
public class FakeRestControllerHelper extends NgsiRestBaseController {

    @Override
    protected AppendContextElementResponse appendContextElement(String entityID, AppendContextElement appendContextElement) throws Exception {
        return Util.createAppendContextElementResponseTemperature();
    }

    @Override
    protected UpdateContextElementResponse updateContextElement(String entityID, UpdateContextElement updateContextElement) throws Exception {
        return Util.createUpdateContextElementResponseTemperature();
    }

    @Override
    protected ContextElementResponse getContextElement(String entityID) throws Exception {
        return Util.createContextElementResponseTemperature();
    }

    @Override
    protected StatusCode deleteContextElement(String entityID) throws Exception {
        return new StatusCode(CodeEnum.CODE_200);
    }

    @Override
    protected StatusCode appendContextAttribute(String entityID, String attributeName, UpdateContextAttribute updateContextAttribute) throws Exception {
        return new StatusCode(CodeEnum.CODE_200);
    }

    @Override
    protected StatusCode updateContextAttribute(String entityID, String attributeName, UpdateContextAttribute updateContextElementRequest) throws Exception {
        return new StatusCode(CodeEnum.CODE_200);
    }

    @Override
    protected StatusCode updateContextAttribute(String entityID, String attributeName, String valueID, UpdateContextAttribute updateContextElementRequest) throws Exception {
        return new StatusCode(CodeEnum.CODE_200);
    }

    @Override
    protected ContextAttributeResponse getContextAttribute(String entityID, String attributeName) throws Exception {
        return Util.createContextAttributeResponseTemperature();
    }

    @Override
    protected ContextAttributeResponse getContextAttribute(String entityID, String attributeName, String valueID) throws Exception {
        return Util.createContextAttributeResponseTemperature();
    }

    @Override
    protected StatusCode deleteContextAttribute(String entityID, String attributeName) throws Exception {
        return new StatusCode(CodeEnum.CODE_200);
    }

    @Override
    protected StatusCode deleteContextAttribute(String entityID, String attributeName, String valueID) throws Exception {
        return new StatusCode(CodeEnum.CODE_200);
    }

    @Override
    protected QueryContextResponse getContextEntitiesType(String typeName) throws Exception {
        return Util.createQueryContextResponseTemperature();
    }

    @Override
    protected QueryContextResponse getContextEntitiesType(String typeName, String attributeName) throws Exception {
        return Util.createQueryContextResponseTemperature();
    }

    @Override
    protected SubscribeContextResponse createSubscription(SubscribeContext subscribeContext) throws Exception {
        return Util.createSubscribeContextResponseTemperature();
    }

    @Override
    protected UpdateContextSubscriptionResponse updateSubscription(UpdateContextSubscription updateContextSubscription) throws Exception {
        return Util.createUpdateContextSubscriptionResponseTemperature();
    }

    @Override
    protected UnsubscribeContextResponse deleteSubscription(String subscriptionID) throws Exception {
        return Util.createUnsubscribeContextSubscriptionResponseTemperature();
    }
}

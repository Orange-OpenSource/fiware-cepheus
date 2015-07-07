/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.model.ngsi.NotifyContext;
import com.orange.espr4fastdata.model.ngsi.UpdateContext;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by pborscia on 06/07/2015.
 */
public class NgsiValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return (NotifyContext.class.equals(aClass) || UpdateContext.class.equals(aClass));
    }

    @Override
    public void validate(Object target, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "subscriptionId", "subscriptionId.empty");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "originator", "originator.empty");

        NotifyContext notifyContext = (NotifyContext) target;

    }
}

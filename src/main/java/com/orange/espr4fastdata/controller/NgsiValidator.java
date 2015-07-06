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

package com.orange.ngsi.server;

import com.orange.ngsi.exception.MissingRequestParameterException;
import com.orange.ngsi.model.ContextElement;
import com.orange.ngsi.model.UpdateAction;
import com.orange.ngsi.model.UpdateContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pborscia on 07/08/2015.
 */
public class NgsiValidationTest {

    static NgsiValidation ngsiValidation = new NgsiValidation();

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Test
    public void missingUpdateActionInUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();

        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("updateAction");
        ngsiValidation.checkUpdateContext(updateContext);

    }

    @Test
    public void missingContextElementWithDeleteInUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.DELETE);
        List<ContextElement> contextElements = new ArrayList<>();
        updateContext.setContextElements(contextElements);

        ngsiValidation.checkUpdateContext(updateContext);

    }

    @Test
    public void missingContextElementWithUpdateInUpdateContext() throws MissingRequestParameterException {
        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);

        thrown.expect(MissingRequestParameterException.class);
        thrown.expectMessage("contextElements");
        ngsiValidation.checkUpdateContext(updateContext);

    }


}

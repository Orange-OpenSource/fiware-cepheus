package com.orange.espr4fastdata.model.ngsi;

import java.util.List;

/**
 * Created by pborscia on 05/06/2015.
 */
public class UpdateContext {

    List<ContextElement> contextElements;

    UpdateAction updateAction;

    public UpdateContext() {
    }

    public List<ContextElement> getContextElements() {
        return contextElements;
    }

    public void setContextElements(List<ContextElement> contextElements) {
        this.contextElements = contextElements;
    }

    public UpdateAction getUpdateAction() {
        return updateAction;
    }

    public void setUpdateAction(UpdateAction updateAction) {
        this.updateAction = updateAction;
    }
}

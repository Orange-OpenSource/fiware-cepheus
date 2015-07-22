/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.model;

import java.util.List;

/**
 * Created by pborscia on 05/06/2015.
 */
public class UpdateContext {

    List<ContextElement> contextElements;

    UpdateAction updateAction;

    public UpdateContext() {
    }

    public UpdateContext(UpdateAction updateAction) {
        this.updateAction = updateAction;
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

/*
 * Copyright (C) 2015 Orange
 *
 * This software is distributed under the terms and conditions of the 'GNU GENERAL PUBLIC LICENSE
 * Version 2' license which can be found in the file 'LICENSE.txt' in this package distribution or
 * at 'http://www.gnu.org/licenses/gpl-2.0-standalone.html'.
 */

package com.orange.ngsi.server;

import com.orange.ngsi.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for the NGSI 9/10 requests
 */

public class NgsiBaseController {

    @Autowired
    private NgsiValidation ngsiValidation;

    @RequestMapping(value = "/notifyContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    final public ResponseEntity<NotifyContextResponse> notifyContextRequest(@RequestBody final NotifyContext notify) throws Exception {
        ngsiValidation.checkNotifyContext(notify);
        return notifyContext(notify);
    }

    //Base Operation notifyContext
    protected ResponseEntity<NotifyContextResponse> notifyContext(@RequestBody final NotifyContext notify) throws Exception {
        throw new UnsupportedOperationException("notifyContext");
    }

    @RequestMapping(value = "/updateContext", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    final public UpdateContextResponse updateContextRequest(@RequestBody final UpdateContext update) throws Exception {
        ngsiValidation.checkUpdateContext(update);
        return updateContext(update);
    }

    //Base Operation updateContext
    protected UpdateContextResponse updateContext(@RequestBody final UpdateContext update) throws Exception {
        throw new UnsupportedOperationException("updateContext");
    }
}

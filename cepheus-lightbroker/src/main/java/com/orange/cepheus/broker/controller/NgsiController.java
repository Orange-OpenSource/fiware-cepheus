package com.orange.cepheus.broker.controller;

import com.orange.ngsi.model.*;
import com.orange.ngsi.server.NgsiBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pborscia on 06/08/2015.
 */
@RestController
@RequestMapping("/v1")
public class NgsiController extends NgsiBaseController {

    private static Logger logger = LoggerFactory.getLogger(NgsiController.class);
    @Override
    public UpdateContextResponse updateContext(final UpdateContext update) throws Exception {

        UpdateContextResponse response = new UpdateContextResponse();

        List<ContextElementResponse> responses = new ArrayList<>();
        ContextElementResponse contextElementResponse = new ContextElementResponse();
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_200);
        contextElementResponse.setStatusCode(statusCode);
        responses.add(contextElementResponse);
        response.setContextElementResponses(responses);
        return response;
    }

}

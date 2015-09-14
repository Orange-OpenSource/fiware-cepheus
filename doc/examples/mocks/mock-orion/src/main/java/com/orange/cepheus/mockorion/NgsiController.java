package com.orange.cepheus.mockorion;

import com.orange.ngsi.model.*;
import com.orange.ngsi.server.NgsiBaseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * NGSI Controller : NGSI operation implemented by mock-orion
 */
@RestController
@RequestMapping(value = {"/v1","/ngsi10","/ngsi9"})
public class NgsiController extends NgsiBaseController {
    private static Logger logger = LoggerFactory.getLogger(NgsiController.class);

    @Value("${registrationId:999999}")
    String registrationId;

    @Override
    public UpdateContextResponse updateContext(final UpdateContext update) {

        logger.info("=> updateContext : {}", update.toString());

        //send response with status 200 = OK
        UpdateContextResponse updateContextResponse = new UpdateContextResponse();
        List<ContextElementResponse> contextElementResponseList = new ArrayList<>();
        StatusCode statusCode = new StatusCode(CodeEnum.CODE_200);
        for (ContextElement c : update.getContextElements()) {
            contextElementResponseList.add(new ContextElementResponse(c, statusCode));
        }
        updateContextResponse.setContextElementResponses(contextElementResponseList);
        return updateContextResponse;
    }

    @Override
    public RegisterContextResponse registerContext(final RegisterContext register) {
        logger.info("=> registerContext : {}", register.toString());

        RegisterContextResponse registerContextResponse = new RegisterContextResponse(registrationId);
        registerContextResponse.setDuration(register.getDuration());
        return registerContextResponse;
    }

}

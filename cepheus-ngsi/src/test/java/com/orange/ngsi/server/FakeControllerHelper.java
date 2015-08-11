package com.orange.ngsi.server;

import com.orange.ngsi.model.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/i")
public class FakeControllerHelper extends NgsiBaseController {

    @Override
    protected NotifyContextResponse notifyContext(NotifyContext notify) throws Exception {
        return new NotifyContextResponse();
    }

    @Override
    protected UpdateContextResponse updateContext(UpdateContext update) throws Exception {
        return new UpdateContextResponse();
    }

    @Override
    protected RegisterContextResponse registerContext(RegisterContext register) throws Exception {
        return new RegisterContextResponse();
    }

    @Override
    protected SubscribeContextResponse subscribeContext(SubscribeContext subscribe) throws Exception {
        return new SubscribeContextResponse();
    }
}

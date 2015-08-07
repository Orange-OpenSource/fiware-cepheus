package com.orange.ngsi.server;

import com.orange.ngsi.model.NotifyContext;
import com.orange.ngsi.model.NotifyContextResponse;
import com.orange.ngsi.model.UpdateContext;
import com.orange.ngsi.model.UpdateContextResponse;
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
}

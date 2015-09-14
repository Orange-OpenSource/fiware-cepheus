package com.orange.cepheus.mockorion;

import com.orange.cepheus.mockorion.model.Query;
import com.orange.cepheus.mockorion.model.Update;
import com.orange.ngsi.client.NgsiClient;
import com.orange.ngsi.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Created by pborscia on 20/08/2015.
 */
@RestController
@RequestMapping("/v1/admin")
public class AdminController {

    private static Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    NgsiClient ngsiClient;

    @Value("${cepheus.broker:http://localhost:8081}")
    String cepheusBroker;

    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity query(@Valid @RequestBody final Query query) throws ExecutionException, InterruptedException {

        QueryContext queryContext = new QueryContext(Collections.singletonList(new EntityId(query.getName(), query.getType(), query.getIsPattern())));
        QueryContextResponse queryContextResponse = ngsiClient.queryContext(cepheusBroker, null, queryContext).get();
        logger.info("=> QueryContextResponse received from {}: {}", cepheusBroker, queryContextResponse.toString());
        if (queryContextResponse.getErrorCode() == null) {
            queryContextResponse.getContextElementResponses().forEach(contextElementResponse -> {
                logger.info("EntityId : {}", contextElementResponse.getContextElement().getEntityId().toString());
                contextElementResponse.getContextElement().getContextAttributeList().forEach(contextAttribute -> {
                    logger.info("Attribute : {}", contextAttribute.toString());
                });
            });
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@Valid @RequestBody final Update update) throws ExecutionException, InterruptedException {

        UpdateContext updateContext = new UpdateContext();
        updateContext.setUpdateAction(UpdateAction.UPDATE);
        ContextElement contextElement = new ContextElement();
        contextElement.setEntityId(new EntityId(update.getName(), update.getType(), update.getIsPattern()));
        ContextAttribute contextAttribute = new ContextAttribute();
        contextAttribute.setName(update.getAttributName());
        contextAttribute.setType(update.getAttributType());
        contextAttribute.setValue(update.getAttributValue());
        contextElement.setContextAttributeList(Collections.singletonList(contextAttribute));
        updateContext.setContextElements(Collections.singletonList(contextElement));

        UpdateContextResponse updateContextResponse = ngsiClient.updateContext(cepheusBroker, null, updateContext).get();
        logger.info("=> UpdateContextResponse received from {}: {}", cepheusBroker, updateContextResponse.toString());
        if (updateContextResponse.getErrorCode() == null) {
            updateContextResponse.getContextElementResponses().forEach(contextElementResponse -> {
                logger.info("EntityId : {}", contextElementResponse.getContextElement().getEntityId().toString());
                logger.info("StatusCode : {}", contextElementResponse.getStatusCode().toString());
            });
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

package com.orange.espr4fastdata.controller;

import com.orange.espr4fastdata.exception.EventProcessingException;
import com.orange.espr4fastdata.model.ngsi.NotifyContext;
import com.orange.espr4fastdata.model.ngsi.NotifyContextResponse;
import com.orange.espr4fastdata.model.ngsi.StatusCode;
import com.orange.espr4fastdata.model.ngsi.UpdateContextResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by pborscia on 06/07/2015.
 */
@ControllerAdvice("com.orange.espr4fastdata.controller.NgsiController")
public class NgsiHandlerException extends ResponseEntityExceptionHandler {


    @ExceptionHandler({EventProcessingException.class})
    public ResponseEntity<Object> badRequest(HttpServletRequest req, Exception exception) {

        Object entity = entityForPath(req.getRequestURI(), StatusCode.CODE_400);
        return new ResponseEntity<Object>(entity, HttpStatus.OK);

    }


    /**
     * Response for request error. NGSI requests require custom responses.
     */
    private Object entityForPath(String path, StatusCode statusCode) {
        /*if (path.contains("/registerContext")) {
            RegisterContextResponse r = new RegisterContextResponse();
            r.setErrorCode(statusCode);
            return r;
        } else if (path.contains("/subscribeContext")) {
            SubscribeContextResponse r = new SubscribeContextResponse();
            SubscribeError e = new SubscribeError();
            e.setErrorCode(statusCode);
            r.setSubscribeError(e);
            return r;
        } else if (path.contains("/unsubscribeContext")) {
            UnsubscribeContextResponse r = new UnsubscribeContextResponse();
            r.setStatusCode(statusCode); // WTF?
            return r;
        } else if (path.contains("/updateContext")) {
            UpdateContextResponse r = new UpdateContextResponse();
            r.setErrorCode(statusCode);
            return r;
        } else if (path.contains("/queryContext")) {
            QueryContextResponse r = new QueryContextResponse();
            r.setErrorCode(statusCode);
            return r;
        }*/

        if (path.contains("/notifyContext")) {
            NotifyContextResponse r = new NotifyContextResponse();
            r.setResponseCode(statusCode);
            return r;
        } else if (path.contains("/updateContext")) {
            UpdateContextResponse r = new UpdateContextResponse();
            r.setErrorCode(statusCode);
            return r;
        }

        // All other non NGSI requests send back NotifyContextResponse
        NotifyContextResponse r = new NotifyContextResponse();
        r.setResponseCode(statusCode);
        return r;
    }
}

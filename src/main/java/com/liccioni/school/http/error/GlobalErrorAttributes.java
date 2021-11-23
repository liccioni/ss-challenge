package com.liccioni.school.http.error;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request,
                                                  ErrorAttributeOptions options) {

        Map<String, Object> map = super.getErrorAttributes(request, options);
        Throwable error = getError(request);
        if (error instanceof DataIntegrityViolationException dk) {
            return handleDuplicateKey(map, dk);
        }
        map.put("exception", "SystemException");
        map.put("message", "System Error , Check logs!");
        map.put("error", " System Error ");
        return map;
    }

    private Map<String, Object> handleDuplicateKey(Map<String, Object> map, DataIntegrityViolationException dk) {
        map.put("exception", dk.getClass().getSimpleName());
        map.put("message", dk.getMessage());
        map.put("status", HttpStatus.CONFLICT.value());
        map.put("error", HttpStatus.CONFLICT.getReasonPhrase());
        return map;
    }
}

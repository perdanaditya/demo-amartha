package com.amartha.billing.inbound.util;

import com.amartha.billing.exception.AppException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AppException.class)
    protected ResponseEntity<ApiError> handleCommonServiceException(AppException ex) {
        return buildResponseEntity(ApiError.builder().code(ex.getError().getCode()).status(ex.getError().getHttpStatus()).message(ex.getMessage()).build());
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}

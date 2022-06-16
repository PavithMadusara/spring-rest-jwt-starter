package com.aupma.spring.starter.security.config;

import com.aupma.spring.starter.model.ErrorResponse;
import com.aupma.spring.starter.security.exception.ApplicationSecurityException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;


public class SecurityExceptionHandler {

    @ExceptionHandler(ApplicationSecurityException.class)
    @ApiResponse(responseCode = "401/403", description = "Security Error")
    public ResponseEntity<ErrorResponse> handleNotFound(final ApplicationSecurityException exception) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(exception.getStatus().value());
        errorResponse.setException(exception.getClass().getSimpleName());
        errorResponse.setCode(exception.getCode());
        errorResponse.setMessage(exception.getMessage());
        return new ResponseEntity<>(errorResponse, exception.getStatus());
    }
}

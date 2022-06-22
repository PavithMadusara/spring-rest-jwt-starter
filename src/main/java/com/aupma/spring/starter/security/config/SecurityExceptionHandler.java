package com.aupma.spring.starter.security.config;

import com.aupma.spring.starter.model.ErrorResponse;
import com.aupma.spring.starter.security.exception.ApplicationSecurityException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;


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

    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponse(responseCode = "403", description = "Access Denied")
    public ResponseEntity<ErrorResponse> handleNotFound(final AccessDeniedException exception) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setHttpStatus(HttpStatus.FORBIDDEN.value());
        errorResponse.setException(exception.getClass().getSimpleName());
        errorResponse.setCode("ACCESS_DENIED");
        errorResponse.setMessage(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}

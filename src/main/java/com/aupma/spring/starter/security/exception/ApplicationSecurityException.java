package com.aupma.spring.starter.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationSecurityException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String message;
}

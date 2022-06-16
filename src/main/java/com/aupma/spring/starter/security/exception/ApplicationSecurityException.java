package com.aupma.spring.starter.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
public class ApplicationSecurityException extends RuntimeException {
    private HttpStatus status;
    private String code;
    private String message;
}

package com.aupma.spring.starter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class StoredFileNotFound extends RuntimeException {
    public StoredFileNotFound(String message) {
        super(message);
    }

    public StoredFileNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}

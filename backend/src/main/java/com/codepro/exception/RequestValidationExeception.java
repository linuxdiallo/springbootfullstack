package com.codepro.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RequestValidationExeception extends RuntimeException {
    public RequestValidationExeception(String message) {
        super(message);
    }
}

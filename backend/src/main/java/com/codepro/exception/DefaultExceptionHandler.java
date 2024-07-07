package com.codepro.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author aboubacar.diallo
 * @created 05/09/2023 - 14:54
 * @project springbootfullstack
 * @package com.codepro.exception
 */

@ControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    ResponseEntity<ApiError> handleException(ResourceNotFoundException e,
                                             HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                DateTimeFormatter
                        .ofPattern("dd/MM/yyyy à HH:mm:ss")
                        .format(LocalDateTime.now())
        );

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(InsufficientAuthenticationException.class)
    ResponseEntity<ApiError> handleException(InsufficientAuthenticationException e,
                                                             HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.FORBIDDEN.value(),
                DateTimeFormatter
                        .ofPattern("dd/MM/yyyy à HH:mm:ss")
                        .format(LocalDateTime.now())
        );
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiError> handleException(BadCredentialsException e,
                                             HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.UNAUTHORIZED.value(),
                DateTimeFormatter
                        .ofPattern("dd/MM/yyyy à HH:mm:ss")
                        .format(LocalDateTime.now())
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> handleException(Exception e,
                                             HttpServletRequest request) {
        ApiError apiError = new ApiError(
                request.getRequestURI(),
                e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                DateTimeFormatter
                        .ofPattern("dd/MM/yyyy à HH:mm:ss")
                        .format(LocalDateTime.now())
        );

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}

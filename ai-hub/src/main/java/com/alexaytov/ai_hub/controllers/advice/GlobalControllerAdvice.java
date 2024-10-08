package com.alexaytov.ai_hub.controllers.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDto> handleException(HttpClientErrorException e) {
        return ResponseEntity.status(e.getStatusCode()).body(new ErrorDto(e.getMessage(), System.currentTimeMillis()));
    }
}

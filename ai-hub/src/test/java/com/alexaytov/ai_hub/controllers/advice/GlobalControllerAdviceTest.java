package com.alexaytov.ai_hub.controllers.advice;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class GlobalControllerAdviceTest {

    @Test
    void whenHandlingHttpClientErrorException_thenErrorDtoIsReturned() {
        GlobalControllerAdvice classUnderTest = new GlobalControllerAdvice();
        HttpClientErrorException exception = new HttpClientErrorException(BAD_REQUEST, "error");

        var result = classUnderTest.handleException(exception);

        assertNotNull(result);
        assertEquals(exception.getMessage(), result.getBody().message());
        assertNotNull(result.getBody().timestamp());
    }

}
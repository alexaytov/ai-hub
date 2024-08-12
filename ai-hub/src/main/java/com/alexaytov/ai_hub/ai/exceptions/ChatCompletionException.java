package com.alexaytov.ai_hub.ai.exceptions;

public class ChatCompletionException extends RuntimeException {

    public ChatCompletionException(String message, Throwable cause) {
        super(message, cause);
    }
}

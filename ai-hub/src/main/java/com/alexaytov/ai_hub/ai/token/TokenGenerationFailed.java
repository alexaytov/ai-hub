package com.alexaytov.ai_hub.ai.token;

public class TokenGenerationFailed extends RuntimeException {

    public TokenGenerationFailed(String message, Throwable cause) {
        super(message, cause);
    }
}

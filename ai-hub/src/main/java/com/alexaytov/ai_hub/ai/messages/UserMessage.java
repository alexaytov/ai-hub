package com.alexaytov.ai_hub.ai.messages;


import com.alexaytov.ai_hub.ai.data.CompletionRequestMessage;

public record UserMessage(String content, String role, String name) implements CompletionRequestMessage {
    public UserMessage(String content) {
        this(content, "user", null);
    }

    public UserMessage(String content, String name) {
        this(content, "user", name);
    }
}

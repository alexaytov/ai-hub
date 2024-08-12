package com.alexaytov.ai_hub.ai.messages;

import com.alexaytov.ai_hub.ai.data.CompletionRequestMessage;

public record SystemMessage(String content, String role, String name) implements CompletionRequestMessage {
    public SystemMessage(String content) {
        this(content, "system", null);
    }

    public SystemMessage(String content, String name) {
        this(content, "system", name);
    }
}

package com.alexaytov.ai_hub.ai.messages;

import com.alexaytov.ai_hub.ai.data.CompletionRequestMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ToolMessage(String role,
                          String content,
                          @JsonProperty("tool_call_id") String toolCallId) implements CompletionRequestMessage {

    public ToolMessage(String content) {
        this("tool", content, null);
    }

    public ToolMessage(String content, String toolCallId) {
        this("tool", content, toolCallId);
    }
}

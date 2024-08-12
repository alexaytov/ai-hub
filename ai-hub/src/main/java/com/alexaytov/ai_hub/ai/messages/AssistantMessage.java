package com.alexaytov.ai_hub.ai.messages;

import java.util.List;

import com.alexaytov.ai_hub.ai.data.CompletionRequestMessage;
import com.alexaytov.ai_hub.ai.data.ToolCall;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AssistantMessage(String content,
                               String role,
                               String name,
                               @JsonProperty("tool_calls") List<ToolCall> toolCalls) implements CompletionRequestMessage {

    public AssistantMessage() {
        this(null, "assistant", null, List.of());
    }

    public AssistantMessage(String content) {
        this(content, "assistant", null, List.of());
    }

    public AssistantMessage(String content, List<ToolCall> toolCalls) {
        this(content, "assistant", null, toolCalls);
    }

    public AssistantMessage(List<ToolCall> toolCalls) {
        this(null, "assistant", null, toolCalls);
    }
}

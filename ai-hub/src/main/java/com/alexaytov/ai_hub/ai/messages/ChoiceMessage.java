package com.alexaytov.ai_hub.ai.messages;

import java.util.List;

import com.alexaytov.ai_hub.ai.data.ToolCall;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ChoiceMessage(String content, String role, @JsonProperty("tool_calls") List<ToolCall> toolCalls) {
}

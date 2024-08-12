package com.alexaytov.ai_hub.ai.data;

public record ToolCall(String id, String type, ToolCallFunction function) {
}

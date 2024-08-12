package com.alexaytov.ai_hub.ai.data;

public record ToolFunction(
    String name,
    String description,
    FunctionParameters parameters
) {
}

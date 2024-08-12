package com.alexaytov.ai_hub.ai.data;

public record Tool(String type, ToolFunction function) {

    public Tool(ToolFunction function) {
        this("function", function);
    }
}

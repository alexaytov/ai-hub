package com.alexaytov.ai_hub.ai.data;

import java.util.List;

public record CompletionResponse(
    List<Choice> choices,
    long created,
    String id,
    String model,
    String object,
    Usage usage
) {
}


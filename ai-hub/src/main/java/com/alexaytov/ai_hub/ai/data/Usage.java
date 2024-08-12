package com.alexaytov.ai_hub.ai.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Usage(
    @JsonProperty("completion_tokens") int completionTokens,
    @JsonProperty("prompt_tokens") int promptTokens,
    @JsonProperty("total_tokens") int totalTokens
) {
}

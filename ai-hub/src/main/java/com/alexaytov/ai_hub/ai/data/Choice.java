package com.alexaytov.ai_hub.ai.data;

import com.alexaytov.ai_hub.ai.messages.ChoiceMessage;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Choice(
    @JsonProperty("finish_reason") String finishReason,
    int index,
    ChoiceMessage message
) {
}
package com.alexaytov.ai_hub.ai.data;

import java.util.List;
import java.util.Map;

public record FunctionParameters(
    String type,
    Map<String, FunctionProperty> properties,
    List<String> required
) {

    public FunctionParameters(Map<String, FunctionProperty> properties, List<String> required) {
        this("object", properties, required);
    }
}

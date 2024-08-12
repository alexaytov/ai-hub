package com.alexaytov.ai_hub.ai.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompletionRequest {

    private final List<CompletionRequestMessage> messages;
    private final List<Tool> tools;


    @JsonProperty("max_tokens")
    private final Integer maxTokens;
    private final double temperature;
    @JsonProperty("frequency_penalty")
    private final double freuqncyPenalty;
    @JsonProperty("presence_penalty")
    private final double presencePenalty;
    @JsonProperty("top_p")
    private final double topP;
    private final String stop;

    public CompletionRequest(List<CompletionRequestMessage> messages,
                             Integer maxTokens,
                             double temperature,
                             double frequencyPenalty,
                             double presencePenalty,
                             double topP,
                             String stop,
                             List<Tool> tools) {
        this.messages = messages;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.freuqncyPenalty = frequencyPenalty;
        this.presencePenalty = presencePenalty;
        this.topP = topP;
        this.stop = stop;
        this.tools = tools;
    }

    public List<CompletionRequestMessage> getMessages() {
        return messages;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getFreuqncyPenalty() {
        return freuqncyPenalty;
    }

    public double getPresencePenalty() {
        return presencePenalty;
    }

    public double getTopP() {
        return topP;
    }

    public String getStop() {
        return stop;
    }

    public List<Tool> getTools() {
        return tools;
    }
}

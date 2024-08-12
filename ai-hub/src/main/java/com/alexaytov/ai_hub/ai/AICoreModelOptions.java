package com.alexaytov.ai_hub.ai;

public class AICoreModelOptions {

    private String stop;
    private Integer maxTokens;
    private float topP;
    private float presencePenalty;
    private float frequencyPenalty;
    private float temperature;

    private AICoreModelOptions(Builder builder) {
        this.stop = builder.stop;
        this.topP = builder.topP;
        this.presencePenalty = builder.presencePenalty;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.temperature = builder.temperature;
        this.maxTokens = builder.maxTokens;
    }

    public String getStop() {
        return stop;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public Float getTemperature() {
        return temperature;
    }

    public Float getTopP() {
        return topP;
    }

    public Integer getTopK() {
        return 0;
    }

    public double getPresencePenalty() {
        return presencePenalty;
    }

    public double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public static class Builder {
        private String stop;
        private Integer maxTokens;

        private float topP;
        private float presencePenalty;
        private float frequencyPenalty;
        private float temperature;

        public Builder() {
            stop = null;
            maxTokens = null;

            topP = 1;
            presencePenalty = 0;
            frequencyPenalty = 0;
            temperature = 1;
        }

        public Builder stop(String stop) {
            this.stop = stop;
            return this;
        }

        public Builder topP(float topP) {
            this.topP = topP;
            return this;
        }

        public Builder presencePenalty(float presencePenalty) {
            this.presencePenalty = presencePenalty;
            return this;
        }

        public Builder frequencyPenalty(float frequencyPenalty) {
            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder temperature(float temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public AICoreModelOptions build() {
            return new AICoreModelOptions(this);
        }
    }
}
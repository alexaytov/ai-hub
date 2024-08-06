package com.alexaytov.ai_hub.model.dtos;

import jakarta.validation.constraints.NotNull;

public class AgentDto {

    @NotNull
    private Long id;
    @NotNull
    private Long modelId;
    @NotNull
    private Long systemMessageId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getSystemMessageId() {
        return systemMessageId;
    }

    public void setSystemMessageId(Long systemMessageId) {
        this.systemMessageId = systemMessageId;
    }
}

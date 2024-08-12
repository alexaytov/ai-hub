package com.alexaytov.ai_hub.model.dtos;

import java.util.ArrayList;
import java.util.List;

public class GetChatResponse {

    private List<ChatMessageDto> messages = new ArrayList<>();
    private Long modelId;
    private Long agentId;

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public List<ChatMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageDto> messages) {
        this.messages = messages;
    }
}

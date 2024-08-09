package com.alexaytov.ai_hub.model.dtos;

import com.alexaytov.ai_hub.model.entities.MessageType;

public class ChatMessageDto {

    private MessageType type;
    private String content;

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

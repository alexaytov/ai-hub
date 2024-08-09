package com.alexaytov.ai_hub.model.dtos;

import java.util.ArrayList;
import java.util.List;

public class GetChatResponse {

    private List<ChatMessageDto> messages = new ArrayList<>();

    public List<ChatMessageDto> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessageDto> messages) {
        this.messages = messages;
    }
}

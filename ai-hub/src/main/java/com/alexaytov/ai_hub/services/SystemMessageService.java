package com.alexaytov.ai_hub.services;

import java.util.List;

import com.alexaytov.ai_hub.model.dtos.SystemMessageDto;

public interface SystemMessageService {
    List<SystemMessageDto> getMessages();

    SystemMessageDto createMessage(SystemMessageDto message);

    SystemMessageDto getMessage(Long id);

    void deleteMessage(Long id);
}

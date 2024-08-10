package com.alexaytov.ai_hub.services;

import java.util.List;

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.ChatModelQueryDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
import com.alexaytov.ai_hub.model.dtos.GetChatResponse;
import com.alexaytov.ai_hub.model.dtos.QueryResponseDto;

public interface ChatService {
    QueryResponseDto query(Long chatId, ChatModelQueryDto query);

    ChatDto createChat(CreateChatDto dto);

    void deleteChat(Long id);

    GetChatResponse getChat(Long id);

    List<ChatDto> getChats();

    void deleteOlderThan(long milliseconds);
}

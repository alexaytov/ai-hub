package com.alexaytov.ai_hub.services;

import com.alexaytov.ai_hub.model.dtos.ChatMessageDto;
import com.alexaytov.ai_hub.model.dtos.QueryRequestDto;

public interface QueryService {

  ChatMessageDto query(QueryRequestDto request);

}

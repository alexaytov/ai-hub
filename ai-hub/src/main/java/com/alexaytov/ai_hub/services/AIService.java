package com.alexaytov.ai_hub.services;

import java.util.Map;

import com.alexaytov.ai_hub.model.entities.AIModelType;

import dev.langchain4j.model.chat.ChatLanguageModel;

public interface AIService {

    ChatLanguageModel getModel(AIModelType type, String apiKey, Map<String, String> parameters);
}

package com.alexaytov.ai_hub.services;

import java.util.List;

import com.alexaytov.ai_hub.model.dtos.AIModelDto;

public interface ModelService {
    AIModelDto createModel(AIModelDto dto);

    void deleteModel(Long id);

    List<AIModelDto> getModels();

    AIModelDto getModel(Long id);
}

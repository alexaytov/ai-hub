package com.alexaytov.ai_hub.services;

import java.util.List;

import com.alexaytov.ai_hub.model.dtos.AgentDto;

public interface AgentService {
    List<AgentDto> getAgents();

    AgentDto getAgent(Long id);

    void deleteAgent(Long id);

    AgentDto createAgent(AgentDto dto);
}

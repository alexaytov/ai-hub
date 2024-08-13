package com.alexaytov.ai_hub.services.impl;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.AgentDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.model.entities.Agent;
import com.alexaytov.ai_hub.model.entities.SystemMessage;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.AgentRepository;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.SystemMessageRepository;
import com.alexaytov.ai_hub.services.AgentService;
import com.alexaytov.ai_hub.services.UserService;

import jakarta.transaction.Transactional;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class AgentServiceImpl implements AgentService {

    private final UserService userService;
    private final AgentRepository repository;
    private final ModelMapper mapper;
    private final SystemMessageRepository messageRepository;
    private final ModelRepository modelRepository;

    public AgentServiceImpl(UserService userService, AgentRepository repository, ModelMapper mapper, SystemMessageRepository messageRepository, ModelRepository modelRepository) {
        this.userService = userService;
        this.repository = repository;
        this.mapper = mapper;
        this.messageRepository = messageRepository;
        this.modelRepository = modelRepository;
    }

    @Override
    public List<AgentDto> getAgents() {
        return userService.getUser().getAgents().stream()
            .map(agent -> {
                AgentDto dto = mapper.map(agent, AgentDto.class);

                dto.setId(agent.getId());
                dto.setModelId(agent.getModel().getId());
                dto.setSystemMessageId(agent.getSystemMessage().getId());

                return dto;
            }).toList();
    }

    @Override
    public AgentDto getAgent(Long id) {
        User user = userService.getUser();
        Agent agent = repository.findById(id)
            .filter(a -> a.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid agent id"));

        AgentDto dto = mapper.map(agent, AgentDto.class);
        dto.setModelId(agent.getModel().getId());
        dto.setSystemMessageId(agent.getSystemMessage().getId());
        return dto;
    }

    @Override
    public void deleteAgent(Long id) {
        User user = userService.getUser();
        Optional<Agent> agent = repository.findById(id)
            .filter(a -> a.getUser().getId().equals(user.getId()));

        if (agent.isEmpty()) {
            return;
        }

        repository.delete(agent.get());
    }

    @Override
    @Transactional
    public AgentDto createAgent(AgentDto dto) {
        User user = userService.getUser();
        SystemMessage message = messageRepository.findById(dto.getSystemMessageId())
            .filter(m -> m.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid message id"));

        AIModel model = modelRepository.findById(dto.getModelId())
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid model id"));

        Agent agent = mapper.map(dto, Agent.class);
        agent.setUser(user);
        agent.setModel(model);
        agent.setSystemMessage(message);

        agent = repository.save(agent);
        dto.setId(agent.getId());
        return dto;
    }
}

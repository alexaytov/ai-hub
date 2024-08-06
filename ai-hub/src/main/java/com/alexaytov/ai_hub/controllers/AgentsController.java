package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.exceptions.UserException;
import com.alexaytov.ai_hub.model.dtos.AgentDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.model.entities.Agent;
import com.alexaytov.ai_hub.model.entities.SystemMessage;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.AgentRepository;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.SystemMessageRepository;
import com.alexaytov.ai_hub.repositories.UserRepository;

import jakarta.validation.Valid;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class AgentsController {

    private final UserRepository userRepository;
    private final SystemMessageRepository messageRepository;
    private final ModelRepository modelRepository;
    private final AgentRepository repository;

    public AgentsController(UserRepository userRepository, SystemMessageRepository messageRepository, ModelRepository modelRepository, AgentRepository repository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.modelRepository = modelRepository;
        this.repository = repository;
    }

    @GetMapping("/agents")
    public ResponseEntity<List<AgentDto>> getAgents() {
        List<AgentDto> agentDtos = getUser().getAgents().stream()
            .map(agent -> {
                AgentDto dto = new AgentDto();
                dto.setId(agent.getId());
                dto.setModelId(agent.getModel().getId());
                dto.setSystemMessageId(agent.getSystemMessage().getId());
                return dto;
            }).toList();
        return ResponseEntity.ok(agentDtos);
    }

    @GetMapping("/agents/{id}")
    public ResponseEntity<AgentDto> getAgent(Long id) {
        User user = getUser();
        Agent agent = repository.findById(id)
            .filter(a -> a.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid agent id"));

        AgentDto dto = new AgentDto();
        dto.setId(agent.getId());
        dto.setModelId(agent.getModel().getId());
        dto.setSystemMessageId(agent.getSystemMessage().getId());

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/agents")
    public ResponseEntity<AgentDto> createAgent(@Valid AgentDto dto) {
        User user = getUser();
        SystemMessage message = messageRepository.findById(dto.getSystemMessageId())
            .filter(m -> m.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid message id"));

        AIModel model = modelRepository.findById(dto.getModelId())
            .filter(m -> m.getId().equals(user.getId()))
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Invalid model id"));

        Agent agent = new Agent();
        agent.setUser(user);
        agent.setModel(model);
        agent.setSystemMessage(message);

        repository.save(agent);

        return ResponseEntity.status(201).body(dto);
    }

    private User getUser() {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(details.getUsername()).orElseThrow(() -> new UserException("User not found"));
    }
}

package com.alexaytov.ai_hub.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
import com.alexaytov.ai_hub.model.entities.Agent;
import com.alexaytov.ai_hub.model.entities.Chat;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.AgentRepository;
import com.alexaytov.ai_hub.repositories.ChatRepository;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.UserRepository;

import jakarta.transaction.Transactional;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class ChatService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final ChatRepository repository;
    private final AgentRepository agentRepository;
    private final ModelRepository modelRepository;

    public ChatService(UserRepository userRepository, UserService userService, ChatRepository repository, AgentRepository agentRepository, ModelRepository modelRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.repository = repository;
        this.agentRepository = agentRepository;
        this.modelRepository = modelRepository;
    }

    @Transactional
    public ChatDto createChat(CreateChatDto dto) {
        User user = userService.getUser();
        if (dto.getAgentId() != null) {
            Agent agent = agentRepository.findById(dto.getAgentId())
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Agent not found"));

            Chat chat = new Chat();
            chat.setModel(agent.getModel());
            chat.setUser(user);
            repository.save(chat);

            user.getChats().add(chat);
            userRepository.save(user);

            ChatDto created = new ChatDto();
            created.setId(chat.getId());
            created.setAgentId(dto.getAgentId());
            created.setModelId(agent.getModel().getId());
            return created;
        }

        if (dto.getModelId() != null) {
            var model = modelRepository.findById(dto.getModelId())
                .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Model not found"));

            Chat chat = new Chat();
            chat.setModel(model);
            chat.setUser(user);
            repository.save(chat);

            user.getChats().add(chat);
            userRepository.save(user);

            ChatDto created = new ChatDto();
            created.setId(chat.getId());
            created.setModelId(dto.getModelId());
            return created;
        }

        throw new HttpClientErrorException(BAD_REQUEST, "Either agentId or modelId must be provided");
    }

    public void deleteChat(Long id) {
        User user = userService.getUser();
        repository.findById(id)
            .filter(c -> c.getModel().getUser().getId().equals(user.getId()))
            .ifPresent(value -> repository.deleteById(value.getId()));
    }
}

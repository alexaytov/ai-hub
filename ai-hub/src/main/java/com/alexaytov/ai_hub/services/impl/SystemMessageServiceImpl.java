package com.alexaytov.ai_hub.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.SystemMessageDto;
import com.alexaytov.ai_hub.model.entities.SystemMessage;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.SystemMessageRepository;
import com.alexaytov.ai_hub.services.SystemMessageService;
import com.alexaytov.ai_hub.services.UserService;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class SystemMessageServiceImpl implements SystemMessageService {
    private final UserService userService;
    private final ModelMapper mapper;
    private final SystemMessageRepository repository;

    public SystemMessageServiceImpl(UserService userService, ModelMapper mapper, SystemMessageRepository repository) {
        this.userService = userService;
        this.mapper = mapper;
        this.repository = repository;
    }

    @Override
    public List<SystemMessageDto> getMessages() {
        User user = userService.getUser();

        return user.getSystemMessages().stream()
            .map(message -> mapper.map(message, SystemMessageDto.class))
            .toList();
    }

    @Override
    public SystemMessageDto createMessage(SystemMessageDto message) {
        User user = userService.getUser();
        SystemMessage newMessage = mapper.map(message, SystemMessage.class);
        newMessage.setUser(user);
        newMessage = repository.save(newMessage);

        message.setId(newMessage.getId());
        return message;
    }

    @Override
    public SystemMessageDto getMessage(Long id) {
        User user = userService.getUser();
        SystemMessage message = repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, "Not found"));

        if (message.getUser().getId().equals(user.getId())) {
            return mapper.map(message, SystemMessageDto.class);
        }

        throw new HttpClientErrorException(NOT_FOUND, "Not found");
    }

    @Override
    public void deleteMessage(Long id) {
        User user = userService.getUser();
        SystemMessage message = repository.findById(id).orElse(null);

        if (message == null) {
            return;
        }

        if (message.getUser().getId().equals(user.getId())) {
            repository.delete(message);
        }
    }
}

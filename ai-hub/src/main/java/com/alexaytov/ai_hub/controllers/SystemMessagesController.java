package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.exceptions.UserException;
import com.alexaytov.ai_hub.model.dtos.SystemMessageDto;
import com.alexaytov.ai_hub.model.entities.SystemMessage;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.SystemMessageRepository;
import com.alexaytov.ai_hub.repositories.UserRepository;

import jakarta.validation.Valid;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class SystemMessagesController {

    private final ModelMapper mapper;
    private final UserRepository userRepository;
    private final SystemMessageRepository repository;

    public SystemMessagesController(ModelMapper mapper, UserRepository userRepository, SystemMessageRepository repository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.repository = repository;
    }

    @GetMapping("/system-messages")
    public ResponseEntity<List<SystemMessageDto>> getSystemMessages() {
        User user = getUser();

        return ResponseEntity.ok(user.getSystemMessages().stream()
            .map(message -> mapper.map(message, SystemMessageDto.class))
            .toList());
    }

    @PostMapping("/system-messages")
    public ResponseEntity<SystemMessageDto> createSystemMessage(@Valid @RequestBody SystemMessageDto message) {
        User user = getUser();
        SystemMessage newMessage = mapper.map(message, SystemMessage.class);
        newMessage.setUser(user);
        repository.save(newMessage);
        return ResponseEntity.status(201).body(message);
    }

    @GetMapping("/system-messages/{id}")
    public ResponseEntity<SystemMessageDto> getSystemMessage(@PathVariable Long id) {
        User user = getUser();
        SystemMessage message = repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, "Not found"));

        if (message.getUser().getId().equals(user.getId())) {
            return ResponseEntity.ok(mapper.map(message, SystemMessageDto.class));
        }
        throw new HttpClientErrorException(NOT_FOUND, "Not found");
    }

    @DeleteMapping("/system-messages/{id}")
    public ResponseEntity<Void> deleteSystemMessage(@PathVariable Long id) {
        User user = getUser();
        SystemMessage message = repository.findById(id).orElseThrow(() -> new HttpClientErrorException(NOT_FOUND, "Not found"));

        if (message.getUser().getId().equals(user.getId())) {
            repository.delete(message);
            return ResponseEntity.noContent().build();
        }

        repository.delete(message);
        return ResponseEntity.noContent().build();
    }

    private User getUser() {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(details.getUsername()).orElseThrow(() -> new UserException("User not found"));
    }
}

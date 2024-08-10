package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.ChatMessageDto;
import com.alexaytov.ai_hub.model.dtos.ChatModelQueryDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
import com.alexaytov.ai_hub.model.dtos.GetChatResponse;
import com.alexaytov.ai_hub.model.dtos.QueryResponseDto;
import com.alexaytov.ai_hub.model.entities.Chat;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.ChatRepository;
import com.alexaytov.ai_hub.services.ChatService;
import com.alexaytov.ai_hub.services.UserService;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository repository;
    private final UserService userService;

    public ChatController(ChatService chatService, ChatRepository repository, UserService userService) {
        this.chatService = chatService;
        this.repository = repository;
        this.userService = userService;
    }

    @PutMapping("/chats/{id}")
    public ResponseEntity<QueryResponseDto> queryModel(@PathVariable Long id, @Valid @RequestBody ChatModelQueryDto dto) {
        String message = chatService.query(id, dto);
        QueryResponseDto response = new QueryResponseDto();
        response.setContent(message);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/chats")
    public ResponseEntity<ChatDto> createChat(@Valid @RequestBody CreateChatDto dto) {
        ChatDto created = chatService.createChat(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/chats/{id}")
    public ResponseEntity<GetChatResponse> getChat(@PathVariable Long id) {
        User user = userService.getUser();
        Chat chat = user.getChats().stream()
            .filter(c -> c.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new HttpClientErrorException(BAD_REQUEST, "Chat not found"));

        List<ChatMessageDto> messages = chat.getMessages().stream()
            .map(msg -> {
                ChatMessageDto dto = new ChatMessageDto();
                dto.setType(msg.getType().getType());
                dto.setContent(msg.getContent());
                return dto;
            }).toList();
        GetChatResponse response = new GetChatResponse();
        response.setMessages(messages);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/chats/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatDto>> getChats() {
        List<ChatDto> dtos = repository.findAll().stream()
            .filter(chat -> chat.getUser().getId().equals(userService.getUser().getId()))
            .map(c -> {
                ChatDto dto = new ChatDto();
                dto.setId(c.getId());
                if (c.getAgent() != null) {
                    dto.setAgentId(c.getAgent().getId());
                    dto.setModelId(c.getAgent().getModel().getId());
                }

                if (c.getModel() != null) {
                    dto.setModelId(c.getModel().getId());
                }
                dto.setCreated(c.getCreated());

                return dto;
            }).toList();
        return ResponseEntity.ok(dtos);
    }
}

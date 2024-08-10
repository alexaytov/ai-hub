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

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.ChatModelQueryDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
import com.alexaytov.ai_hub.model.dtos.GetChatResponse;
import com.alexaytov.ai_hub.model.dtos.QueryResponseDto;
import com.alexaytov.ai_hub.services.ChatService;

import jakarta.validation.Valid;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PutMapping("/chats/{id}")
    public ResponseEntity<QueryResponseDto> queryModel(@PathVariable Long id, @Valid @RequestBody ChatModelQueryDto dto) {
        return ResponseEntity.status(200).body(chatService.query(id, dto));
    }

    @PostMapping("/chats")
    public ResponseEntity<ChatDto> createChat(@Valid @RequestBody CreateChatDto dto) {
        return ResponseEntity.status(201).body(chatService.createChat(dto));
    }

    @GetMapping("/chats/{id}")
    public ResponseEntity<GetChatResponse> getChat(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getChat(id));
    }

    @DeleteMapping("/chats/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        chatService.deleteChat(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatDto>> getChats() {
        return ResponseEntity.ok(chatService.getChats());
    }
}

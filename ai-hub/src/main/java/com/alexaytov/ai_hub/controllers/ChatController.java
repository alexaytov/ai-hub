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
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.ChatService;

import jakarta.validation.Valid;
import static java.lang.String.format;

@RestController
public class ChatController {

    private final AuditLogService auditLog;
    private final ChatService chatService;

    public ChatController(AuditLogService auditLog, ChatService chatService) {
        this.auditLog = auditLog;
        this.chatService = chatService;
    }

    @PutMapping("/chats/{id}")
    public ResponseEntity<QueryResponseDto> queryModel(@PathVariable Long id, @Valid @RequestBody ChatModelQueryDto dto) {
        auditLog.postAuditLog("Querying model with id " + id);
        return ResponseEntity.status(200).body(chatService.query(id, dto));
    }

    @PostMapping("/chats")
    public ResponseEntity<ChatDto> createChat(@Valid @RequestBody CreateChatDto dto) {
        auditLog.postAuditLog(format("Creating chat with model id %d and agent id %d ", dto.getModelId(), dto.getAgentId()));
        ChatDto chat = chatService.createChat(dto);
        auditLog.postAuditLog(format("Chat with id %d was created for model %d and agent %d", chat.getId(), chat.getModelId(), chat.getAgentId()));
        return ResponseEntity.status(201).body(chat);
    }

    @GetMapping("/chats/{id}")
    public ResponseEntity<GetChatResponse> getChat(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getChat(id));
    }

    @DeleteMapping("/chats/{id}")
    public ResponseEntity<Void> deleteChat(@PathVariable Long id) {
        auditLog.postAuditLog(format("Deleting chat with id %d", id));
        chatService.deleteChat(id);
        auditLog.postAuditLog(format("Chat with id %d was deleted", id));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatDto>> getChats() {
        return ResponseEntity.ok(chatService.getChats());
    }
}

package com.alexaytov.ai_hub.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexaytov.ai_hub.model.dtos.ChatDto;
import com.alexaytov.ai_hub.model.dtos.CreateChatDto;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.ChatService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatControllerTest {

    private AuditLogService auditLog;
    private ChatService chatService;
    private ChatController classUnderTest;

    @BeforeEach
    void setup() {
        auditLog = mock(AuditLogService.class);
        chatService = mock(ChatService.class);
        classUnderTest = new ChatController(auditLog, chatService);
    }

    @Test
    void whenGettingChat_thenChatIsReturned() {
        var result = classUnderTest.getChat(1L);

        assertNotNull(result);
    }

    @Test
    void whenCreatingChat_thenChatIsCreated() {
        CreateChatDto dto = new CreateChatDto();
        ChatDto createdChatDto = new ChatDto();
        when(chatService.createChat(dto)).thenReturn(createdChatDto);

        var result = classUnderTest.createChat(dto);

        assertNotNull(result);
        verify(chatService).createChat(dto);
        verify(auditLog).postAuditLog("Creating chat with model id null and agent id null ");
        verify(auditLog).postAuditLog("Chat with id null was created for model null and agent null");
    }

    @Test
    void whenDeletingChat_thenChatIsDeleted() {
        var result = classUnderTest.deleteChat(1L);

        assertNotNull(result);
        verify(chatService).deleteChat(1L);
        verify(auditLog).postAuditLog("Deleting chat with id 1");
        verify(auditLog).postAuditLog("Chat with id 1 was deleted");
    }


}
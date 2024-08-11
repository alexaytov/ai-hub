package com.alexaytov.ai_hub.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.alexaytov.ai_hub.model.dtos.SystemMessageDto;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.SystemMessageService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SystemMessagesControllerTest {

    private AuditLogService auditLog;
    private SystemMessageService service;
    private SystemMessagesController classUnderTest;

    @BeforeEach
    void setup () {
        auditLog = mock(AuditLogService.class);
        service = mock(SystemMessageService.class);
        classUnderTest = new SystemMessagesController(auditLog, service);
    }

    @Test
    void whenGettingSystemMessage_thenSystemMessageIsReturned() {
        var result = classUnderTest.getSystemMessage(1L);

        assertNotNull(result);
    }

    @Test
    void whenCreatingSystemMessage_thenSystemMessageIsCreated() {
        SystemMessageDto dto = new SystemMessageDto();
        when(service.createMessage(null)).thenReturn(dto);

        var result = classUnderTest.createSystemMessage(null);

        assertNotNull(result);
        Mockito.verify(service).createMessage(null);
        Mockito.verify(auditLog).postAuditLog("Creating system message");
        Mockito.verify(auditLog).postAuditLog("System message was created with id null");
    }

    @Test
    void whenDeletingSystemMessage_thenSystemMessageIsDeleted() {
        var result = classUnderTest.deleteSystemMessage(1L);

        assertNotNull(result);
        Mockito.verify(service).deleteMessage(1L);
        Mockito.verify(auditLog).postAuditLog("Deleting system message with id 1");
        Mockito.verify(auditLog).postAuditLog("System message with id 1 was deleted");
    }

}
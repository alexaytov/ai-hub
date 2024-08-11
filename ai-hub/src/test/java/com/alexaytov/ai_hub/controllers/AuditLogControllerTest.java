package com.alexaytov.ai_hub.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexaytov.ai_hub.services.AuditLogService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

class AuditLogControllerTest {

    private AuditLogService auditLog;
    private AuditLogController classUnderTest;

    @BeforeEach
    void setup() {
        auditLog = mock(AuditLogService.class);
        classUnderTest = new AuditLogController(auditLog);
    }

    @Test
    void whenGettingAuditLogs_thenAuditLogsAreReturned() {
        var result = classUnderTest.getAuditLogs();

        assertNotNull(result);
    }
}
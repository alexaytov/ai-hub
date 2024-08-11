package com.alexaytov.ai_hub.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexaytov.ai_hub.model.dtos.AgentDto;
import com.alexaytov.ai_hub.services.AgentService;
import com.alexaytov.ai_hub.services.AuditLogService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AgentsControllerTest {

    private AgentService agentService;
    private AuditLogService auditLog;
    private AgentsController classUnderTest;

    @BeforeEach
    void setup() {
        agentService = mock(AgentService.class);
        auditLog = mock(AuditLogService.class);
        classUnderTest = new AgentsController(auditLog, agentService);
    }

    @Test
    void givenAgentExists_whenGettingAgent_thenAgentIsReturned() {
        var result = classUnderTest.getAgent(1L);

        assertNotNull(result);
    }

    @Test
    void givenAgentExists_whenDeletingAgent_thenAgentIsDeleted() {
        var result = classUnderTest.deleteAgent(1L);

        assertNotNull(result);
        verify(agentService).deleteAgent(1L);
        verify(auditLog).postAuditLog("Deleting agent with id 1");
        verify(auditLog).postAuditLog("Agent with id 1 was deleted");

    }

    @Test
    void whenCreatingAgent_thenAgentIsCreated() {
        AgentDto dto = new AgentDto();
        var result = classUnderTest.createAgent(dto);

        assertNotNull(result);
        verify(agentService).createAgent(dto);
        verify(auditLog).postAuditLog("Creating agent with name null");
        verify(auditLog).postAuditLog("Agent with name null and id null was created");
    }

    @Test
    void whenGettingAgents_thenAgentsAreReturned() {
        var result = classUnderTest.getAgents();

        assertNotNull(result);
        verify(agentService).getAgents();
    }

    @Test
    void whenGettingAgent_thenAgentIsReturned() {
        var result = classUnderTest.getAgent(1L);

        assertNotNull(result);
        verify(agentService).getAgent(1L);
    }

}
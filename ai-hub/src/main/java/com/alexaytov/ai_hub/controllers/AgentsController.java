package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.ai_hub.model.dtos.AgentDto;
import com.alexaytov.ai_hub.services.AgentService;
import com.alexaytov.ai_hub.services.AuditLogService;

import jakarta.validation.Valid;
import static java.lang.String.format;

@RestController
public class AgentsController {

    private final AuditLogService auditLog;
    private final AgentService service;

    public AgentsController(AuditLogService auditLog, AgentService service) {
        this.auditLog = auditLog;
        this.service = service;
    }

    @GetMapping("/agents")
    public ResponseEntity<List<AgentDto>> getAgents() {
        return ResponseEntity.ok(service.getAgents());
    }

    @GetMapping("/agents/{id}")
    public ResponseEntity<AgentDto> getAgent(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAgent(id));
    }

    @DeleteMapping("/agents/{id}")
    public ResponseEntity<Void> deleteAgent(@PathVariable Long id) {
        auditLog.postAuditLog(format("Deleting agent with id %s", id));
        service.deleteAgent(id);
        auditLog.postAuditLog(format("Agent with id %s was deleted", id));

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/agents")
    public ResponseEntity<AgentDto> createAgent(@Valid @RequestBody AgentDto dto) {
        auditLog.postAuditLog(format("Creating agent with name %s", dto.getName()));
        AgentDto agent = service.createAgent(dto);
        auditLog.postAuditLog(format("Agent with name %s and id %d was created", dto.getName(), dto.getId()));

        return ResponseEntity.status(201).body(agent);
    }
}

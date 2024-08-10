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

import jakarta.validation.Valid;

@RestController
public class AgentsController {

    private final AgentService service;

    public AgentsController(AgentService service) {
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
        service.deleteAgent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/agents")
    public ResponseEntity<AgentDto> createAgent(@Valid @RequestBody AgentDto dto) {
        return ResponseEntity.status(201).body(service.createAgent(dto));
    }
}

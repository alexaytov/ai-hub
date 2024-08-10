package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.ai_hub.model.dtos.AuditLogDto;
import com.alexaytov.ai_hub.services.AuditLogService;

@RestController
public class AuditLogController {

    private final AuditLogService service;

    public AuditLogController(AuditLogService service) {
        this.service = service;
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<List<AuditLogDto>> getAuditLogs() {
        return ResponseEntity.ok(service.getAuditLogs());
    }
}

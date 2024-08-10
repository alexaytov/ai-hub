package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.ai_hub.model.dtos.SystemMessageDto;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.SystemMessageService;

import jakarta.validation.Valid;
import static java.lang.String.format;

@RestController
public class SystemMessagesController {

    private final AuditLogService auditLog;
    private final SystemMessageService service;

    public SystemMessagesController(AuditLogService auditLog, SystemMessageService service) {
        this.auditLog = auditLog;
        this.service = service;
    }

    @GetMapping("/system-messages")
    public ResponseEntity<List<SystemMessageDto>> getSystemMessages() {
        return ResponseEntity.ok(service.getMessages());
    }

    @PostMapping("/system-messages")
    public ResponseEntity<SystemMessageDto> createSystemMessage(@Valid @RequestBody SystemMessageDto message) {
        auditLog.postAuditLog("Creating system message");
        SystemMessageDto createdMessage = service.createMessage(message);
        auditLog.postAuditLog("System message was created");

        return ResponseEntity.status(201).body(createdMessage);
    }

    @GetMapping("/system-messages/{id}")
    public ResponseEntity<SystemMessageDto> getSystemMessage(@PathVariable Long id) {
        return ResponseEntity.ok(service.getMessage(id));
    }

    @DeleteMapping("/system-messages/{id}")
    public ResponseEntity<Void> deleteSystemMessage(@PathVariable Long id) {
        auditLog.postAuditLog("Deleting system message with id " + id);
        service.deleteMessage(id);
        auditLog.postAuditLog(format("System message with id %d was deleted", id));
        return ResponseEntity.noContent().build();
    }
}

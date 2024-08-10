package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.ai_hub.model.dtos.AIModelDto;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.ModelService;

import jakarta.validation.Valid;

@RestController
public class AIModelController {

    private final AuditLogService auditLog;
    private final ModelService modelService;

    public AIModelController(AuditLogService auditLog, ModelService modelService) {
        this.auditLog = auditLog;
        this.modelService = modelService;
    }

    @DeleteMapping("/chat-models/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        auditLog.postAuditLog("Deleting model with id " + id);
        modelService.deleteModel(id);
        auditLog.postAuditLog("Model with id " + id + " was deleted");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chat-models")
    public ResponseEntity<List<AIModelDto>> getModels() {
        return ResponseEntity.ok(modelService.getModels());
    }

    @PostMapping("/chat-models")
    public ResponseEntity<AIModelDto> createModel(@Valid @RequestBody AIModelDto model) {
        auditLog.postAuditLog("Creating model with name " + model.getName());
        AIModelDto createdModel = modelService.createModel(model);
        auditLog.postAuditLog("Model with name " + model.getName() + " and id " + model.getId() + " was created");
        return ResponseEntity.ok(createdModel);
    }

    @GetMapping("/chat-models/{id}")
    public ResponseEntity<AIModelDto> getModel(@PathVariable Long id) {
        return ResponseEntity.ok(modelService.getModel(id));
    }

}

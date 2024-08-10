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
import com.alexaytov.ai_hub.services.ModelService;

import jakarta.validation.Valid;

@RestController
public class AIModelController {

    private final ModelService modelService;

    public AIModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @DeleteMapping("/chat-models/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chat-models")
    public ResponseEntity<List<AIModelDto>> getModels() {
        return ResponseEntity.ok(modelService.getModels());
    }

    @PostMapping("/chat-models")
    public ResponseEntity<AIModelDto> createModel(@Valid @RequestBody AIModelDto model) {
        return ResponseEntity.ok(modelService.createModel(model));
    }

    @GetMapping("/chat-models/{id}")
    public ResponseEntity<AIModelDto> getModel(@PathVariable Long id) {
        return ResponseEntity.ok(modelService.getModel(id));
    }

}

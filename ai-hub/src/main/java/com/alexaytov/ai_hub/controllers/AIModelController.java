package com.alexaytov.ai_hub.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.ai_hub.model.dtos.AIModelDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.services.ModelService;
import com.alexaytov.ai_hub.services.UserService;

import jakarta.validation.Valid;

@RestController
public class AIModelController {

    private final ModelService modelService;
    private final ModelRepository repository;
    private final ModelMapper mapper;
    private final UserService userService;

    public AIModelController(ModelService modelService, ModelRepository repository, ModelMapper mapper, UserService userService) {
        this.modelService = modelService;
        this.repository = repository;
        this.mapper = mapper;
        this.userService = userService;
    }

    @DeleteMapping("/chat-models/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        AIModel model = repository.findById(id).orElse(null);
        if (model == null) {
            return ResponseEntity.noContent().build();
        }

        if (!model.getUser().equals(userService.getUser())) {
            return ResponseEntity.noContent().build();
        }

        repository.delete(model);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/chat-models")
    public ResponseEntity<List<AIModelDto>> getModels() {
        List<AIModelDto> data = repository.findAll().stream()
            .map(model -> mapper.map(model, AIModelDto.class))
            .toList();
        return ResponseEntity.ok(data);
    }

    @PostMapping("/chat-models")
    public ResponseEntity<AIModelDto> createModel(@Valid @RequestBody AIModelDto model) {
        AIModel newModel = modelService.save(model);
        model.setId(newModel.getId());

        return ResponseEntity.ok(model);
    }

    @GetMapping("/chat-models/{id}")
    public ResponseEntity<AIModelDto> getModel(@PathVariable Long id) {
        AIModel model = repository.findById(id).orElse(null);
        if (model == null) {
            return ResponseEntity.notFound().build();
        }

        AIModelDto dto = mapper.map(model, AIModelDto.class);
        dto.setType(model.getType().getType());
        dto.clearApiKey();
        return ResponseEntity.ok(dto);
    }

}

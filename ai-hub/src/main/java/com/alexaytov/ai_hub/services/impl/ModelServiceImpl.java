package com.alexaytov.ai_hub.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.AIModelDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.ModelTypeRepository;
import com.alexaytov.ai_hub.services.ModelService;
import com.alexaytov.ai_hub.services.UserService;
import com.alexaytov.ai_hub.utils.Encryption;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ModelServiceImpl implements ModelService {

    private final ModelRepository repository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final Encryption encryption;
    private final ModelTypeRepository modelTypeRepository;

    public ModelServiceImpl(ModelRepository repository, UserService userService, ModelMapper mapper, Encryption encryption, ModelTypeRepository modelTypeRepository) {
        this.repository = repository;
        this.userService = userService;
        this.mapper = mapper;
        this.encryption = encryption;
        this.modelTypeRepository = modelTypeRepository;
    }

    @Override
    public AIModelDto createModel(AIModelDto dto) {
        if (repository.findByName(dto.getName()).isPresent()) {
            throw new HttpClientErrorException(BAD_REQUEST, "Model with this name already exists");
        }

        AIModel newModel = mapper.map(dto, AIModel.class);
        newModel.setApiKey(encryption.encrypt(dto.getApiKey()));

        modelTypeRepository.findByType(dto.getType())
            .ifPresentOrElse(newModel::setType, () -> {
                throw new IllegalArgumentException("Invalid model type");
            });

        newModel.setUser(userService.getUser());
        newModel = repository.save(newModel);

        dto.setId(newModel.getId());
        return dto;
    }


    @Override
    public void deleteModel(Long id) {
        AIModel model = repository.findById(id).orElse(null);
        if (model == null) {
            return;
        }

        if (!model.getUser().getId().equals(userService.getUser().getId())) {
            return;
        }

        repository.delete(model);
    }

    @Override
    public List<AIModelDto> getModels() {
        return repository.findAll().stream()
            .map(model -> mapper.map(model, AIModelDto.class))
            .toList();
    }

    @Override
    public AIModelDto getModel(Long id) {
        AIModel model = repository.findById(id).orElse(null);
        if (model == null) {
            throw new HttpClientErrorException(NOT_FOUND, "Model not found");
        }

        AIModelDto dto = mapper.map(model, AIModelDto.class);
        dto.setType(model.getType().getType());
        dto.clearApiKey();
        return dto;
    }
}

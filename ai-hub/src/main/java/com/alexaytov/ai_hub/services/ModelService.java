package com.alexaytov.ai_hub.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.AIModelDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.ModelTypeRepository;
import com.alexaytov.ai_hub.utils.AES256Encryption;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class ModelService {

    private final ModelRepository repository;
    private final UserService userService;
    private final ModelMapper mapper;
    private final AES256Encryption encryption;
    private final ModelTypeRepository modelTypeRepository;

    public ModelService(ModelRepository repository, UserService userService, ModelMapper mapper, AES256Encryption encryption, ModelTypeRepository modelTypeRepository) {
        this.repository = repository;
        this.userService = userService;
        this.mapper = mapper;
        this.encryption = encryption;
        this.modelTypeRepository = modelTypeRepository;
    }

    public AIModel save(AIModelDto dto) {
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
        return repository.save(newModel);
    }


}

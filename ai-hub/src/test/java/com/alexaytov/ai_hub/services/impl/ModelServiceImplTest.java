package com.alexaytov.ai_hub.services.impl;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.alexaytov.ai_hub.model.dtos.AIModelDto;
import com.alexaytov.ai_hub.model.entities.AIModel;
import com.alexaytov.ai_hub.model.entities.AIModelType;
import com.alexaytov.ai_hub.model.entities.AIModelTypeEntity;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.ModelRepository;
import com.alexaytov.ai_hub.repositories.ModelTypeRepository;
import com.alexaytov.ai_hub.services.UserService;
import com.alexaytov.ai_hub.utils.Encryption;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ModelServiceImplTest {


    private ModelRepository modelRepository;
    private UserService userService;
    private ModelMapper mapper;
    private Encryption encryption;
    private ModelTypeRepository modelTypeRepository;

    private ModelServiceImpl classUnderTest;

    @BeforeEach
    void setUp() {
        modelRepository = mock(ModelRepository.class);
        userService = mock(UserService.class);
        mapper = mock(ModelMapper.class);
        encryption = mock(Encryption.class);
        modelTypeRepository = mock(ModelTypeRepository.class);

        classUnderTest = new ModelServiceImpl(
            modelRepository,
            userService,
            mapper,
            encryption,
            modelTypeRepository
        );
    }

    @Test
    void givenExistingModel_whenGettingModel_thenCorrectValueIsReturned() {
        AIModel model = new AIModel();
        model.setId(1L);
        model.setName("model");
        model.setApiKey("key");

        AIModelTypeEntity type = new AIModelTypeEntity();
        type.setType(AIModelType.OPEN_AI);
        model.setType(type);

        when(mapper.map(model, AIModelDto.class)).thenReturn(new AIModelDto());

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));

        AIModelDto result = classUnderTest.getModel(1L);

        assertNull(result.getApiKey());
        assertEquals(AIModelType.OPEN_AI, result.getType());
    }

    @Test
    void whenGettingModels_thenCorrectValuesAreReturned() {
        AIModel model = new AIModel();
        model.setId(1L);
        model.setName("model");
        model.setApiKey("key");

        AIModelTypeEntity type = new AIModelTypeEntity();
        type.setType(AIModelType.OPEN_AI);
        model.setType(type);

        when(mapper.map(model, AIModelDto.class)).thenReturn(new AIModelDto());

        when(modelRepository.findAll()).thenReturn(List.of(model));

        List<AIModelDto> result = classUnderTest.getModels();

        assertEquals(1, result.size());
        assertEquals(AIModelType.OPEN_AI, result.get(0).getType());
    }

    @Test
    void givenExistingModel_whenDeletingModel_thenCorrectModelIsDeleted() {
        User user = new User();
        user.setId(1L);


        AIModel model = new AIModel();
        model.setId(1L);
        model.setUser(user);

        when(userService.getUser()).thenReturn(user);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));

        classUnderTest.deleteModel(1L);

        verify(modelRepository).delete(model);
    }

    @Test
    void givenNonExistingModel_whenDeletingModel_thenNothingHappens() {
        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        when(modelRepository.findById(1L)).thenReturn(Optional.empty());

        classUnderTest.deleteModel(1L);

        verify(modelRepository, never()).delete(any());
    }

    @Test
    void givenModelForDifferentUser_whenDeletingModel_thenNothingHappens() {
        User user = new User();
        user.setId(1L);

        AIModel model = new AIModel();
        model.setId(1L);
        User secondUser = new User();
        secondUser.setId(2L);
        model.setUser(secondUser);

        when(userService.getUser()).thenReturn(user);
        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));

        classUnderTest.deleteModel(1L);

        verify(modelRepository, never()).delete(any());
    }

    @Test
    void givenModelWithSameNameExists_whenCreatingModel_thenCorrectExceptionIsThrown() {
        AIModel model = new AIModel();
        model.setName("model");

        when(modelRepository.findByName("model")).thenReturn(Optional.of(model));

        AIModelDto dto = new AIModelDto();
        dto.setName("model");

        try {
            classUnderTest.createModel(dto);
        } catch (Exception e) {
            assertEquals("400 Model with this name already exists", e.getMessage());
        }
    }

}
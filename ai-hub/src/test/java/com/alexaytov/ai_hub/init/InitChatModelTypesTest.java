package com.alexaytov.ai_hub.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.alexaytov.ai_hub.model.entities.AIModelTypeEntity;
import com.alexaytov.ai_hub.repositories.ModelTypeRepository;

import static com.alexaytov.ai_hub.model.entities.AIModelType.OPEN_AI;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InitChatModelTypesTest {

    private ModelTypeRepository typeRepository;
    private InitChatModelTypes classUnderTest;


    @BeforeEach
    void setup() {
        typeRepository = mock(ModelTypeRepository.class);
        classUnderTest = new InitChatModelTypes(typeRepository);
    }

    @Test
    void givenNonEmptyModelTypes_whenRunning_thenNothingHappens() {
        when(typeRepository.count()).thenReturn(1L);

        classUnderTest.run();

        verify(typeRepository, never()).save(any());
        verify(typeRepository, never()).saveAll(any());
    }

    @Test
    void givenEmptyModelTypes_whenRunning_thenCorrectValuesAreSaved() {
        when(typeRepository.count()).thenReturn(0L);

        classUnderTest.run();

        ArgumentCaptor<AIModelTypeEntity> typeCaptor = ArgumentCaptor.forClass(AIModelTypeEntity.class);
        verify(typeRepository).save(typeCaptor.capture());

        assertEquals(OPEN_AI, typeCaptor.getValue().getType());
    }
}
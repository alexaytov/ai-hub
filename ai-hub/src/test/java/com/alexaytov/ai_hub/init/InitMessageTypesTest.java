package com.alexaytov.ai_hub.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.alexaytov.ai_hub.model.entities.MessageTypeEntity;
import com.alexaytov.ai_hub.model.enums.MessageType;
import com.alexaytov.ai_hub.repositories.MessageTypeRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InitMessageTypesTest {

    private MessageTypeRepository typeRepository;
    private InitMessageTypes classUnderTest;

    @BeforeEach
    void setUp() {
        typeRepository = mock(MessageTypeRepository.class);
        classUnderTest = new InitMessageTypes(typeRepository);
    }

    @Test
    void givenEnumTypesIsNotEmpty_whenRunning_thenNothingHappens() {
        when(typeRepository.count()).thenReturn(1L);

        classUnderTest.run();

        verify(typeRepository, never()).save(any());
        verify(typeRepository, never()).saveAll(any());
    }

    @Test
    void givenEnumTypesIsEmpty_whenRunning_thenTypesAreSaved() {
        when(typeRepository.count()).thenReturn(0L);

        classUnderTest.run();

        ArgumentCaptor<Iterable<MessageTypeEntity>> typesCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(typeRepository).saveAll(typesCaptor.capture());

        typesCaptor.getValue().forEach(
            type -> {
                if (type.getType().equals(MessageType.USER)) {
                    return;
                }
                if (type.getType().equals(MessageType.ASSISTANT)) {
                    return;
                }
                throw new AssertionError("Unexpected type: " + type.getType());
            }
        );
    }

}
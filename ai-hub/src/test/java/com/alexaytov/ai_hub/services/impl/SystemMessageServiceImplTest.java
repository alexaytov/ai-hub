package com.alexaytov.ai_hub.services.impl;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.alexaytov.ai_hub.model.dtos.SystemMessageDto;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.repositories.SystemMessageRepository;
import com.alexaytov.ai_hub.services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SystemMessageServiceImplTest {

    private UserService userService;
    private ModelMapper modelMapper;
    private SystemMessageRepository systemMessageRepository;
    private SystemMessageServiceImpl classUnderTest;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        modelMapper = mock(ModelMapper.class);
        systemMessageRepository = mock(SystemMessageRepository.class);

        classUnderTest = new SystemMessageServiceImpl(
            userService,
            modelMapper,
            systemMessageRepository
        );
    }

    @Test
    void whenGettingMessages_thenCorrectValueIsReturned() {
        User user = new User();
        com.alexaytov.ai_hub.model.entities.SystemMessage message = new com.alexaytov.ai_hub.model.entities.SystemMessage();
        message.setId(1L);
        user.setSystemMessages(List.of(message));

        when(userService.getUser()).thenReturn(user);
        SystemMessageDto dto = new SystemMessageDto();
        dto.setId(1L);
        when(modelMapper.map(message, SystemMessageDto.class)).thenReturn(dto);

        List<SystemMessageDto> messages = classUnderTest.getMessages();

        assertEquals(1, messages.size());
        assertEquals(1L, messages.get(0).getId());
    }

    @Test
    void givenExistingMessage_whenGettingMessage_thenCorrectValueIsReturned() {
        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        com.alexaytov.ai_hub.model.entities.SystemMessage message = new com.alexaytov.ai_hub.model.entities.SystemMessage();
        message.setId(1L);
        message.setUser(user);
        when(systemMessageRepository.findById(1L)).thenReturn(java.util.Optional.of(message));

        SystemMessageDto dto = new SystemMessageDto();
        dto.setId(1L);
        when(modelMapper.map(message, SystemMessageDto.class)).thenReturn(dto);

        SystemMessageDto result = classUnderTest.getMessage(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void givenNonExistingMessage_whenGettingMessage_thenExceptionIsThrown() {
        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        when(systemMessageRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        try {
            classUnderTest.getMessage(1L);
        } catch (Exception e) {
            assertEquals("404 Not found", e.getMessage());
        }
    }

    @Test
    void givenExistingMessage_whenDeletingMessage_thenCorrectValueIsDeleted() {
        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        com.alexaytov.ai_hub.model.entities.SystemMessage message = new com.alexaytov.ai_hub.model.entities.SystemMessage();
        message.setId(1L);
        message.setUser(user);
        when(systemMessageRepository.findById(1L)).thenReturn(java.util.Optional.of(message));

        classUnderTest.deleteMessage(1L);

        verify(systemMessageRepository).delete(message);
    }

    @Test
    void givenNonExistingMessage_whenDeletingMessage_thenNothingHappens() {
        User user = new User();
        user.setId(1L);

        when(userService.getUser()).thenReturn(user);
        when(systemMessageRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        classUnderTest.deleteMessage(1L);

        verify(systemMessageRepository, never()).delete(any());
    }

}
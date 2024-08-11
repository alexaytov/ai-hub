package com.alexaytov.ai_hub.services.impl;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.alexaytov.ai_hub.model.dtos.AuditLogDto;
import com.alexaytov.ai_hub.model.dtos.AuditLogUserDto;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditLogServiceImplTest {

    private static final String ENDPOINT = "http://localhost:8080";

    private UserService userService;
    private RestTemplate template;
    private AuditLogServiceImpl classUnderTest;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        template = mock(RestTemplate.class);

        classUnderTest = new AuditLogServiceImpl(ENDPOINT, template, userService);
    }

    @Test
    void givenUserId_whenCreateUser_thenUserIsCreated() {
        classUnderTest.createUser(1L);

        verify(template).postForObject(ENDPOINT, new AuditLogUserDto(1L), AuditLogDto.class);
    }

    @Test
    void whenDeleteUser_thenUserIsDeleted() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        classUnderTest.deleteUser();

        verify(template).delete(ENDPOINT + "/users/1");
    }

    @Test
    void givenMessage_whenPostAuditLog_thenAuditLogIsPosted() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        classUnderTest.postAuditLog("message");

        AuditLogDto body = new AuditLogDto();
        body.setMessage("message");
        verify(template).put(ENDPOINT + "/users/1/logs", body);
    }

    @Test
    void whenGettingAuditLogs_thenCorrectValuesAreReturned() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        AuditLogDto first = new AuditLogDto();
        first.setMessage("first");
        first.setTimestamp(1L);

        AuditLogDto second = new AuditLogDto();
        second.setMessage("second");
        second.setTimestamp(2L);

        when(template.getForObject(ENDPOINT + "/users/1/logs", AuditLogDto[].class))
            .thenReturn(new AuditLogDto[]{first, second});

        List<AuditLogDto> result = classUnderTest.getAuditLogs();

        assertEquals(2, result.size());
        assertEquals(first.getMessage(), result.get(0).getMessage());
        assertEquals(second.getMessage(), result.get(1).getMessage());
    }

    @Test
    void givenExceptionThrown_whenCreatingUser_thenCorrectExceptionIsThrown() {
        when(template.postForObject(ENDPOINT, new AuditLogUserDto(1L), AuditLogDto.class))
            .thenThrow(new RestClientException("Error"));

        try {
            classUnderTest.createUser(1L);
        } catch (Exception ex) {
            assertEquals("500 Error while creating user in audit log service", ex.getMessage());
        }
    }

    @Test
    void givenExceptionWhenDeletingUser_thenCorrectExceptionIsThrown() {
        User user = new User();
        user.setId(1L);
        when(userService.getUser()).thenReturn(user);

        doThrow(new RestClientException("Error")).when(template).delete(ENDPOINT + "/users/1");

        try {
            classUnderTest.deleteUser();
        } catch (Exception ex) {
            assertEquals("500 Error while deleting user in audit log service", ex.getMessage());
        }
    }

}
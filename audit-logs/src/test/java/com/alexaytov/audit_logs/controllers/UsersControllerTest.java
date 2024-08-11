package com.alexaytov.audit_logs.controllers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import com.alexaytov.audit_logs.models.dtos.AuditLogDto;
import com.alexaytov.audit_logs.models.dtos.UserDto;
import com.alexaytov.audit_logs.services.UserService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class UsersControllerTest {

    private UserService service;
    private UsersController classUnderTest;

    @BeforeEach
    void setup() {
        service = mock(UserService.class);
        classUnderTest = new UsersController(service);
    }

    @Test
    void whenCreatingUser_thenUserIsCreated() {
        UserDto dto = new UserDto();
        ResponseEntity<UserDto> response = classUnderTest.createUser(dto);

        assertNotNull(response);
        verify(service).createUser(dto);
    }

    @Test
    void whenDeletingUser_thenUserIsDeleted() {
        ResponseEntity<Void> response = classUnderTest.deleteUser(1L);

        assertNotNull(response);
        verify(service).deleteUser(1L);
    }

    @Test
    void whenAddingLogs_thenCorrectLogsAreAdded() {
        AuditLogDto dto = new AuditLogDto();
        ResponseEntity<Void> response = classUnderTest.addAuditLog(1L, dto);

        assertNotNull(response);
        verify(service).addAuditLog(1L, dto);
    }

    @Test
    void whenGettingAuditLogs_thenCorrectLogsAreReturned() {
        ResponseEntity<List<AuditLogDto>> response = classUnderTest.getUserLogs(1L);

        assertNotNull(response);
        verify(service).getUserLogs(1L);
    }

}
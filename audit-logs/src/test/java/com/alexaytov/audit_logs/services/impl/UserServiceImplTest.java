package com.alexaytov.audit_logs.services.impl;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.alexaytov.audit_logs.models.dtos.AuditLogDto;
import com.alexaytov.audit_logs.models.dtos.UserDto;
import com.alexaytov.audit_logs.models.entities.AuditLog;
import com.alexaytov.audit_logs.models.entities.User;
import com.alexaytov.audit_logs.repositories.AuditLogRepository;
import com.alexaytov.audit_logs.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserRepository repository;
    private AuditLogRepository auditLogRepository;
    private UserServiceImpl classUnderTest;

    @BeforeEach
    void setup() {
        repository = mock(UserRepository.class);
        auditLogRepository = mock(AuditLogRepository.class);
        classUnderTest = new UserServiceImpl(repository, auditLogRepository);
    }

    @Test
    void whenCreatingUser_thenUserIsCreated() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        classUnderTest.createUser(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        assertEquals(1L, userCaptor.getValue().getUserId());
    }

    @Test
    void whenDeletingUser_thenUserIsDeleted() {
        User user = new User();
        when(repository.findByUserId(1L)).thenReturn(Optional.of(user));

        classUnderTest.deleteUser(1L);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        assertEquals(true, userCaptor.getValue().isDeleted());
    }

    @Test
    void whenAddingAuditLog_thenCorreLogIsAdded() {
        User user = new User();
        when(repository.findByUserId(1L)).thenReturn(Optional.of(user));

        AuditLogDto dto = new AuditLogDto();
        classUnderTest.addAuditLog(1L, dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());

        assertEquals(1, userCaptor.getValue().getAuditLogs().size());
    }

    @Test
    void whenGettingUserLogs_thenLogsAreReturned() {
        User user = new User();
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setId(1L);
        log.setMessage("msg");
        log.setTimestamp(1L);
        user.setAuditLogs(List.of(log));
        when(repository.findByUserId(1L)).thenReturn(Optional.of(user));

        List<AuditLogDto> actual = classUnderTest.getUserLogs(1L);

        verify(repository).findByUserId(1L);
        assertEquals(1, actual.size());
        AuditLogDto dto = actual.get(0);
        assertEquals(1L, dto.getTimestamp());
        assertEquals("msg", dto.getMessage());
    }

}
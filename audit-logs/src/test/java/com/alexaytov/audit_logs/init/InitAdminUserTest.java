package com.alexaytov.audit_logs.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.alexaytov.audit_logs.models.entities.User;
import com.alexaytov.audit_logs.repositories.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InitAdminUserTest {

    private UserRepository repo;
    private InitAdminUser classUnderTest;

    @BeforeEach
    void setup() {
        repo = mock(UserRepository.class);
        classUnderTest = new InitAdminUser(repo);
    }

    @Test
    void givenNonEmptyRepo_thenNothingIsCreated() {
        when(repo.count()).thenReturn(1L);

        classUnderTest.run();

        verify(repo, never()).save(any());
        verify(repo, never()).saveAll(any());
    }

    @Test
    void givenEmptyRepo_thenAdminUserIsCreated() {
        when(repo.count()).thenReturn(0L);

        classUnderTest.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repo).save(userCaptor.capture());
        User user = userCaptor.getValue();
        assertEquals(1L, user.getUserId());
    }

}
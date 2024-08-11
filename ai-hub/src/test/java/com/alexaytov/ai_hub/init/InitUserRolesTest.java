package com.alexaytov.ai_hub.init;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.alexaytov.ai_hub.model.entities.UserRoleEntity;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.repositories.UserRoleRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InitUserRolesTest {

    private UserRoleRepository roleRepository;
    private InitUserRoles classUnderTest;

    @BeforeEach
    void setUp() {
        roleRepository = mock(UserRoleRepository.class);
        classUnderTest = new InitUserRoles(roleRepository);
    }

    @Test
    void givenEnumRolesIsNotEmpty_whenRunning_thenNothingHappens() throws Exception {
        when(roleRepository.count()).thenReturn(1L);

        classUnderTest.run();

        verify(roleRepository, never()).save(any());
        verify(roleRepository, never()).saveAll(any());
    }

    @Test
    void givenEnumRolesIsEmpty_whenRunning_thenRolesAreSaved() throws Exception {
        when(roleRepository.count()).thenReturn(0L);

        classUnderTest.run();


        ArgumentCaptor<Iterable<UserRoleEntity>> rolesCaptor = ArgumentCaptor.forClass(Iterable.class);
        verify(roleRepository).saveAll(rolesCaptor.capture());

        rolesCaptor.getValue().forEach(role -> {
            if (role.getRole().equals(UserRole.ADMIN)) {
                return;
            }
            if (role.getRole().equals(UserRole.USER)) {
                return;
            }
            throw new AssertionError("Unexpected role: " + role.getRole());
        });
    }

}
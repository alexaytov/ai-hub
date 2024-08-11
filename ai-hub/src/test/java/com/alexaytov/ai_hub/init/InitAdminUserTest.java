package com.alexaytov.ai_hub.init;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.entities.UserRoleEntity;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.repositories.UserRepository;
import com.alexaytov.ai_hub.repositories.UserRoleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InitAdminUserTest {

    private PasswordEncoder encoder;
    private UserRoleRepository roleRepository;
    private UserRepository userRepository;
    private InitAdminUser classUnderTest;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        roleRepository = mock(UserRoleRepository.class);
        encoder = mock(PasswordEncoder.class);
        classUnderTest = new InitAdminUser(userRepository, roleRepository, encoder);
    }

    @Test
    void givenAdminUserExists_whenRunning_thenNothingHappens() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(new User()));

        classUnderTest.run();

        verify(userRepository, never()).save(any());
    }

    @Test
    void givenAdminUserDoesNotExist_whenRunning_thenUserIsSaved() {
        when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
        when(encoder.encode("admin")).thenReturn("encoded");

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setRole(UserRole.USER);
        UserRoleEntity adminRole = new UserRoleEntity();
        adminRole.setRole(UserRole.ADMIN);
        when(roleRepository.findByRole(UserRole.ADMIN)).thenReturn(userRole);
        when(roleRepository.findByRole(UserRole.USER)).thenReturn(userRole);

        classUnderTest.run();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User actual = userCaptor.getValue();
        assertEquals("admin", actual.getUsername());
        assertEquals("encoded", actual.getPassword());
        actual.getRoles().forEach(
            role -> {
                if (role.getRole().equals(UserRole.ADMIN)) {
                    return;
                }
                if (role.getRole().equals(UserRole.USER)) {
                    return;
                }
                throw new AssertionError("Unexpected role: " + role.getRole());
            }
        );
    }

}
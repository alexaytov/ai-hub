package com.alexaytov.ai_hub.services.impl;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.HttpClientErrorException;

import com.alexaytov.ai_hub.model.dtos.CredentialsDto;
import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.entities.UserRoleEntity;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.repositories.UserRepository;
import com.alexaytov.ai_hub.repositories.UserRoleRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    private UserRepository userRepository;
    private UserRoleRepository userRoleRepository;
    private PasswordEncoder encoder;

    private UserServiceImpl classUnderTest;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userRoleRepository = mock(UserRoleRepository.class);
        encoder = mock(PasswordEncoder.class);

        classUnderTest = new UserServiceImpl(
            userRepository,
            userRoleRepository,
            encoder
        );
    }

    @Test
    void whenGettingUser_thenCorrectUserIsReturned() {
        org.springframework.security.core.userdetails.User authUser = new org.springframework.security.core.userdetails.User("user", "password", true, true, true, true, Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser, "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = new User();
        user.setId(1L);
        when(userRepository.findByUsername("user")).thenReturn(java.util.Optional.of(user));

        User actual = classUnderTest.getUser();

        assertEquals(1L, actual.getId());
    }

    @Test
    void givenNonExistentUser_whenLogin_thenCorrectExceptionIsThrown() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        CredentialsDto dto = new CredentialsDto("user", "password");

        assertThrows(HttpClientErrorException.class, () -> classUnderTest.login(dto));
    }

    @Test
    void givenUserExists_andPasswordsDoesNotMatch_whenLogin_thenCorrectExceptionIsThrown() {
        User user = new User();
        user.setPassword("password");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(encoder.matches("password", "password")).thenReturn(false);

        CredentialsDto dto = new CredentialsDto("user", "password");

        assertThrows(HttpClientErrorException.class, () -> classUnderTest.login(dto));
    }

    @Test
    void givenUserExists_andPasswordsMatch_whenLogin_thenCorrectValueIsReturned() {
        User user = new User();
        user.setId(1L);
        user.setPassword("password");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(encoder.matches("password", "password")).thenReturn(true);

        UserDto actual = classUnderTest.login(new CredentialsDto("user", "password"));

        assertEquals(user.getId(), actual.getId());
    }

    @Test
    void givenAlreadyExistingUsername_whenRegistering_thenCorrectExceptionIsThrown() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User()));

        SignUpDto dto = new SignUpDto("user", "password".toCharArray());
        assertThrows(HttpClientErrorException.class, () -> classUnderTest.register(dto));
    }

    @Test
    void whenRegistering_thenCorrectUserIsCreated() {
        when(userRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(encoder.encode("password")).thenReturn("encoded");

        when(userRoleRepository.findByRole(UserRole.USER)).thenReturn(new UserRoleEntity());
        User user = new User();
        user.setUsername("user");
        user.setId(1L);
        when(userRepository.save(user)).thenReturn(user);

        when(userRepository.save(any())).thenReturn(user);

        SignUpDto dto = new SignUpDto("user", "password".toCharArray());
        UserDto actual = classUnderTest.register(dto);

        assertEquals("user", actual.getUsername());
    }

    @Test
    void givenAdminUser_whenDeletingUser_thenCorrectExceptionIsThrown() {
        org.springframework.security.core.userdetails.User authUser = new org.springframework.security.core.userdetails.User("user", "password", true, true, true, true, Collections.emptyList());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUser, "password");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(new User(1L, "user", "password")));

        assertThrows(HttpClientErrorException.class, () -> classUnderTest.deleteUser());
    }

}
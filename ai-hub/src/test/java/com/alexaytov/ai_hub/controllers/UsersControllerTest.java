package com.alexaytov.ai_hub.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.alexaytov.ai_hub.model.dtos.CredentialsDto;
import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UpdateUserRequest;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.UserAuthenticationProvider;
import com.alexaytov.ai_hub.services.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsersControllerTest {

    private UserService service;
    private UserAuthenticationProvider authProvider;
    private AuditLogService auditLog;
    private UsersController classUnderTest;

    @BeforeEach
    void setup() {
        service = mock(UserService.class);
        authProvider = mock(UserAuthenticationProvider.class);
        auditLog = mock(AuditLogService.class);

        classUnderTest = new UsersController(service, authProvider, auditLog);
    }

    @Test
    void whenLogin_thenCorrectUserIsLoggedIn() {
        CredentialsDto dto = new CredentialsDto("username", "password");
        UserDto userDto = new UserDto();
        when(service.login(dto)).thenReturn(userDto);
        when(authProvider.createToken(userDto)).thenReturn("token");

        classUnderTest.login(dto);

        verify(service).login(dto);
        assertEquals("token", userDto.getToken());
    }

    @Test
    void whenRegisteringUser_thenCorrectUserIsRegistered() {
        SignUpDto signUp = new SignUpDto("username", "pass".toCharArray());
        UserDto user = new UserDto();
        user.setId(1L);
        when(service.register(signUp)).thenReturn(user);
        when(authProvider.createToken(user)).thenReturn("token");

        UserDto actual = classUnderTest.register(signUp).getBody();

        verify(auditLog).createUser(user.getId());
        verify(service).register(signUp);
        actual.getRoles().forEach(role -> assertEquals(UserRole.USER, role));
        assertEquals("token", actual.getToken());
    }

    @Test
    void whenChangingUsername_() {
        UpdateUserRequest request = new UpdateUserRequest();
        UserDto user = new UserDto();
        when(service.updateUser(request)).thenReturn(user);
        when(authProvider.createToken(user)).thenReturn("token");

        UserDto actual = classUnderTest.updateUser(request).getBody();

        verify(service).updateUser(request);
        verify(authProvider).createToken(user);
        assertEquals("token", actual.getToken());
    }

}
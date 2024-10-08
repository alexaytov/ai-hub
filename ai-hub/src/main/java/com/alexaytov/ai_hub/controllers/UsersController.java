package com.alexaytov.ai_hub.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.ai_hub.model.dtos.CredentialsDto;
import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UpdateUserRequest;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.entities.User;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.services.AuditLogService;
import com.alexaytov.ai_hub.services.UserAuthenticationProvider;
import com.alexaytov.ai_hub.services.UserService;

import jakarta.validation.Valid;

@RestController
public class UsersController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final AuditLogService auditLog;

    public UsersController(UserService userService, UserAuthenticationProvider userAuthenticationProvider, AuditLogService auditLog) {
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.auditLog = auditLog;
    }

    @PutMapping("/user")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        UserDto userDto = userService.updateUser(request);
        userDto.setToken(userAuthenticationProvider.createToken(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody @Valid CredentialsDto credentialsDto) {
        UserDto userDto = userService.login(credentialsDto);
        userDto.setToken(userAuthenticationProvider.createToken(userDto));
        return ResponseEntity.ok(userDto);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpDto user) {
        UserDto createdUser = userService.register(user);
        try {
            auditLog.createUser(createdUser.getId());
        } catch (Exception ex) {
            userService.deleteUser(createdUser.getId());
            throw ex;
        }

        createdUser.setRoles(List.of(UserRole.USER));
        createdUser.setToken(userAuthenticationProvider.createToken(createdUser));
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Void> deleteUser() {
        User user = userService.getUser();
        auditLog.postAuditLog("Deleting user with id " + user.getId());
        userService.deleteUser();
        auditLog.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

}

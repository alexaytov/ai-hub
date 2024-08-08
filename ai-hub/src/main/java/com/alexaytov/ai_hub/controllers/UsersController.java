package com.alexaytov.ai_hub.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.alexaytov.ai_hub.model.dtos.UpdateUserRequest;
import com.alexaytov.ai_hub.model.dtos.CredentialsDto;
import com.alexaytov.ai_hub.model.dtos.SignUpDto;
import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.enums.UserRole;
import com.alexaytov.ai_hub.services.UserAuthenticationProvider;
import com.alexaytov.ai_hub.services.UserService;

import jakarta.validation.Valid;

@RestController
public class UsersController {

    private final UserService userService;
    private final UserAuthenticationProvider userAuthenticationProvider;

    public UsersController(UserService userService, UserAuthenticationProvider userAuthenticationProvider) {
        this.userService = userService;
        this.userAuthenticationProvider = userAuthenticationProvider;
    }

    @PutMapping("/user")
    public ResponseEntity<UserDto> changeUsername(@Valid @RequestBody UpdateUserRequest request) {
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
        createdUser.setRoles(List.of(UserRole.USER));
        createdUser.setToken(userAuthenticationProvider.createToken(createdUser));
        return ResponseEntity.created(URI.create("/users/" + createdUser.getId())).body(createdUser);
    }

}

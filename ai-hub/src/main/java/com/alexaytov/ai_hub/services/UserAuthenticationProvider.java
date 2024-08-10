package com.alexaytov.ai_hub.services;

import org.springframework.security.core.Authentication;

import com.alexaytov.ai_hub.model.dtos.UserDto;

import jakarta.annotation.PostConstruct;

public interface UserAuthenticationProvider {

    String createToken(UserDto userDto);

    Authentication buildAuthentication(String token);
}

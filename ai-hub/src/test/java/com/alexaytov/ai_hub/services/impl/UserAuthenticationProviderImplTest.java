package com.alexaytov.ai_hub.services.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.model.enums.UserRole;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAuthenticationProviderImplTest {

    private static final String SECRET_KEY = "secret";
    private UserAuthenticationProviderImpl classUnderTest = new UserAuthenticationProviderImpl(SECRET_KEY);

    @Test
    void createToken() {
        UserDto dto = new UserDto();
        dto.setUsername("username");
        dto.setRoles(List.of(UserRole.USER));

        String token = classUnderTest.createToken(dto);

        Authentication auth = classUnderTest.buildAuthentication(token);
        UserDetails details = (UserDetails) auth.getPrincipal();

        assertEquals("username", details.getUsername());
        details.getAuthorities().forEach(a -> assertEquals("ROLE_USER", a.getAuthority()));
    }

}
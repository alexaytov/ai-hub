package com.alexaytov.ai_hub.services.impl;

import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import com.alexaytov.ai_hub.model.dtos.UserDto;
import com.alexaytov.ai_hub.services.UserAuthenticationProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import jakarta.annotation.PostConstruct;

@Component
public class UserAuthenticationProviderImpl implements UserAuthenticationProvider {


    private String secretKey;

    public UserAuthenticationProviderImpl(@Value("${security.jwt.token.secret-key}") String secretKey) {
        this.secretKey = secretKey;
    }

    @PostConstruct
    public void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Override
    public String createToken(UserDto userDto) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 3_600_000);

        return JWT.create()
            .withSubject(userDto.getUsername())
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .withClaim("roles", userDto.getRoles().stream().map(Enum::name).toList())
            .sign(Algorithm.HMAC256(secretKey));
    }

    @Override
    public Authentication buildAuthentication(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secretKey)).build();

        DecodedJWT decoded = verifier.verify(token);

        List<SimpleGrantedAuthority> roles = decoded.getClaim("roles")
            .asList(String.class).stream()
            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
            .toList();

        User user = new User(decoded.getSubject(), "", roles);

        return new UsernamePasswordAuthenticationToken(user, null, roles);
    }
}

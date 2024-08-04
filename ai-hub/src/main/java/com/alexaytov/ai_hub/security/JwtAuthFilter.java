package com.alexaytov.ai_hub.security;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alexaytov.ai_hub.services.UserAuthenticationProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserAuthenticationProvider provider;

    public JwtAuthFilter(UserAuthenticationProvider provider) {
        this.provider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");

        if (token != null) {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            } else {
                filterChain.doFilter(request, response);
                return;
            }

            try {
                filterChain.doFilter(request, response);
                SecurityContextHolder.getContext().setAuthentication(provider.validateToken(token));
            } catch (RuntimeException ex) {
                SecurityContextHolder.clearContext();
                throw ex;
            }
        }

        filterChain.doFilter(request, response);
    }
}

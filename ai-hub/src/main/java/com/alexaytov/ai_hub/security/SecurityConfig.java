package com.alexaytov.ai_hub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.alexaytov.ai_hub.services.UserAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserAuthenticationProvider authProvider;

    public SecurityConfig(UserAuthenticationProvider authProvider) {
        this.authProvider = authProvider;
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
            .addFilterBefore(new JwtAuthFilter(authProvider), BasicAuthenticationFilter.class)
            .sessionManagement(customizer -> customizer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(requests ->
                requests.requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
                    .anyRequest().authenticated()

            )
            .build();
    }
}

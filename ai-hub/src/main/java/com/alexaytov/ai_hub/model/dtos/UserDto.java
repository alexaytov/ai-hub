package com.alexaytov.ai_hub.model.dtos;

import java.util.ArrayList;
import java.util.List;

import com.alexaytov.ai_hub.model.enums.UserRole;

public class UserDto {
    private Long id;
    private String username;
    private String token;
    private List<UserRole> roles;

    public UserDto() {
        roles = new ArrayList<>();
    }

    public List<UserRole> getRoles() {
        return roles;
    }

    public void setRoles(List<UserRole> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

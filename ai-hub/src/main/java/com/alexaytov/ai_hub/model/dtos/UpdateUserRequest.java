package com.alexaytov.ai_hub.model.dtos;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;

public class UpdateUserRequest {

    @Length(max = 40)
    private String username;

    @Length(max = 40)
    private String password;

    public @Length(max = 40) String getUsername() {
        return username;
    }

    public void setUsername(@Length(max = 40) String username) {
        this.username = username;
    }

    public @Length(max = 40) String getPassword() {
        return password;
    }

    public void setPassword(@Length(max = 40) String password) {
        this.password = password;
    }
}
